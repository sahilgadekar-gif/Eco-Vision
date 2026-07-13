const CarbonLog = require('../models/CarbonLog');
const User = require('../models/User');
const { success, error, paginated } = require('../utils/apiResponse');

// Enhanced emission factors (kg CO₂ per unit)
const EMISSION_FACTORS = {
  travel: { car: 0.21, bus: 0.089, train: 0.041, flight: 0.255, bike: 0.0, walk: 0.0 },
  electricity: { grid: 0.82, solar: 0.04, wind: 0.011 },
  food: { beef: 27.0, chicken: 6.9, fish: 6.1, vegetarian: 2.0, vegan: 1.5 },
  shopping: { clothing: 10.0, electronics: 70.0, furniture: 30.0, general: 2.0 },
  waste: { landfill: 0.5, recycled: 0.1, composted: 0.05 },
  digital: { streaming: 0.05, gaming: 0.08, social: 0.03, work: 0.04 }
};

// Rank titles based on percentile
const RANK_TITLES = [
  { min: 0, max: 20, title: "Carbon Novice", icon: "🌱" },
  { min: 20, max: 40, title: "Eco Learner", icon: "🌿" },
  { min: 40, max: 60, title: "Green Warrior", icon: "🍃" },
  { min: 60, max: 80, title: "Climate Champion", icon: "🌍" },
  { min: 80, max: 95, title: "Carbon Master", icon: "⭐" },
  { min: 95, max: 100, title: "Eco Legend", icon: "👑" }
];

// Badge definitions
const CARBON_BADGES = [
  { id: 'first_step', title: 'First Step', description: 'Logged your first carbon footprint', icon: '👣' },
  { id: 'week_warrior', title: 'Week Warrior', description: 'Tracked carbon for 7 days straight', icon: '📅' },
  { id: 'low_impact', title: 'Low Impact', description: 'Kept daily footprint under 5kg CO₂', icon: '🌟' },
  { id: 'transport_hero', title: 'Transport Hero', description: 'Used green transport for a week', icon: '🚴' },
  { id: 'energy_saver', title: 'Energy Saver', description: 'Reduced electricity by 20%', icon: '💡' },
  { id: 'waste_reducer', title: 'Waste Reducer', description: 'Halved your waste production', icon: '♻️' }
];

const getEmissionFactor = (activity, subActivity) => {
  const factors = EMISSION_FACTORS[activity];
  if (!factors) return 0.5; // default
  return factors[subActivity] || Object.values(factors)[0];
};

// POST /api/carbon/calculate
const calculateCarbon = async (req, res, next) => {
  try {
    const { activity, subActivity, quantity, unit, notes, date } = req.body;

    const emissionFactor = getEmissionFactor(activity, subActivity);
    const co2Value = parseFloat((quantity * emissionFactor).toFixed(4));

    const log = await CarbonLog.create({
      userId: req.user._id,
      activity,
      subActivity,
      quantity,
      unit,
      co2Value,
      emissionFactor,
      notes,
      date: date ? new Date(date) : new Date(),
    });

    // Update user eco score (inverse: less CO₂ = higher score)
    const reductionPoints = Math.max(0, Math.floor(10 - co2Value));
    if (reductionPoints > 0) {
      await User.findByIdAndUpdate(req.user._id, {
        $inc: { points: reductionPoints },
      });
    }

    return success(res, {
      logId: log._id,
      co2Value,
      emissionFactor,
      activity,
      subActivity,
      quantity,
      unit,
      equivalents: {
        treeDays: parseFloat((co2Value / 0.022).toFixed(1)),
        drivingKm: parseFloat((co2Value / 0.21).toFixed(1)),
        phoneCharges: parseFloat((co2Value / 0.008).toFixed(0)),
      },
    }, 'Carbon calculated');
  } catch (err) {
    next(err);
  }
};

