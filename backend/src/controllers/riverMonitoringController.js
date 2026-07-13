const RiverHotspot = require('../models/RiverHotspot');
const UserProfile = require('../models/UserProfile');
const { success, error, paginated } = require('../utils/apiResponse');

// GET /api/river-monitoring/hotspots
const getRiverHotspots = async (req, res, next) => {
  try {
    const { page = 1, limit = 20, status, severity } = req.query;
    const filter = {};
    
    if (status) filter.status = status;
    if (severity) filter.severity = severity;

    const hotspots = await RiverHotspot.find(filter)
      .sort({ timestamp: -1 })
      .skip((page - 1) * limit)
      .limit(parseInt(limit));

    const total = await RiverHotspot.countDocuments(filter);

    return paginated(res, hotspots, total, page, limit, 'River hotspots retrieved');
  } catch (err) {
    next(err);
  }
};

// POST /api/river-monitoring/hotspots
const createRiverHotspot = async (req, res, next) => {
  try {
    const {
      location,
      description,
      imageUrl,
      reporterName,
      reporterId,
      severity = 'High',
      coordinates
    } = req.body;

    const hotspot = new RiverHotspot({
      location,
      description,
      imageUrl,
      reporterName,
      reporterId,
      severity,
      coordinates,
      status: 'Pending Cleanup'
    });

    await hotspot.save();

    // Auto-notify municipal corporation for high severity cases
    if (severity === 'High') {
      await notifyMunicipalCorporation(hotspot);
    }

    return success(res, hotspot, 'River hotspot reported successfully');
  } catch (err) {
    next(err);
  }
};

// GET /api/river-monitoring/hotspots/:id
const getRiverHotspotById = async (req, res, next) => {
  try {
    const hotspot = await RiverHotspot.findById(req.params.id);
    
    if (!hotspot) {
      return error(res, 'Hotspot not found', 404);
    }

    return success(res, hotspot, 'Hotspot retrieved');
  } catch (err) {
    next(err);
  }
};

// PUT /api/river-monitoring/hotspots/:id/status
const updateHotspotStatus = async (req, res, next) => {
  try {
    const { status, cleanupDate } = req.body;
    
    const hotspot = await RiverHotspot.findByIdAndUpdate(
      req.params.id,
      { 
        status,
        cleanupDate: status === 'Cleaned' ? cleanupDate || new Date() : null
      },
      { new: true }
    );

    if (!hotspot) {
      return error(res, 'Hotspot not found', 404);
    }

    return success(res, hotspot, 'Hotspot status updated');
  } catch (err) {
    next(err);
  }
};

// GET /api/river-monitoring/users/:id
const getUserProfile = async (req, res, next) => {
  try {
    const user = await UserProfile.findOne({ id: req.params.id });
    
    if (!user) {
      return error(res, 'User not found', 404);
    }

    return success(res, user, 'User profile retrieved');
  } catch (err) {
    next(err);
  }
};

// POST /api/river-monitoring/users
const createUserProfile = async (req, res, next) => {
  try {
    const {
      id,
      name,
      email,
      phone,
      address,
      profileImageUrl
    } = req.body;

    const existingUser = await UserProfile.findOne({ id });
    if (existingUser) {
      return error(res, 'User already exists', 400);
    }

    const user = new UserProfile({
      id,
      name,
      email,
      phone,
      address,
      profileImageUrl,
      joinDate: new Date().toISOString(),
      totalReports: 0,
      verifiedReports: 0,
      citizenScore: 0
    });

    await user.save();
    return success(res, user, 'User profile created');
  } catch (err) {
    next(err);
  }
};

// Helper function to notify municipal corporation
const notifyMunicipalCorporation = async (hotspot) => {
  try {
    // In a real implementation, this would send email/SMS to municipal authorities
    console.log(`🚨 URGENT: River pollution reported at ${hotspot.location}`);
    console.log(`Description: ${hotspot.description}`);
    console.log(`Reporter: ${hotspot.reporterName} (${hotspot.reporterId})`);
    console.log(`Severity: ${hotspot.severity}`);
    console.log('Immediate cleanup action required!');
    
    // Mark as notified
    await RiverHotspot.findByIdAndUpdate(hotspot._id, { municipalNotified: true });
  } catch (err) {
    console.error('Failed to notify municipal corporation:', err);
  }
};

// GET /api/river-monitoring/stats
const getRiverMonitoringStats = async (req, res, next) => {
  try {
    const stats = await RiverHotspot.aggregate([
      {
        $group: {
          _id: '$status',
          count: { $sum: 1 }
        }
      }
    ]);

    const severityStats = await RiverHotspot.aggregate([
      {
        $group: {
          _id: '$severity',
          count: { $sum: 1 }
        }
      }
    ]);

    const locationStats = await RiverHotspot.aggregate([
      {
        $group: {
          _id: '$location',
          count: { $sum: 1 }
        }
      },
      { $sort: { count: -1 } },
      { $limit: 10 }
    ]);

    return success(res, {
      statusBreakdown: stats,
      severityBreakdown: severityStats,
      topLocations: locationStats,
      totalHotspots: await RiverHotspot.countDocuments()
    }, 'River monitoring stats retrieved');
  } catch (err) {
    next(err);
  }
};

module.exports = {
  getRiverHotspots,
  createRiverHotspot,
  getRiverHotspotById,
  updateHotspotStatus,
  getUserProfile,
  createUserProfile,
  getRiverMonitoringStats
};
