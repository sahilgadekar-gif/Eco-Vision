const express = require('express');
const { body } = require('express-validator');
const { 
  getRiverHotspots, 
  createRiverHotspot, 
  getRiverHotspotById, 
  updateHotspotStatus,
  getUserProfile,
  createUserProfile,
  getRiverMonitoringStats
} = require('../controllers/riverMonitoringController');
const { protect } = require('../middleware/auth');
const validate = require('../middleware/validate');

const router = express.Router();

// Public routes
router.get('/hotspots', getRiverHotspots);
router.get('/hotspots/:id', getRiverHotspotById);
router.get('/users/:id', getUserProfile);
router.get('/stats', getRiverMonitoringStats);

// Protected routes
router.use(protect);

router.post(
  '/hotspots',
  [
    body('location').notEmpty().withMessage('Location is required'),
    body('description').notEmpty().withMessage('Description is required'),
    body('imageUrl').optional().isURL().withMessage('Image URL must be valid'),
    body('reporterName').notEmpty().withMessage('Reporter name is required'),
    body('reporterId').notEmpty().withMessage('Reporter ID is required'),
    body('severity').optional().isIn(['Low', 'Medium', 'High']).withMessage('Severity must be Low, Medium, or High'),
  ],
  validate,
  createRiverHotspot
);

router.put(
  '/hotspots/:id/status',
  [
    body('status').isIn(['Pending Cleanup', 'In Progress', 'Cleaned']).withMessage('Invalid status'),
    body('cleanupDate').optional().isISO8601().withMessage('Cleanup date must be valid'),
  ],
  validate,
  updateHotspotStatus
);

router.post(
  '/users',
  [
    body('id').notEmpty().withMessage('User ID is required'),
    body('name').notEmpty().withMessage('Name is required'),
    body('email').isEmail().withMessage('Valid email is required'),
    body('phone').notEmpty().withMessage('Phone number is required'),
    body('address').notEmpty().withMessage('Address is required'),
  ],
  validate,
  createUserProfile
);

module.exports = router;
