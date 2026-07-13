import os
import logging
from flask import Flask, request, jsonify
from flask_cors import CORS
from dotenv import load_dotenv

from models.waste_classifier import WasteClassifier
from models.recommendation_engine import RecommendationEngine
from utils.image_utils import decode_base64_image

load_dotenv()

# ── Logging ──────────────────────────────────────────────────────────────────
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s [%(levelname)s] %(name)s: %(message)s',
)
logger = logging.getLogger(__name__)

# ── App ───────────────────────────────────────────────────────────────────────
app = Flask(__name__)
CORS(app, origins=os.getenv('ALLOWED_ORIGINS', '*').split(','))

# ── Model Initialisation (lazy singleton) ────────────────────────────────────
_classifier = None
_recommender = None


def get_classifier() -> WasteClassifier:
    global _classifier
    if _classifier is None:
        model_path = os.getenv('MODEL_PATH', 'models/waste_classifier.pth')
        _classifier = WasteClassifier(model_path=model_path)
    return _classifier


def get_recommender() -> RecommendationEngine:
    global _recommender
    if _recommender is None:
        _recommender = RecommendationEngine()
    return _recommender


# ── Routes ────────────────────────────────────────────────────────────────────

@app.route('/health', methods=['GET'])
def health():
    return jsonify({'status': 'ok', 'service': 'vasundhara-ai'}), 200


@app.route('/predict', methods=['POST'])
def predict():
    """Waste classification endpoint."""
    try:
        data = request.get_json(force=True)
        if not data or 'image' not in data:
            return jsonify({'error': 'image field required'}), 400

        image = decode_base64_image(data['image'])
        result = get_classifier().predict(image)

        threshold = float(os.getenv('CONFIDENCE_THRESHOLD', 0.5))
        if result['confidence'] < threshold:
            result['category'] = 'Unknown'
            result['warning'] = f'Low confidence ({result["confidence"]:.0%}). Please retake photo.'

        return jsonify(result), 200

    except Exception as e:
        logger.exception('Prediction error')
        return jsonify({'error': str(e)}), 500


@app.route('/recommend', methods=['POST'])
def recommend():
    """Personalised recommendation endpoint."""
    try:
        data = request.get_json(force=True)
        context = data.get('context', {})
        recommendations = get_recommender().get_recommendations(context)
        return jsonify({'recommendations': recommendations}), 200
    except Exception as e:
        logger.exception('Recommendation error')
        return jsonify({'error': str(e)}), 500


@app.route('/chat', methods=['POST'])
def chat():
    """Conversational AI endpoint."""
    try:
        data = request.get_json(force=True)
        message = data.get('message', '')
        if not message:
            return jsonify({'error': 'message required'}), 400

        reply = get_recommender().chat_reply(message)
        return jsonify({'reply': reply}), 200
    except Exception as e:
        logger.exception('Chat error')
        return jsonify({'error': str(e)}), 500


# ── Entry Point ───────────────────────────────────────────────────────────────
if __name__ == '__main__':
    port = int(os.getenv('PORT', 8000))
    debug = os.getenv('FLASK_ENV') != 'production'
    logger.info(f'🤖 Vasundhara AI service starting on port {port}')
    app.run(host='0.0.0.0', port=port, debug=debug)
