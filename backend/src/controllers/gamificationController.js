const User = require('../models/User');
const { success, error } = require('../utils/apiResponse');

const CHALLENGES = [
  { id: '1', title: 'No Plastic Day', description: 'Avoid single-use plastic for 24 hours', pointsReward: 50, expiresAt: () => Date.now() + 86400000 },
  { id: '2', title: 'Public Transport', description: 'Use bus or train today', pointsReward: 40, expiresAt: () => Date.now() + 86400000 },
  { id: '3', title: 'Plant-Based Meal', description: 'Eat one plant-based meal', pointsReward: 30, expiresAt: () => Date.now() + 86400000 },
  { id: '4', title: '5-Min Cold Shower', description: 'Save water and energy', pointsReward: 20, expiresAt: () => Date.now() + 86400000 },
  { id: '5', title: 'Recycle 3 Items', description: 'Properly recycle 3 waste items', pointsReward: 35, expiresAt: () => Date.now() + 86400000 },
];

exports.getChallenges = async (req, res, next) => {
  try {
    const challenges = CHALLENGES.map(c => ({ ...c, expiresAt: c.expiresAt(), isCompleted: false, progress: 0 }));
    return success(res, challenges);
  } catch (err) { next(err); }
};

exports.completeChallenge = async (req, res, next) => {
  try {
    const challenge = CHALLENGES.find(c => c.id === req.params.id);
    if (!challenge) return error(res, 'Challenge not found.', 404);
    const user = await User.findByIdAndUpdate(req.user._id, { $inc: { points: challenge.pointsReward, ecoScore: 1 } }, { new: true });
    user.updateRank(); await user.save({ validateBeforeSave: false });
    return success(res, { pointsEarned: challenge.pointsReward, newTotal: user.points, rank: user.rank });
  } catch (err) { next(err); }
};

exports.getLeaderboard = async (req, res, next) => {
  try {
    const users = await User.find({ isActive: true }).sort({ ecoScore: -1 }).limit(20).select('name ecoScore avatarUrl');
    const entries = users.map((u, i) => ({ rank: i + 1, name: u.name, ecoScore: u.ecoScore, avatarUrl: u.avatarUrl, isCurrentUser: u._id.toString() === req.user._id.toString() }));
    const userRank = entries.findIndex(e => e.isCurrentUser) + 1;
    return success(res, { entries, userRank });
  } catch (err) { next(err); }
};
