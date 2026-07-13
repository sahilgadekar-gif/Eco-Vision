const RhiData = require('../models/RhiData');
const { success, error } = require('../utils/apiResponse');

const SUGGESTIONS = {
  CRITICAL: ['Immediately stop industrial waste dumping near rivers', 'Organize emergency river clean-up drives', 'Report illegal dumping to local authorities', 'Avoid using chemical fertilizers near water bodies'],
  MODERATE: ['Reduce plastic disposal near water bodies', 'Avoid chemical dumping in drainage systems', 'Support local river clean-up drives', 'Use eco-friendly detergents'],
  HEALTHY:  ['Maintain current practices', 'Encourage community awareness', 'Monitor regularly to sustain health', 'Plant trees along riverbanks'],
};

// POST /api/rhi/calculate
exports.calculateRhi = async (req, res, next) => {
  try {
    const { waterPollution, wasteDumped, industrialImpact, carbonInRiver } = req.body;
    const score = Math.max(0, Math.min(100,
      100 - (waterPollution * 0.3 + wasteDumped * 0.25 + industrialImpact * 0.25 + carbonInRiver * 0.2)
    ));
    const status = score >= 70 ? 'HEALTHY' : score >= 40 ? 'MODERATE' : 'CRITICAL';

    const log = await RhiData.create({
      userId: req.user._id, score, status,
      waterPollution, wasteDumped, industrialImpact, carbonInRiver,
      suggestions: SUGGESTIONS[status],
    });

    const trend = await RhiData.aggregate([
      { $match: { userId: req.user._id } },
      { $group: { _id: { $dateToString: { format: '%b', date: '$createdAt' } }, total: { $avg: '$score' } } },
      { $sort: { _id: 1 } }, { $limit: 6 },
    ]);

    return success(res, { score, status, suggestions: SUGGESTIONS[status], trend, logId: log._id });
  } catch (err) { next(err); }
};

// GET /api/rhi/latest
exports.getLatestRhi = async (req, res, next) => {
  try {
    const latest = await RhiData.findOne({ userId: req.user._id }).sort({ createdAt: -1 });
    if (!latest) return error(res, 'No RHI data found.', 404);
    return success(res, latest);
  } catch (err) { next(err); }
};
