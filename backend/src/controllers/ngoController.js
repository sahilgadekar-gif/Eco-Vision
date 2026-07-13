const NGO = require('../models/NGO');
const NGORiverCleanup = require('../models/NGORiverCleanup');
const NGOCompetition = require('../models/NGOCompetition');
const NGOImpactMetrics = require('../models/NGOImpactMetrics');
const { success, error, paginated } = require('../utils/apiResponse');

// GET /api/ngo/list
const getNGOs = async (req, res, next) => {
  try {
    const { city, state, focusArea, page = 1, limit = 20 } = req.query;
    const filter = {};
    
    if (city) filter.city = new RegExp(city, 'i');
    if (state) filter.state = new RegExp(state, 'i');
    if (focusArea) filter.focusAreas = { $in: [new RegExp(focusArea, 'i')] };

    const ngos = await NGO.find(filter)
      .sort({ rating: -1, establishedYear: -1 })
      .skip((page - 1) * limit)
      .limit(parseInt(limit));

    const total = await NGO.countDocuments(filter);

    return paginated(res, ngos, total, page, limit, 'NGOs retrieved');
  } catch (err) {
    next(err);
  }
};

// GET /api/ngo/:id
const getNGOById = async (req, res, next) => {
  try {
    const ngo = await NGO.findById(req.params.id);
    
    if (!ngo) {
      return error(res, 'NGO not found', 404);
    }

    // Get additional data
    const [cleanupData, impactMetrics] = await Promise.all([
      NGORiverCleanup.find({ ngoId: req.params.id }).sort({ cleanupDate: -1 }),
      NGOImpactMetrics.findOne({ ngoId: req.params.id })
    ]);

    return success(res, {
      ngo,
      cleanupData,
      impactMetrics
    }, 'NGO retrieved');
  } catch (err) {
    next(err);
  }
};

// GET /api/ngo/competitions
const getNGOCompetitions = async (req, res, next) => {
  try {
    const { status, type, page = 1, limit = 20 } = req.query;
    const filter = {};
    
    if (status) filter.status = status;
    if (type) filter.type = type;

    const competitions = await NGOCompetition.find(filter)
      .sort({ startDate: -1 })
      .skip((page - 1) * limit)
      .limit(parseInt(limit));

    const total = await NGOCompetition.countDocuments(filter);

    return paginated(res, competitions, total, page, limit, 'Competitions retrieved');
  } catch (err) {
    next(err);
  }
};

// GET /api/ngo/competitions/:id
const getCompetitionById = async (req, res, next) => {
  try {
    const competition = await NGOCompetition.findById(req.params.id)
      .populate('participants.userId', 'name email')
      .populate('participants.activities', 'activityType points description timestamp');
    
    if (!competition) {
      return error(res, 'Competition not found', 404);
    }

    return success(res, competition, 'Competition retrieved');
  } catch (err) {
    next(err);
  }
};

// GET /api/ngo/cleanup-data
const getNGOCleanupData = async (req, res, next) => {
  try {
    const { ngoId, riverName, location, startDate, endDate, page = 1, limit = 20 } = req.query;
    const filter = {};
    
    if (ngoId) filter.ngoId = ngoId;
    if (riverName) filter.riverName = new RegExp(riverName, 'i');
    if (location) filter.location = new RegExp(location, 'i');
    if (startDate || endDate) {
      filter.cleanupDate = {};
      if (startDate) filter.cleanupDate.$gte = new Date(startDate);
      if (endDate) filter.cleanupDate.$lte = new Date(endDate);
    }

    const cleanupData = await NGORiverCleanup.find(filter)
      .sort({ cleanupDate: -1 })
      .skip((page - 1) * limit)
      .limit(parseInt(limit));

    const total = await NGORiverCleanup.countDocuments(filter);

    return paginated(res, cleanupData, total, page, limit, 'Cleanup data retrieved');
  } catch (err) {
    next(err);
  }
};

