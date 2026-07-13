const express = require('express');
const { body } = require('express-validator');
const { detectWaste, getWasteHistory } = require('../controllers/wasteController');
const { protect } = require('../middleware/auth');
const validate = require('../middleware/validate');

const router = express.Router();

router.use(protect);

router.post(
  '/detect',
  [body('imageBase64').notEmpty().withMessage('Image data required')],
  validate,
  detectWaste
);

router.get('/history', getWasteHistory);

module.exports = router;
