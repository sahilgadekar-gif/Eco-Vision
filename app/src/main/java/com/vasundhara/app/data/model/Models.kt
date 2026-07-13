package com.vasundhara.app.data.model

// ── API Result ────────────────────────────────────────────────────────────────
sealed class ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    data class Error(val message: String, val code: Int = 0) : ApiResult<Nothing>()
    object Loading : ApiResult<Nothing>()
}

// ── User ──────────────────────────────────────────────────────────────────────
data class User(
    val id: String,
    val name: String,
    val email: String,
    val ecoScore: Int,
    val totalCO2Saved: Float,
    val streakDays: Int,
    val rank: String,
    val points: Int,
    val level: EcoLevel,
    val avatarUrl: String?,
    val language: AppLanguage,
    val theme: AppTheme,
    val achievements: List<Achievement>
)

enum class EcoLevel(val label: String, val minPoints: Int, val color: Long) {
    ECO_BEGINNER("Eco Beginner", 0, 0xFF78909C),
    GREEN_EXPLORER("Green Explorer", 200, 0xFF26A69A),
    GREEN_WARRIOR("Green Warrior", 500, 0xFF00E676),
    EARTH_GUARDIAN("Earth Guardian", 1000, 0xFF2979FF),
    ECO_CHAMPION("Eco Champion", 2000, 0xFFFFAB00)
}

enum class AppLanguage(val code: String, val displayName: String) {
    ENGLISH("en", "English"),
    HINDI("hi", "हिंदी"),
    MARATHI("mr", "मराठी")
}

enum class AppTheme { DARK, LIGHT, SYSTEM }

// ── Achievement ───────────────────────────────────────────────────────────────
data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val iconRes: String,
    val isUnlocked: Boolean,
    val progress: Float = 1f,
    val pointsReward: Int = 50
)

// ── River Monitoring Hotspot ──────────────────────────────────────────────────
data class RiverHotspot(
    val id: String,
    val location: String,
    val description: String,
    val imageUrl: String,
    val reporterName: String,
    val reporterId: String,
    val status: String = "Pending Cleanup",
    val timestamp: Long = System.currentTimeMillis(),
    val severity: String = "High",
    val coordinates: Pair<Double, Double>? = null,
    val municipalNotified: Boolean = false,
    val cleanupDate: String? = null
)

// ── User Profile ──────────────────────────────────────────────────────────────
data class UserProfile(
    val id: String,
    val name: String,
    val email: String,
    val phone: String,
    val address: String,
    val profileImageUrl: String? = null,
    val joinDate: String,
    val totalReports: Int = 0,
    val verifiedReports: Int = 0,
    val citizenScore: Int = 0,
    val treesPlanted: Int = 0,
    val territory: String? = null
)

// ── Trees Planted ─────────────────────────────────────────────────────────────
data class TreeLocation(
    val id: String,
    val lat: Double,
    val lng: Double,
    val treeType: String,
    val plantedBy: String,
    val plantedDate: String,
    val height: Float = 1.5f,
    val health: String = "Healthy",
    val imageUrl: String? = null
)

// ── Territory ───────────────────────────────────────────────────────────────────
data class Territory(
    val id: String,
    val name: String,
    val ownerName: String,
    val ownerId: String,
    val area: List<Coordinate>,
    val centerLat: Double,
    val centerLng: Double,
    val description: String,
    val color: String = "#4CAF50",
    val totalTrees: Int = 0,
    val population: Int = 0
)

// ── Coordinate ───────────────────────────────────────────────────────────────
data class Coordinate(
    val lat: Double,
    val lng: Double
)

// ── River Location Data ───────────────────────────────────────────────────────
data class RiverLocationData(
    val userLat: Double,
    val userLng: Double,
    val riverLat: Double,
    val riverLng: Double,
    val distanceInMeters: Double,
    val riverName: String,
    val timestamp: Long = System.currentTimeMillis()
)