// GET /api/carbon/history
const getCarbonHistory = async (req, res, next) => {
  try {
    const { period = '30d', activity } = req.query;
    const page = parseInt(req.query.page) || 1;
    const limit = parseInt(req.query.limit) || 30;

    const days = period === '7d' ? 7 : period === '90d' ? 90 : 30;
    const since = new Date(Date.now() - days * 24 * 60 * 60 * 1000);

    const filter = { userId: req.user._id, date: { $gte: since } };
    if (activity) filter.activity = activity;

    const [logs, total, aggregated] = await Promise.all([
      CarbonLog.find(filter).sort({ date: -1 }).skip((page - 1) * limit).limit(limit),
      CarbonLog.countDocuments(filter),
      CarbonLog.aggregate([
        { $match: filter },
        {
          $group: {
            _id: { $dateToString: { format: '%Y-%m-%d', date: '$date' } },
            total: { $sum: '$co2Value' },
            count: { $sum: 1 },
          },
        },
        { $sort: { _id: 1 } },
      ]),
    ]);

    return success(res, { logs, total, trend: aggregated, page, limit });
  } catch (err) {
    next(err);
  }
};

// GET /api/carbon/summary
const getCarbonSummary = async (req, res, next) => {
  try {
    const userId = req.user._id;
    const summary = await CarbonLog.aggregate([
      { $match: { userId } },
      {
        $group: {
          _id: '$activity',
          total: { $sum: '$co2Value' },
          count: { $sum: 1 },
          avg: { $avg: '$co2Value' },
        },
      },
    ]);
    return success(res, summary);
  } catch (err) {
    next(err);
  }
};

// GET /api/carbon/profile
const getCarbonProfile = async (req, res, next) => {
  try {
    const userId = req.user._id;
    const thirtyDaysAgo = new Date(Date.now() - 30 * 24 * 60 * 60 * 1000);
    
    // Get user's carbon data
    const [userLogs, totalUsers, userRank] = await Promise.all([
      CarbonLog.find({ userId, date: { $gte: thirtyDaysAgo } }),
      User.countDocuments(),
      getUserRank(userId)
    ]);

    const totalFootprint = userLogs.reduce((sum, log) => sum + log.co2Value, 0);
    const weeklyAverage = totalFootprint / 4.3; // 4.3 weeks in 30 days
    
    // Get monthly trend
    const monthlyTrend = await CarbonLog.aggregate([
      { $match: { userId, date: { $gte: new Date(Date.now() - 180 * 24 * 60 * 60 * 1000) } } },
      {
        $group: {
          _id: { $dateToString: { format: '%Y-%m', date: '$date' } },
          total: { $sum: '$co2Value' }
        }
      },
      { $sort: { _id: 1 } },
      { $limit: 6 }
    ]).then(results => results.map(r => ({ label: r._id, value: r.total })));

    // Get user badges
    const badges = await getUserBadges(userId, userLogs);
    
    // Get comparisons
    const comparisons = await getCarbonComparisons(userId, totalFootprint);

    const profile = {
      userId,
      totalFootprint,
      weeklyAverage,
      monthlyTrend,
      rank: userRank,
      badges,
      goals: await getUserGoals(userId),
      comparisons
    };

    return success(res, profile);
  } catch (err) {
    next(err);
  }
};

// GET /api/carbon/leaderboard
const getCarbonLeaderboard = async (req, res, next) => {
  try {
    const { type = 'global', period = '30d' } = req.query;
    const days = period === '7d' ? 7 : period === '90d' ? 90 : 30;
    const since = new Date(Date.now() - days * 24 * 60 * 60 * 1000);

    let matchStage = { date: { $gte: since } };
    if (type === 'friends') {
      // Add friend logic here based on user's friends list
      const user = await User.findById(req.user._id);
      matchStage.userId = { $in: [req.user._id, ...(user.friends || [])] };
    } else if (type === 'city') {
      // Add city-based filtering
      const user = await User.findById(req.user._id);
      matchStage.city = user.city;
    }

    const leaderboard = await CarbonLog.aggregate([
      { $match: matchStage },
      {
        $group: {
          _id: '$userId',
          totalCarbon: { $sum: '$co2Value' },
          count: { $sum: 1 }
        }
      },
      { $sort: { totalCarbon: 1 } }, // Lower carbon = higher rank
      {
        $lookup: {
          from: 'users',
          localField: '_id',
          foreignField: '_id',
          as: 'user'
        }
      },
      { $unwind: '$user' },
      {
        $project: {
          rank: { $add: [{ $indexOfArray: [['$_id'], '$_id'] }, 1] },
          name: '$user.name',
          ecoScore: '$user.ecoScore',
          avatarUrl: '$user.avatarUrl',
          totalCarbon: 1,
          isCurrentUser: { $eq: ['$userId', req.user._id] }
        }
      },
      { $limit: 50 }
    ]);

    // Add rank numbers
    const rankedLeaderboard = leaderboard.map((entry, index) => ({
      ...entry,
      rank: index + 1
    }));

    return success(res, { [type]: rankedLeaderboard });
  } catch (err) {
    next(err);
  }
};

