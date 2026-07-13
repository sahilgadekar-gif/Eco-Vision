import os
import logging
import random

logger = logging.getLogger(__name__)

# Static knowledge base for fallback
ECO_KNOWLEDGE = {
    'travel': [
        "Switch to public transport 3 days/week — reduces travel emissions by up to 30%.",
        "Cycling for trips under 5km saves ~1kg CO₂ per trip and improves health.",
        "Carpooling with just one other person halves your per-trip emissions.",
        "Electric vehicles produce 50-70% less lifetime CO₂ than petrol cars.",
        "Working from home 2 days/week can save 400kg CO₂ annually.",
    ],
    'electricity': [
        "LED bulbs use 75% less energy than incandescent — replace all bulbs today.",
        "Setting your thermostat 1°C lower saves ~10% on heating bills.",
        "Unplugging devices on standby eliminates phantom loads (up to 10% of your bill).",
        "A smart power strip can automatically cut power to idle devices.",
        "Solar panels can offset 80% of home electricity and pay back in 6-8 years.",
    ],
    'food': [
        "Replacing one beef meal/week with plant-based saves 3.3kg CO₂ — like not driving for 2 days.",
        "Buying local and seasonal produce cuts transport emissions by up to 50%.",
        "Reducing food waste by 50% saves ~1 tonne CO₂ per household annually.",
        "A plant-rich diet is the single biggest individual climate action.",
        "Growing herbs at home reduces packaging and transport emissions.",
    ],
    'shopping': [
        "Buying second-hand clothing saves 70% of the carbon vs new production.",
        "Repairing electronics instead of replacing saves 70kg CO₂ per device.",
        "Choosing products with minimal packaging reduces waste stream emissions.",
        "Renting or borrowing rarely-used items avoids unnecessary production.",
    ],
    'general': [
        "Your eco score improves with every logged activity — keep the streak going!",
        "Set a weekly carbon budget and track against it for best results.",
        "Share your progress to inspire others — social influence is powerful.",
        "Small consistent actions compound: 1% better daily = 37x better in a year.",
        "Vasundhara tip: combine activities — e.g., cycle to the farmers market.",
    ],
}


class RecommendationEngine:
    """LLM-based recommendation engine with static fallback."""

    def __init__(self):
        self.llm = None
        self._try_load_llm()

    def _try_load_llm(self):
        """Attempt to load a local LLM. Silently falls back if unavailable."""
        try:
            from transformers import pipeline
            hf_token = os.getenv('HF_TOKEN')
            model_name = os.getenv('LLM_MODEL', 'microsoft/phi-2')
            logger.info(f'Loading LLM: {model_name}')
            self.llm = pipeline(
                'text-generation',
                model=model_name,
                token=hf_token,
                device_map='auto',
                max_new_tokens=256,
            )
            logger.info('LLM loaded successfully')
        except Exception as e:
            logger.warning(f'LLM not available ({e}). Using static recommendations.')
            self.llm = None

    def get_recommendations(self, context: dict) -> list[str]:
        """Generate personalised recommendations based on user context."""
        top_source = context.get('topEmissionSource', 'general')

        if self.llm:
            return self._llm_recommendations(context)
        return self._static_recommendations(top_source)

    def _llm_recommendations(self, context: dict) -> list[str]:
        try:
            prompt = self._build_prompt(context)
            output = self.llm(prompt, do_sample=True, temperature=0.7)[0]['generated_text']
            # Parse numbered list from output
            lines = [l.strip() for l in output.split('\n') if l.strip() and l[0].isdigit()]
            return lines[:5] if lines else self._static_recommendations(context.get('topEmissionSource', 'general'))
        except Exception as e:
            logger.error(f'LLM inference error: {e}')
            return self._static_recommendations(context.get('topEmissionSource', 'general'))

    def _static_recommendations(self, top_source: str) -> list[str]:
        source_tips = ECO_KNOWLEDGE.get(top_source, ECO_KNOWLEDGE['general'])
        general_tips = ECO_KNOWLEDGE['general']
        combined = source_tips + general_tips
        random.shuffle(combined)
        return combined[:5]

    def chat_reply(self, message: str) -> str:
        """Generate a conversational eco-advice reply."""
        if self.llm:
            try:
                prompt = f"You are Vasundhara AI, an eco-friendly assistant. Answer concisely: {message}"
                output = self.llm(prompt, max_new_tokens=150, do_sample=True, temperature=0.7)
                reply = output[0]['generated_text'].replace(prompt, '').strip()
                return reply if reply else self._keyword_reply(message)
            except Exception as e:
                logger.error(f'LLM chat error: {e}')
        return self._keyword_reply(message)

    def _keyword_reply(self, message: str) -> str:
        lower = message.lower()
        for key in ECO_KNOWLEDGE:
            if key in lower:
                return random.choice(ECO_KNOWLEDGE[key])
        return random.choice(ECO_KNOWLEDGE['general'])

    def _build_prompt(self, context: dict) -> str:
        return (
            f"You are an environmental AI assistant. "
            f"User eco score: {context.get('ecoScore', 0)}/100. "
            f"Top emission source: {context.get('topEmissionSource', 'unknown')}. "
            f"Total CO₂: {context.get('totalCO2', 0)}kg. "
            f"Provide 5 numbered, specific, actionable recommendations to reduce their carbon footprint:\n"
        )
