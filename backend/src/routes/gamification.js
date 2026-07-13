const express = require('express');
const { getChallenges, completeChallenge, getLeaderboard } = require('../controllers/gamificationController');
const { protect } = require('../middleware/auth');
const router = express.Router();
router.use(protect);
router.get('/challenges', getChallenges);
router.post('/complete/:id', completeChallenge);
router.get('/leaderboard', getLeaderboard);
module.exports = router;
