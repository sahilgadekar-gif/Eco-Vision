import os
import torch
import torch.nn as nn
from torchvision import models, transforms
from PIL import Image
import numpy as np
import logging

logger = logging.getLogger(__name__)

WASTE_CLASSES = [
    'Plastic', 'Glass', 'Metal', 'Paper',
    'Organic', 'Electronic', 'Hazardous',
    'Textile', 'Mixed', 'Unknown'
]

DISPOSAL_GUIDE = {
    'Plastic': {
        'steps': [
            'Rinse the item thoroughly to remove residue',
            'Remove caps and labels if possible',
            'Crush to save space in recycling bin',
            'Place in blue/yellow recycling bin',
        ],
        'tips': [
            'PET (#1) and HDPE (#2) plastics are most widely recycled',
            'Avoid mixing plastic types in the same bag',
            'Black plastic is often not recyclable — check locally',
        ],
    },
    'Glass': {
        'steps': [
            'Rinse the glass container',
            'Remove metal lids (recycle separately)',
            'Do not break — whole glass is easier to sort',
            'Place in glass recycling bin or bottle bank',
        ],
        'tips': [
            'Glass can be recycled infinitely without quality loss',
            'Pyrex and window glass are NOT recyclable with bottles',
            'Coloured glass should be sorted by colour where required',
        ],
    },
    'Metal': {
        'steps': [
            'Rinse cans and tins',
            'Crush aluminium cans to save space',
            'Remove paper labels if possible',
            'Place in metal/mixed recycling bin',
        ],
        'tips': [
            'Aluminium recycling saves 95% of the energy vs new production',
            'Steel and aluminium are both recyclable',
            'Aerosol cans are recyclable when completely empty',
        ],
    },
    'Paper': {
        'steps': [
            'Keep paper dry — wet paper cannot be recycled',
            'Remove plastic windows from envelopes',
            'Flatten cardboard boxes',
            'Place in paper recycling bin',
        ],
        'tips': [
            'Shredded paper should be bagged before recycling',
            'Greasy pizza boxes go to compost, not paper recycling',
            'Receipts (thermal paper) are not recyclable',
        ],
    },
    'Organic': {
        'steps': [
            'Separate from non-organic waste',
            'Place in compost bin or green waste bin',
            'Avoid adding meat/dairy to home compost',
            'Use a sealed bin to prevent pests',
        ],
        'tips': [
            'Composting reduces methane emissions from landfill',
            'Finished compost enriches soil naturally',
            'Bokashi systems can handle meat and dairy',
        ],
    },
    'Electronic': {
        'steps': [
            'Do NOT place in regular bins — hazardous materials',
            'Find your nearest e-waste collection point',
            'Wipe personal data before disposal',
            'Check manufacturer take-back programs',
        ],
        'tips': [
            'E-waste contains valuable metals like gold and copper',
            'Many retailers offer free e-waste drop-off',
            'Donate working electronics to extend their life',
        ],
    },
    'Hazardous': {
        'steps': [
            'Never pour down drains or in regular bins',
            'Store safely in original container',
            'Take to hazardous waste collection facility',
            'Check local council for collection dates',
        ],
        'tips': [
            'Batteries, paint, and chemicals are hazardous',
            'Many pharmacies accept old medicines',
            'Car batteries can be returned to auto shops',
        ],
    },
    'Textile': {
        'steps': [
            'Clean and dry before donating or recycling',
            'Donate wearable items to charity shops',
            'Use textile recycling bins for worn items',
            'Check brand take-back schemes',
        ],
        'tips': [
            'Only 15% of textiles are currently recycled globally',
            'Natural fibres can be composted if untreated',
            'Repair before replacing to extend garment life',
        ],
    },
    'Mixed': {
        'steps': [
            'Try to separate components if possible',
            'Identify the dominant material type',
            'Check local mixed waste guidelines',
            'When in doubt, use general waste bin',
        ],
        'tips': [
            'Sorting waste at source improves recycling rates',
            'Many councils offer sorting guides online',
        ],
    },
    'Unknown': {
        'steps': ['Consult your local waste authority guidelines'],
        'tips': ['When unsure, check the recycling symbol on the item'],
    },
}


class WasteClassifier:
    """ResNet50-based waste classification model."""

    def __init__(self, model_path: str = None, num_classes: int = len(WASTE_CLASSES)):
        self.device = torch.device('cuda' if torch.cuda.is_available() else 'cpu')
        self.num_classes = num_classes
        self.model = self._build_model(num_classes)
        self.transform = self._build_transform()

        if model_path and os.path.exists(model_path):
            self._load_weights(model_path)
            logger.info(f'Loaded model weights from {model_path}')
        else:
            logger.warning('No model weights found. Using ImageNet pretrained features (demo mode).')

        self.model.eval()

    def _build_model(self, num_classes: int) -> nn.Module:
        model = models.resnet50(weights=models.ResNet50_Weights.IMAGENET1K_V2)
        # Replace final FC layer for waste classification
        in_features = model.fc.in_features
        model.fc = nn.Sequential(
            nn.Dropout(0.4),
            nn.Linear(in_features, 512),
            nn.ReLU(),
            nn.Dropout(0.3),
            nn.Linear(512, num_classes),
        )
        return model.to(self.device)

    def _build_transform(self) -> transforms.Compose:
        return transforms.Compose([
            transforms.Resize(256),
            transforms.CenterCrop(224),
            transforms.ToTensor(),
            transforms.Normalize(mean=[0.485, 0.456, 0.406], std=[0.229, 0.224, 0.225]),
        ])

    def _load_weights(self, path: str):
        state_dict = torch.load(path, map_location=self.device)
        self.model.load_state_dict(state_dict)

    def predict(self, image: Image.Image) -> dict:
        """Run inference on a PIL image."""
        tensor = self.transform(image).unsqueeze(0).to(self.device)

        with torch.no_grad():
            logits = self.model(tensor)
            probs = torch.softmax(logits, dim=1).squeeze()

        confidence, idx = probs.max(0)
        category = WASTE_CLASSES[idx.item()]
        confidence_val = confidence.item()

        # Top-3 predictions
        top3_probs, top3_idx = probs.topk(3)
        top3 = [
            {'category': WASTE_CLASSES[i.item()], 'confidence': round(p.item(), 4)}
            for p, i in zip(top3_probs, top3_idx)
        ]

        guide = DISPOSAL_GUIDE.get(category, DISPOSAL_GUIDE['Unknown'])

        return {
            'category': category,
            'confidence': round(confidence_val, 4),
            'top3': top3,
            'disposal_steps': guide['steps'],
            'recycling_tips': guide['tips'],
        }
