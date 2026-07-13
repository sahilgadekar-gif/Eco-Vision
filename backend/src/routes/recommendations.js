const express = require('express');
const { body } = require('express-validator');
const { getRecommendations, chatRecommendation } = require('../controllers/recommendationController');
const { protect } = require('../middleware/auth');
const validate = require('../middleware/validate');

const router = express.Router();

router.use(protect);

router.get('/', getRecommendations);
router.post(
  '/chat',
  [body('message').trim().notEmpty().withMessage('Message required')],
  validate,
  chatRecommendation
);

module.exports = router;