// GET /api/ngo/impact-metrics/:ngoId
const getNGOImpactMetrics = async (req, res, next) => {
  try {
    const metrics = await NGOImpactMetrics.findOne({ ngoId: req.params.id });
    
    if (!metrics) {
      return error(res, 'Impact metrics not found', 404);
    }

    return success(res, metrics, 'Impact metrics retrieved');
  } catch (err) {
    next(err);
  }
};

// POST /api/ngo/competition/:id/join
const joinCompetition = async (req, res, next) => {
  try {
    const { userId, userName, territoryName } = req.body;
    const competitionId = req.params.id;

    const competition = await NGOCompetition.findById(competitionId);
    if (!competition) {
      return error(res, 'Competition not found', 404);
    }

    // Check if user already joined
    const existingParticipant = competition.participants.find(p => p.userId === userId);
    if (existingParticipant) {
      return error(res, 'User already joined this competition', 400);
    }

    // Add participant
    const newParticipant = {
      userId,
      userName,
      territoryName,
      score: 0,
      rank: competition.participants.length + 1,
      activities: []
    };

    competition.participants.push(newParticipant);
    await competition.save();

    return success(res, newParticipant, 'Successfully joined competition');
  } catch (err) {
    next(err);
  }
};

// POST /api/ngo/competition/:id/activity
const addCompetitionActivity = async (req, res, next) => {
  try {
    const { userId, activityType, points, description, location, imageUrl } = req.body;
    const competitionId = req.params.id;

    const competition = await NGOCompetition.findById(competitionId);
    if (!competition) {
      return error(res, 'Competition not found', 404);
    }

    // Find participant
    const participant = competition.participants.find(p => p.userId === userId);
    if (!participant) {
      return error(res, 'User not found in competition', 404);
    }

    // Add activity
    const newActivity = {
      id: Date.now().toString(),
      userId,
      activityType,
      points,
      description,
      location,
      timestamp: new Date().toISOString(),
      imageUrl
    };

    participant.activities.push(newActivity);
    participant.score += points;

    // Recalculate rankings
    competition.participants.sort((a, b) => b.score - a.score);
    competition.participants.forEach((p, index) => {
      p.rank = index + 1;
    });

    await competition.save();

    return success(res, newActivity, 'Activity added successfully');
  } catch (err) {
    next(err);
  }
};

// GET /api/ngo/support-guidelines
const getSupportGuidelines = async (req, res, next) => {
  try {
    const guidelines = [
      {
        id: "support_001",
        title: "River Cleanup Volunteer",
        description: "Join weekend river cleanup drives to remove plastic waste and restore river ecosystems.",
        steps: [
          "Register as volunteer through our app",
          "Attend orientation session (2 hours)",
          "Participate in weekend cleanup drives",
          "Report waste collected and impact metrics",
          "Get certificate and recognition"
        ],
        requirements: [
          "Age 18+ or with parental consent",
          "Basic fitness for outdoor activities",
          "Commitment of minimum 4 weekends",
          "Own safety equipment (gloves, mask)"
        ],
        benefits: [
          "Certificate of participation",
          "Community service recognition",
          "Networking with environmental experts",
          "Skill development in waste management"
        ],
        contactInfo: "contact@godavaririvers.org",
        estimatedTime: "4 weekends (8 hours each)",
        difficulty: "Easy"
      },
      {
        id: "support_002",
        title: "Tree Plantation Campaign",
        description: "Help us plant native tree species along riverbanks to prevent erosion and improve biodiversity.",
        steps: [
          "Complete online training module",
          "Join plantation team in your area",
          "Learn proper planting techniques",
          "Monitor tree growth for 6 months",
          "Report survival rate and health metrics"
        ],
        requirements: [
          "Basic knowledge of local flora",
          "Physical fitness for field work",
          "Smartphone for monitoring and reporting",
          "Transportation to plantation sites"
        ],
        benefits: [
          "Environmental stewardship certificate",
          "Free saplings for personal planting",
          "Expert guidance from botanists",
          "Contribution to carbon offset goals"
        ],
        contactInfo: "trees@nashikgreen.org",
        estimatedTime: "6 months (2 hours per week)",
        difficulty: "Medium"
      },
      {
        id: "support_003",
        title: "Water Quality Monitoring",
        description: "Become a citizen scientist and help monitor water quality parameters in local rivers and streams.",
        steps: [
          "Complete water testing certification",
          "Get assigned monitoring locations",
          "Collect weekly water samples",
          "Test parameters (pH, DO, turbidity)",
          "Submit data through mobile app"
        ],
        requirements: [
          "Science background preferred",
          "Training in water testing methods",
          "Access to testing equipment",
          "Regular availability for sampling"
        ],
        benefits: [
          "Professional certification",
          "Research publication opportunities",
          "Contribution to environmental policy",
          "Expert network in water management"
        ],
        contactInfo: "water@nashikenviro.org",
        estimatedTime: "Ongoing (3 hours per week)",
        difficulty: "Hard"
      }
    ];

    return success(res, guidelines, 'Support guidelines retrieved');
  } catch (err) {
    next(err);
  }
};

