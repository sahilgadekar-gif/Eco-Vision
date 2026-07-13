import base64
import io
from PIL import Image
import numpy as np


def decode_base64_image(base64_string: str) -> Image.Image:
    """Decode a base64 string to a PIL Image."""
    # Strip data URI prefix if present
    if ',' in base64_string:
        base64_string = base64_string.split(',')[1]
    image_bytes = base64.b64decode(base64_string)
    image = Image.open(io.BytesIO(image_bytes)).convert('RGB')
    return image


def preprocess_image(image: Image.Image, size: tuple = (224, 224)) -> np.ndarray:
    """Resize and normalize image for model input."""
    image = image.resize(size)
    arr = np.array(image, dtype=np.float32) / 255.0
    mean = np.array([0.485, 0.456, 0.406])
    std = np.array([0.229, 0.224, 0.225])
    arr = (arr - mean) / std
    return arr