// ── NGO Data Models ───────────────────────────────────────────────────────────
data class NGO(
    val id: String,
    val name: String,
    val registrationNumber: String,
    val establishedYear: Int,
    val location: String,
    val city: String,
    val state: String,
    val latitude: Double,
    val longitude: Double,
    val contactEmail: String,
    val contactPhone: String,
    val website: String,
    val description: String,
    val focusAreas: List<String>,
    val totalProjects: Int,
    val activeProjects: Int,
    val volunteersCount: Int,
    val rating: Float,
    val verified: Boolean,
    val logoUrl: String? = null,
    val coverImageUrl: String? = null
)

data class NGORiverCleanup(
    val id: String,
    val ngoId: String,
    val riverName: String,
    val location: String,
    val cleanupDate: String,
    val volunteersParticipated: Int,
    val wasteCollectedKg: Float,
    val areaCleanedSqMeters: Float,
    val durationHours: Float,
    val beforeImageUrl: String,
    val afterImageUrl: String,
    val impactScore: Float,
    val status: String,
    val description: String
)

data class NGOCompetition(
    val id: String,
    val name: String,
    val description: String,
    val startDate: String,
    val endDate: String,
    val type: String, // "territory", "city", "state"
    val territoryId: String? = null,
    val participants: List<CompetitionParticipant>,
    val prizes: List<CompetitionPrize>,
    val rules: List<String>,
    val status: String,
    val currentLeaderboard: List<LeaderboardEntry>
)

data class CompetitionParticipant(
    val userId: String,
    val userName: String,
    val territoryName: String,
    val score: Float,
    val rank: Int,
    val activities: List<UserActivity>
)

data class CompetitionPrize(
    val rank: Int,
    val prizeName: String,
    val prizeValue: String,
    val description: String
)

data class UserActivity(
    val id: String,
    val userId: String,
    val activityType: String,
    val points: Float,
    val description: String,
    val location: String,
    val timestamp: String,
    val imageUrl: String? = null
)

data class NGOSupportGuideline(
    val id: String,
    val title: String,
    val description: String,
    val steps: List<String>,
    val requirements: List<String>,
    val benefits: List<String>,
    val contactInfo: String,
    val estimatedTime: String,
    val difficulty: String
)

data class NGOImpactMetrics(
    val ngoId: String,
    val totalWasteCollected: Float,
    val totalTreesPlanted: Int,
    val totalVolunteers: Int,
    val totalProjects: Int,
    val riversCleaned: Int,
    val co2Reduced: Float,
    val monthlyTrend: List<TrendPoint>,
    val territoryImpact: Map<String, Float>
)

// ── Dashboard ─────────────────────────────────────────────────────────────────
data class DashboardStats(
    val co2SavedKg: Float,
    val ecoScore: Int,
    val weeklyTrend: List<TrendPoint>,
    val streakDays: Int,
    val rank: String,
    val points: Int,
    val level: EcoLevel,
    val rhiScore: Float,
    val activeChallenges: List<DailyChallenge>
)

data class TrendPoint(val label: String, val value: Float)

// ── Waste ─────────────────────────────────────────────────────────────────────
data class WasteResult(
    val category: String,
    val confidence: Float,
    val disposalSteps: List<String>,
    val recyclingTips: List<String>,
    val ecoImpact: String,
    val nearbyRecyclers: List<RecyclingCenter>
)

data class RecyclingCenter(
    val name: String,
    val address: String,
    val lat: Double,
    val lng: Double,
    val accepts: List<String>
)

// ── Carbon ────────────────────────────────────────────────────────────────────
data class CarbonInput(
    val travelKm: Float = 0f,
    val travelMode: String = "car",
    val electricityKwh: Float = 0f,
    val foodMeatServings: Float = 0f,
    val waterLitres: Float = 0f,
    val wasteKg: Float = 0f,
    val shoppingItems: Int = 0,
    val digitalHours: Float = 0f
) {
    fun calculateCo2(): Float =
        (travelKm * emissionFactor(travelMode)) +
        (electricityKwh * 0.82f) +
        (foodMeatServings * 3.3f) +
        (waterLitres * 0.0003f) +
        (wasteKg * 0.5f) +
        (shoppingItems * 2.0f) +
        (digitalHours * 0.05f)

    private fun emissionFactor(mode: String) = when (mode) {
        "car" -> 0.21f; "bus" -> 0.089f; "train" -> 0.041f
        "flight" -> 0.255f; "bike" -> 0f; "walk" -> 0f; else -> 0.21f
    }
}