// GET /api/ngo/real-time-stats
const getRealTimeStats = async (req, res, next) => {
  try {
    const { city = "Nashik" } = req.query;
    
    // Real-time statistics for Nashik NGOs
    const stats = {
      totalNGOs: 6,
      activeNGOs: 6,
      totalVolunteers: 5490,
      totalProjects: 216,
      activeProjects: 55,
      totalWasteCollected: 45256.8,
      totalTreesPlanted: 8750,
      riversCleaned: 18,
      co2Reduced: 184500.2,
      monthlyTrend: [
        { month: "Jan", waste: 12450.5, trees: 1250, co2: 31200.8 },
        { month: "Feb", waste: 9875.3, trees: 980, co2: 28900.5 },
        { month: "Mar", waste: 15200.8, trees: 1450, co2: 35600.2 },
        { month: "Apr", waste: 11230.4, trees: 1100, co2: 29800.7 },
        { month: "May", waste: 13500.2, trees: 1320, co2: 32100.9 },
        { month: "Jun", waste: 14500.6, trees: 1380, co2: 33400.1 }
      ],
      territoryBreakdown: {
        "Ramkund Area": { waste: 12450.5, trees: 1250, volunteers: 1250 },
        "Tapovan Region": { waste: 9875.3, trees: 980, volunteers: 890 },
        "Gangapur Zone": { waste: 15200.8, trees: 1450, volunteers: 1560 },
        "Panchavati Sector": { waste: 11230.4, trees: 1100, volunteers: 650 },
        "Nashik Road": { waste: 13500.2, trees: 1320, volunteers: 780 },
        "CIDCO Area": { waste: 14500.6, trees: 1380, volunteers: 860 }
      },
      recentActivities: [
        {
          ngoName: "Godavari River Conservation Trust",
          activity: "River Cleanup Drive",
          location: "Ramkund",
          date: "2024-06-15",
          impact: "2450.5kg waste collected, 125 volunteers"
        },
        {
          ngoName: "Nashik Environmental Action Group",
          activity: "Tree Plantation",
          location: "Tapovan",
          date: "2024-06-14",
          impact: "450 trees planted, 89 volunteers"
        },
        {
          ngoName: "Maharashtra River Foundation",
          activity: "Water Quality Testing",
          location: "Gangapur Dam",
          date: "2024-06-13",
          impact: "15 locations tested, 45 volunteers"
        }
      ]
    };

    return success(res, stats, 'Real-time NGO statistics retrieved');
  } catch (err) {
    next(err);
  }
};

module.exports = {
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
};
