const mongoose = require('mongoose');

const wasteLogSchema = new mongoose.Schema(
  {
    userId: {
      type: mongoose.Schema.Types.ObjectId,
      ref: 'User',
      required: true,
      index: true,
    },
    imageUrl: { type: String, default: null },
    imageBase64: { type: String, select: false }, // stored only if no cloud storage
    category: {
      type: String,
      required: true,
      enum: [
        'Plastic', 'Glass', 'Metal', 'Paper', 'Organic',
        'Electronic', 'Hazardous', 'Textile', 'Mixed', 'Unknown',
      ],
    },
    subCategory: { type: String },
    confidence: { type: Number, min: 0, max: 1 },
    disposalMethod: { type: String },
    co2Impact: { type: Number, default: 0 }, // kg CO₂ saved by recycling
    pointsEarned: { type: Number, default: 10 },
    location: {
      type: { type: String, enum: ['Point'], default: 'Point' },
      coordinates: { type: [Number], default: [0, 0] },
    },
  },
  { timestamps: true }
);

wasteLogSchema.index({ location: '2dsphere' });
wasteLogSchema.index({ userId: 1, createdAt: -1 });

module.exports = mongoose.model('WasteLog', wasteLogSchema);
