const express = require('express');
const { body } = require('express-validator');
const { calculateCarbon, getCarbonHistory, getCarbonSummary, getCarbonProfile, getCarbonLeaderboard, createCarbonGoal } = require('../controllers/carbonController');
const { protect } = require('../middleware/auth');
const validate = require('../middleware/validate');

const router = express.Router();

router.use(protect);

router.post(
  '/calculate',
  [
    body('activity').isIn(['travel', 'electricity', 'food', 'shopping', 'waste', 'digital', 'other']),
    body('quantity').isFloat({ min: 0 }).withMessage('Quantity must be a positive number'),
    body('unit').notEmpty().withMessage('Unit is required'),
  ],
  validate,
  calculateCarbon
);

router.get('/history', getCarbonHistory);
router.get('/summary', getCarbonSummary);
router.get('/profile', getCarbonProfile);
router.get('/leaderboard', getCarbonLeaderboard);

router.post(
  '/goals',
  [
    body('title').notEmpty().withMessage('Goal title is required'),
    body('targetReduction').isFloat({ min: 0 }).withMessage('Target reduction must be positive'),
    body('deadline').isISO8601().withMessage('Valid deadline date is required'),
  ],
  validate,
  createCarbonGoal
);

module.exports = router;
