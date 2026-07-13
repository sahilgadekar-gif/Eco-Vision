const User = require('../models/User');
const WasteLog = require('../models/WasteLog');
const CarbonLog = require('../models/CarbonLog');
const { success, error } = require('../utils/apiResponse');

// GET /api/user/profile
const getProfile = async (req, res, next) => {
  try {
    const user = await User.findById(req.user._id);
    if (!user) return error(res, 'User not found.', 404);

    const [wasteCount, carbonCount] = await Promise.all([
      WasteLog.countDocuments({ userId: req.user._id }),
      CarbonLog.countDocuments({ userId: req.user._id }),
    ]);

    return success(res, { ...user.toJSON(), wasteCount, carbonCount });
  } catch (err) {
    next(err);
  }
};

// PUT /api/user/profile
const updateProfile = async (req, res, next) => {
  try {
    const { name, avatarUrl } = req.body;
    const user = await User.findByIdAndUpdate(
      req.user._id,
      { name, avatarUrl },
      { new: true, runValidators: true }
    );
    return success(res, user, 'Profile updated');
  } catch (err) {
    next(err);
  }
};

// GET /api/user/dashboard
const getDashboard = async (req, res, next) => {
  try {
    const userId = req.user._id;
    const sevenDaysAgo = new Date(Date.now() - 7 * 24 * 60 * 60 * 1000);

    const [user, weeklyCarbon, recentWaste] = await Promise.all([
      User.findById(userId),
      CarbonLog.aggregate([
        { $match: { userId, date: { $gte: sevenDaysAgo } } },
        {
          $group: {
            _id: { $dateToString: { format: '%a', date: '$date' } },
            total: { $sum: '$co2Value' },
          },
        },
        { $sort: { '_id': 1 } },
      ]),
      WasteLog.find({ userId }).sort({ createdAt: -1 }).limit(5),
    ]);

    return success(res, {
      co2Saved: user.totalCO2Saved,
      ecoScore: user.ecoScore,
      streakDays: user.streakDays,
      rank: user.rank,
      points: user.points,
      weeklyTrend: weeklyCarbon,
      recentWaste,
    });
  } catch (err) {
    next(err);
  }
};

module.exports = { getProfile, updateProfile, getDashboard };
