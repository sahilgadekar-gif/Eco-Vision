const mongoose = require('mongoose');
const bcrypt = require('bcryptjs');

const achievementSchema = new mongoose.Schema({ id: String, title: String, description: String, isUnlocked: { type: Boolean, default: false }, unlockedAt: { type: Date, default: null } });

const userSchema = new mongoose.Schema({
  name:         { type: String, required: true, trim: true, minlength: 2, maxlength: 50 },
  email:        { type: String, required: true, unique: true, lowercase: true, trim: true },
  password:     { type: String, minlength: 8, select: false },
  googleId:     { type: String, sparse: true, select: false },
  ecoScore:     { type: Number, default: 0, min: 0, max: 100 },
  totalCO2Saved:{ type: Number, default: 0 },
  streakDays:   { type: Number, default: 0 },
  lastActiveDate:{ type: Date, default: Date.now },
  rank:         { type: String, default: 'Eco Beginner' },
  points:       { type: Number, default: 0 },
  language:     { type: String, default: 'en', enum: ['en', 'hi', 'mr'] },
  theme:        { type: String, default: 'DARK', enum: ['DARK', 'LIGHT', 'SYSTEM'] },
  achievements: [achievementSchema],
  avatarUrl:    { type: String, default: null },
  isActive:     { type: Boolean, default: true },
  refreshToken: { type: String, select: false },
}, { timestamps: true });

userSchema.pre('save', async function (next) {
  if (!this.isModified('password') || !this.password) return next();
  this.password = await bcrypt.hash(this.password, 12);
  next();
});

userSchema.methods.comparePassword = async function (candidate) {
  return this.password ? bcrypt.compare(candidate, this.password) : false;
};

userSchema.methods.updateRank = function () {
  const p = this.points;
  if (p >= 2000) this.rank = 'Eco Champion';
  else if (p >= 1000) this.rank = 'Earth Guardian';
  else if (p >= 500) this.rank = 'Green Warrior';
  else if (p >= 200) this.rank = 'Green Explorer';
  else this.rank = 'Eco Beginner';
};

userSchema.methods.toJSON = function () {
  const o = this.toObject();
  delete o.password; delete o.refreshToken; delete o.__v; delete o.googleId;
  return o;
};

module.exports = mongoose.model('User', userSchema);
