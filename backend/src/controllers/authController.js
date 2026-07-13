const jwt = require('jsonwebtoken');
const { OAuth2Client } = require('google-auth-library');
const User = require('../models/User');
const { success, error } = require('../utils/apiResponse');
const logger = require('../utils/logger');

const client = new OAuth2Client(process.env.GOOGLE_CLIENT_ID);

const signToken = (id) => jwt.sign({ id }, process.env.JWT_SECRET, { expiresIn: process.env.JWT_EXPIRES_IN || '7d' });
const signRefresh = (id) => jwt.sign({ id }, process.env.JWT_SECRET + '_refresh', { expiresIn: process.env.JWT_REFRESH_EXPIRES_IN || '30d' });

const DEFAULT_ACHIEVEMENTS = [
  { id: 'first_step', title: 'First Step', description: 'Joined Vasundhara', isUnlocked: true, unlockedAt: new Date() },
];

// POST /api/auth/register
exports.register = async (req, res, next) => {
  try {
    const { name, email, password, language = 'en' } = req.body;
    if (await User.findOne({ email })) return error(res, 'Email already registered.', 409);
    const user = await User.create({ name, email, password, language, achievements: DEFAULT_ACHIEVEMENTS });
    const token = signToken(user._id);
    const refreshToken = signRefresh(user._id);
    user.refreshToken = refreshToken;
    await user.save({ validateBeforeSave: false });
    logger.info(`Registered: ${email}`);
    return success(res, { token, refreshToken, user }, 'Registration successful', 201);
  } catch (err) { next(err); }
};

// POST /api/auth/login
exports.login = async (req, res, next) => {
  try {
    const { email, password } = req.body;
    const user = await User.findOne({ email }).select('+password');
    if (!user || !(await user.comparePassword(password))) return error(res, 'Invalid credentials.', 401);
    if (!user.isActive) return error(res, 'Account deactivated.', 403);
    await updateStreak(user);
    const token = signToken(user._id);
    const refreshToken = signRefresh(user._id);
    user.refreshToken = refreshToken;
    await user.save({ validateBeforeSave: false });
    logger.info(`Login: ${email}`);
    return success(res, { token, refreshToken, user });
  } catch (err) { next(err); }
};

// POST /api/auth/google
exports.googleAuth = async (req, res, next) => {
  try {
    const { idToken } = req.body;
    const ticket = await client.verifyIdToken({ idToken, audience: process.env.GOOGLE_CLIENT_ID });
    const { sub: googleId, email, name, picture } = ticket.getPayload();
    let user = await User.findOne({ $or: [{ googleId }, { email }] });
    if (!user) {
      user = await User.create({ name, email, googleId, avatarUrl: picture, achievements: DEFAULT_ACHIEVEMENTS });
    } else if (!user.googleId) {
      user.googleId = googleId; user.avatarUrl = picture;
      await user.save({ validateBeforeSave: false });
    }
    await updateStreak(user);
    const token = signToken(user._id);
    return success(res, { token, refreshToken: signRefresh(user._id), user });
  } catch (err) { next(err); }
};

// POST /api/auth/refresh
exports.refreshToken = async (req, res, next) => {
  try {
    const { refreshToken } = req.body;
    if (!refreshToken) return error(res, 'Refresh token required.', 400);
    const decoded = jwt.verify(refreshToken, process.env.JWT_SECRET + '_refresh');
    const user = await User.findById(decoded.id).select('+refreshToken');
    if (!user || user.refreshToken !== refreshToken) return error(res, 'Invalid refresh token.', 401);
    return success(res, { token: signToken(user._id) });
  } catch (err) { next(err); }
};

async function updateStreak(user) {
  const today = new Date().toDateString();
  const last = user.lastActiveDate ? new Date(user.lastActiveDate).toDateString() : null;
  if (last !== today) {
    const yesterday = new Date(Date.now() - 86400000).toDateString();
    user.streakDays = last === yesterday ? user.streakDays + 1 : 1;
    user.lastActiveDate = new Date();
    await user.save({ validateBeforeSave: false });
  }
}