data class CarbonLog(
    val id: String,
    val activity: String,
    val co2Value: Float,
    val date: String,
    val equivalents: CarbonEquivalents,
    val category: CarbonCategory,
    val userId: String
)

data class CarbonEquivalents(
    val treeDays: Float,
    val drivingKm: Float,
    val phoneCharges: Float,
    val burgers: Float
)

enum class CarbonCategory(val label: String, val icon: String) {
    TRANSPORT("Transport", "🚗"),
    ENERGY("Energy", "⚡"),
    FOOD("Food", "🍽️"),
    WATER("Water", "💧"),
    WASTE("Waste", "🗑️"),
    SHOPPING("Shopping", "🛍️"),
    DIGITAL("Digital", "💻")
}

data class PersonalCarbonProfile(
    val userId: String,
    val totalFootprint: Float,
    val weeklyAverage: Float,
    val monthlyTrend: List<TrendPoint>,
    rank: CarbonRank,
    val badges: List<CarbonBadge>,
    val goals: List<CarbonGoal>,
    val comparisons: CarbonComparison
)

data class CarbonRank(
    val currentRank: Int,
    val totalUsers: Int,
    val rankTitle: String,
    val percentile: Float,
    val improvement: Int
)

data class CarbonBadge(
    val id: String,
    val title: String,
    val description: String,
    val icon: String,
    val isUnlocked: Boolean,
    val unlockedDate: String?
)

data class CarbonGoal(
    val id: String,
    val title: String,
    val targetReduction: Float,
    val currentReduction: Float,
    val deadline: String,
    val isActive: Boolean
)

data class CarbonComparison(
    val cityAverage: Float,
    val nationalAverage: Float,
    val globalAverage: Float,
    val friendAverage: Float
)

data class CarbonLeaderboard(
    val global: List<LeaderboardEntry>,
    val friends: List<LeaderboardEntry>,
    val city: List<LeaderboardEntry>
)

// ── RHI ───────────────────────────────────────────────────────────────────────
data class RhiData(
    val score: Float,
    val status: RhiStatus,
    val waterPollutionLevel: Float,
    val wasteDumped: Float,
    val industrialImpact: Float,
    val carbonInRiver: Float,
    val suggestions: List<String>,
    val trend: List<TrendPoint>
)

enum class RhiStatus(val label: String, val colorHex: Long) {
    HEALTHY("Healthy", 0xFF00E676),
    MODERATE("Moderate", 0xFFFFAB00),
    CRITICAL("Critical", 0xFFFF1744)
}

// ── AI Chat ───────────────────────────────────────────────────────────────────
data class ChatMessage(
    val id: String,
    val content: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val language: AppLanguage = AppLanguage.ENGLISH
)

// ── Gamification ──────────────────────────────────────────────────────────────
data class DailyChallenge(
    val id: String,
    val title: String,
    val description: String,
    val pointsReward: Int,
    val isCompleted: Boolean,
    val progress: Float,
    val expiresAt: Long
)

data class LeaderboardEntry(
    val rank: Int,
    val name: String,
    val ecoScore: Int,
    val avatarUrl: String?,
    val isCurrentUser: Boolean
)

// ── Eco Map ───────────────────────────────────────────────────────────────────
data class EcoMapMarker(
    val id: String,
    val name: String,
    val type: EcoMarkerType,
    val lat: Double,
    val lng: Double,
    val description: String,
    val contact: String?
)

enum class EcoMarkerType(val label: String) {
    RECYCLING("Recycling Center"),
    NGO("NGO"),
    SOLAR("Solar Station"),
    EV_CHARGING("EV Charging"),
    COMPOST("Compost Site")
}
