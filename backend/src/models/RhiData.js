const mongoose = require('mongoose');

const rhiSchema = new mongoose.Schema({
  userId:           { type: mongoose.Schema.Types.ObjectId, ref: 'User', required: true, index: true },
  score:            { type: Number, required: true, min: 0, max: 100 },
  status:           { type: String, enum: ['HEALTHY', 'MODERATE', 'CRITICAL'] },
  waterPollution:   { type: Number, required: true },
  wasteDumped:      { type: Number, required: true },
  industrialImpact: { type: Number, required: true },
  carbonInRiver:    { type: Number, required: true },
  location:         { type: String },
  suggestions:      [String],
}, { timestamps: true });

rhiSchema.index({ userId: 1, createdAt: -1 });
module.exports = mongoose.model('RhiData', rhiSchema);
