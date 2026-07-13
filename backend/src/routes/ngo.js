const express = require('express');
const { body } = require('express-validator');
const { 
  getNGOs,
  getNGOById,
  getNGOCompetitions,
  getCompetitionById,
  getNGOCleanupData,
  getNGOImpactMetrics,
  joinCompetition,
  addCompetitionActivity,
  getSupportGuidelines,
  getRealTimeStats
} = require('../controllers/ngoController');
const { protect } = require('../middleware/auth');
const validate = require('../middleware/validate');

const router = express.Router();

// Public routes
router.get('/list', getNGOs);
router.get('/competitions', getNGOCompetitions);
router.get('/cleanup-data', getNGOCleanupData);
router.get('/support-guidelines', getSupportGuidelines);
router.get('/real-time-stats', getRealTimeStats);

// Protected routes
router.use(protect);

router.get('/:id', getNGOById);
router.get('/competitions/:id', getCompetitionById);
router.get('/impact-metrics/:ngoId', getNGOImpactMetrics);

router.post(
  '/competitions/:id/join',
  [
    body('userId').notEmpty().withMessage('User ID is required'),
    body('userName').notEmpty().withMessage('User name is required'),
    body('territoryName').notEmpty().withMessage('Territory name is required'),
  ],
  validate,
  joinCompetition
);

router.post(
  '/competitions/:id/activity',
  [
    body('userId').notEmpty().withMessage('User ID is required'),
    body('activityType').notEmpty().withMessage('Activity type is required'),
    body('points').isFloat({ min: 0 }).withMessage('Points must be a positive number'),
    body('description').notEmpty().withMessage('Description is required'),
    body('location').notEmpty().withMessage('Location is required'),
  ],
  validate,
  addCompetitionActivity
);

module.exports = router;
