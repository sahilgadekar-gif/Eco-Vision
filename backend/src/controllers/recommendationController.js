const axios = require('axios');
const CarbonLog = require('../models/CarbonLog');
const User = require('../models/User');
const { success, error } = require('../utils/apiResponse');
const logger = require('../utils/logger');

// Static rule-based recommendations (fallback when LLM unavailable)
const STATIC_RECOMMENDATIONS = {
  travel: [
    'Switch to public transport 3 days/week — saves up to 30% travel emissions.',
    'Consider carpooling for daily commutes.',
    'For trips under 5km, cycling saves ~1kg CO₂ per trip.',
  ],
  electricity: [
    'Replace incandescent bulbs with LEDs — saves 75% energy per bulb.',
    'Unplug devices on standby — phantom loads account for 10% of home electricity.',
    'Set AC to 24°C instead of 20°C — saves 8% energy per degree.',
  ],
  food: [
    'Replace one beef meal/week with plant-based — saves 3.3kg CO₂.',
    'Buy local and seasonal produce to cut transport emissions.',
    'Reduce food waste — 30% of food produced globally is wasted.',
  ],
  general: [
    'Your eco score improves with every logged activity.',
    'Set a weekly carbon budget and track against it.',
    'Share your progress to inspire others in your network.',
  ],
};

// GET /api/recommendations
const getRecommendations = async (req, res, next) => {
  try {
    const userId = req.user._id;

    // Gather user context
    const [user, topActivities] = await Promise.all([
      User.findById(userId),
      CarbonLog.aggregate([
        { $match: { userId } },
        { $group: { _id: '$activity', total: { $sum: '$co2Value' } } },
        { $sort: { total: -1 } },
        { $limit: 3 },
      ]),
    ]);

    const topActivity = topActivities[0]?._id || 'general';
    const userContext = {
      ecoScore: user.ecoScore,
      rank: user.rank,
      topEmissionSource: topActivity,
      totalCO2: topActivities.reduce((s, a) => s + a.total, 0).toFixed(2),
    };

    // Try LLM service
    let recommendations = [];
    try {
      const prompt = buildPrompt(userContext, topActivities);
      const llmResponse = await axios.post(
        `${process.env.AI_SERVICE_URL}/recommend`,
        { prompt, context: userContext },
        { timeout: 15000 }
      );
      recommendations = llmResponse.data.recommendations || [];
    } catch (llmErr) {
      logger.warn(`LLM service unavailable: ${llmErr.message}. Using static recommendations.`);
      recommendations = [
        ...STATIC_RECOMMENDATIONS[topActivity] || STATIC_RECOMMENDATIONS.general,
        ...STATIC_RECOMMENDATIONS.general,
      ].slice(0, 5);
    }

    return success(res, {
      recommendations,
      userContext,
      topActivities,
    });
  } catch (err) {
    next(err);
  }
};

// POST /api/recommendations/chat
const chatRecommendation = async (req, res, next) => {
  try {
    const { message } = req.body;
    if (!message) return error(res, 'Message is required.', 400);

    let reply;
    try {
      const response = await axios.post(
        `${process.env.AI_SERVICE_URL}/chat`,
        { message, userId: req.user._id.toString() },
        { timeout: 20000 }
      );
      reply = response.data.reply;
    } catch (aiErr) {
      logger.warn(`AI chat unavailable: ${aiErr.message}`);
      reply = getStaticReply(message);
    }

    return success(res, { reply, timestamp: new Date() });
  } catch (err) {
    next(err);
  }
};

const buildPrompt = (context, activities) => {
  const actList = activities.map((a) => `${a._id}: ${a.total.toFixed(1)}kg CO₂`).join(', ');
  return `User eco score: ${context.ecoScore}/100. Top emissions: ${actList}. 
  Provide 5 specific, actionable eco-friendly recommendations to reduce their carbon footprint. 
  Be concise, practical, and encouraging.`;
};

const getStaticReply = (message) => {
  const lower = message.toLowerCase();
  if (lower.includes('travel') || lower.includes('car') || lower.includes('transport'))
    return STATIC_RECOMMENDATIONS.travel[Math.floor(Math.random() * STATIC_RECOMMENDATIONS.travel.length)];
  if (lower.includes('electric') || lower.includes('energy') || lower.includes('power'))
    return STATIC_RECOMMENDATIONS.electricity[Math.floor(Math.random() * STATIC_RECOMMENDATIONS.electricity.length)];
  if (lower.includes('food') || lower.includes('eat') || lower.includes('diet'))
    return STATIC_RECOMMENDATIONS.food[Math.floor(Math.random() * STATIC_RECOMMENDATIONS.food.length)];
  return STATIC_RECOMMENDATIONS.general[Math.floor(Math.random() * STATIC_RECOMMENDATIONS.general.length)];
};

module.exports = { getRecommendations, chatRecommendation };