// POST /api/carbon/goals
const createCarbonGoal = async (req, res, next) => {
  try {
    const { title, targetReduction, deadline } = req.body;
    
    const goal = {
      id: new Date().getTime().toString(),
      title,
      targetReduction,
      currentReduction: 0,
      deadline,
      isActive: true
    };

    await User.findByIdAndUpdate(req.user._id, {
      $push: { carbonGoals: goal }
    });

    return success(res, goal, 'Goal created successfully');
  } catch (err) {
    next(err);
  }
};

// Helper functions
const getUserRank = async (userId) => {
  const thirtyDaysAgo = new Date(Date.now() - 30 * 24 * 60 * 60 * 1000);
  
  const userCarbon = await CarbonLog.aggregate([
    { $match: { userId, date: { $gte: thirtyDaysAgo } } },
    { $group: { _id: null, total: { $sum: '$co2Value' } } }
  ]).then(result => result[0]?.total || 0);

  const allUsersCarbon = await CarbonLog.aggregate([
    { $match: { date: { $gte: thirtyDaysAgo } } },
    {
      $group: {
        _id: '$userId',
        total: { $sum: '$co2Value' }
      }
    },
    { $sort: { total: 1 } }
  ]);

  const userRank = allUsersCarbon.findIndex(user => user._id.toString() === userId.toString()) + 1;
  const totalUsers = allUsersCarbon.length;
  const percentile = ((totalUsers - userRank) / totalUsers) * 100;
  
  const rankInfo = RANK_TITLES.find(r => percentile >= r.min && percentile < r.max) || RANK_TITLES[0];
  
  return {
    currentRank: userRank,
    totalUsers,
    rankTitle: rankInfo.title,
    percentile,
    improvement: Math.floor(Math.random() * 20) - 10 // Mock improvement data
  };
};

const getUserBadges = async (userId, userLogs) => {
  const badges = CARBON_BADGES.map(badge => {
    let isUnlocked = false;
    let unlockedDate = null;

    switch (badge.id) {
      case 'first_step':
        isUnlocked = userLogs.length > 0;
        break;
      case 'week_warrior':
        const uniqueDays = new Set(userLogs.map(log => log.date.toDateString())).size;
        isUnlocked = uniqueDays >= 7;
        break;
      case 'low_impact':
        const dailyAverages = {};
        userLogs.forEach(log => {
          const day = log.date.toDateString();
          dailyAverages[day] = (dailyAverages[day] || 0) + log.co2Value;
        });
        isUnlocked = Object.values(dailyAverages).some(total => total < 5);
        break;
      default:
        isUnlocked = Math.random() > 0.7; // Mock other badges
    }

    if (isUnlocked && !unlockedDate) {
      unlockedDate = new Date().toISOString();
    }

    return {
      ...badge,
      isUnlocked,
      unlockedDate
    };
  });

  return badges;
};

const getUserGoals = async (userId) => {
  const user = await User.findById(userId);
  return user.carbonGoals || [];
};

const getCarbonComparisons = async (userId, userTotal) => {
  // Mock comparison data - in real app, this would aggregate data from other users
  return {
    cityAverage: userTotal * (0.8 + Math.random() * 0.4),
    nationalAverage: userTotal * (0.9 + Math.random() * 0.3),
    globalAverage: userTotal * (0.7 + Math.random() * 0.5),
    friendAverage: userTotal * (0.85 + Math.random() * 0.3)
  };
};

module.exports = { 
  calculateCarbon, 
  getCarbonHistory, 
  getCarbonSummary,
  getCarbonProfile,
  getCarbonLeaderboard,
  createCarbonGoal
};
