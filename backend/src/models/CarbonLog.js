const mongoose = require('mongoose');

const carbonLogSchema = new mongoose.Schema(
  {
    userId: {
      type: mongoose.Schema.Types.ObjectId,
      ref: 'User',
      required: true,
      index: true,
    },
    activity: {
      type: String,
      required: true,
      enum: ['travel', 'electricity', 'food', 'shopping', 'waste', 'other'],
    },
    subActivity: { type: String }, // e.g. "car", "flight", "bus"
    quantity: { type: Number, required: true }, // km, kWh, servings, etc.
    unit: { type: String, required: true },     // "km", "kWh", "servings"
    co2Value: { type: Number, required: true }, // kg CO₂
    emissionFactor: { type: Number },           // kg CO₂ per unit
    notes: { type: String, maxlength: 200 },
    date: { type: Date, default: Date.now },
  },
  { timestamps: true }
);

carbonLogSchema.index({ userId: 1, date: -1 });
carbonLogSchema.index({ userId: 1, activity: 1 });

module.exports = mongoose.model('CarbonLog', carbonLogSchema);
