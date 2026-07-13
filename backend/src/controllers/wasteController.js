const axios = require('axios');
const WasteLog = require('../models/WasteLog');
const User = require('../models/User');
const { success, error, paginated } = require('../utils/apiResponse');
const logger = require('../utils/logger');

const CO2_SAVINGS = {
  Plastic: 0.12, Glass: 0.31, Metal: 0.42, Paper: 0.17,
  Organic: 0.05, Electronic: 0.85, Hazardous: 0.0,
  Textile: 0.22, Mixed: 0.08, Unknown: 0.0,
};

const POINTS_MAP = {
  Plastic: 10, Glass: 15, Metal: 20, Paper: 8,
  Organic: 5, Electronic: 30, Hazardous: 25,
  Textile: 12, Mixed: 5, Unknown: 2,
};

// POST /api/waste/detect
const detectWaste = async (req, res, next) => {
  try {
    const { imageBase64 } = req.body;
    if (!imageBase64) return error(res, 'Image data is required.', 400);

    // Call Python AI service
    let aiResult;
    try {
      const response = await axios.post(
        `${process.env.AI_SERVICE_URL}/predict`,
        { image: imageBase64 },
        { timeout: 30000 }
      );
      aiResult = response.data;
    } catch (aiErr) {
      logger.warn(`AI service unavailable: ${aiErr.message}. Using fallback.`);
      // Graceful fallback
      aiResult = {
        category: 'Unknown',
        confidence: 0.0,
        disposal_steps: ['Please consult local waste guidelines.'],
        recycling_tips: ['Sort waste before disposal.'],
      };
    }

    const category = aiResult.category || 'Unknown';
    const co2Impact = CO2_SAVINGS[category] || 0;
    const pointsEarned = POINTS_MAP[category] || 2;

    // Persist log
    const wasteLog = await WasteLog.create({
      userId: req.user._id,
      category,
      confidence: aiResult.confidence,
      disposalMethod: aiResult.disposal_steps?.join('; '),
      co2Impact,
      pointsEarned,
    });

    // Update user stats
    await User.findByIdAndUpdate(req.user._id, {
      $inc: {
        totalCO2Saved: co2Impact,
        points: pointsEarned,
        ecoScore: Math.min(1, Math.floor(pointsEarned / 10)),
      },
    });

    return success(res, {
      logId: wasteLog._id,
      category,
      confidence: aiResult.confidence,
      disposalSteps: aiResult.disposal_steps || [],
      recyclingTips: aiResult.recycling_tips || [],
      ecoImpact: `Recycling this saves ${co2Impact} kg CO₂`,
      pointsEarned,
    }, 'Waste detected successfully');
  } catch (err) {
    next(err);
  }
};

// GET /api/waste/history
const getWasteHistory = async (req, res, next) => {
  try {
    const page = parseInt(req.query.page) || 1;
    const limit = parseInt(req.query.limit) || 20;
    const skip = (page - 1) * limit;

    const [logs, total] = await Promise.all([
      WasteLog.find({ userId: req.user._id })
        .sort({ createdAt: -1 })
        .skip(skip)
        .limit(limit),
      WasteLog.countDocuments({ userId: req.user._id }),
    ]);

    return paginated(res, logs, total, page, limit);
  } catch (err) {
    next(err);
  }
};

module.exports = { detectWaste, getWasteHistory };
