package com.vasundhara.app.data.remote.dto

import com.google.gson.annotations.SerializedName

// ── Generic wrapper ───────────────────────────────────────────────────────────
data class ApiResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T?
)

// ── Auth ──────────────────────────────────────────────────────────────────────
data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    val language: String = "en"
)

data class LoginRequest(val email: String, val password: String)

data class GoogleAuthRequest(val idToken: String)

data class AuthData(
    val token: String,
    val refreshToken: String,
    val user: UserDto
)

// ── User ──────────────────────────────────────────────────────────────────────
data class UserDto(
    @SerializedName("_id") val id: String,
    val name: String,
    val email: String,
    val ecoScore: Int,
    val totalCO2Saved: Float,
    val streakDays: Int,
    val rank: String,
    val points: Int,
    val avatarUrl: String?,
    val language: String,
    val theme: String,
    val achievements: List<AchievementDto>
)

data class AchievementDto(
    val id: String,
    val title: String,
    val description: String,
    val isUnlocked: Boolean,
    val unlockedAt: String?
)

// ── Dashboard ─────────────────────────────────────────────────────────────────
data class DashboardDto(
    val co2Saved: Float,
    val ecoScore: Int,
    val streakDays: Int,
    val rank: String,
    val points: Int,
    val weeklyTrend: List<TrendDto>,
    val rhiScore: Float,
    val activeChallenges: List<ChallengeDtoItem>
)

data class TrendDto(
    @SerializedName("_id") val label: String,
    val total: Float
)

// ── Waste ─────────────────────────────────────────────────────────────────────
data class WasteDetectRequest(val imageBase64: String, val language: String = "en")

data class WasteResultDto(
    val logId: String,
    val category: String,
    val confidence: Float,
    val disposalSteps: List<String>,
    val recyclingTips: List<String>,
    val ecoImpact: String,
    val pointsEarned: Int,
    val nearbyRecyclers: List<RecyclerDto>
)

data class RecyclerDto(
    val name: String,
    val address: String,
    val lat: Double,
    val lng: Double,
    val accepts: List<String>
)

// ── Carbon ────────────────────────────────────────────────────────────────────
data class CarbonCalculateRequest(
    val activity: String,
    val subActivity: String?,
    val quantity: Float,
    val unit: String
)

data class CarbonResultDto(
    val logId: String,
    val co2Value: Float,
    val activity: String,
    val quantity: Float,
    val unit: String,
    val equivalents: EquivalentsDto
)

data class EquivalentsDto(
    val treeDays: Float,
    val drivingKm: Float,
    val phoneCharges: Float
)

data class CarbonHistoryDto(
    val logs: List<CarbonLogDto>,
    val trend: List<TrendDto>,
    val total: Int
)

data class CarbonLogDto(
    @SerializedName("_id") val id: String,
    val activity: String,
    val co2Value: Float,
    val date: String
)

// ── RHI ───────────────────────────────────────────────────────────────────────
data class RhiRequest(
    val waterPollution: Float,
    val wasteDumped: Float,
    val industrialImpact: Float,
    val carbonInRiver: Float
)

data class RhiResultDto(
    val score: Float,
    val status: String,
    val suggestions: List<String>,
    val trend: List<TrendDto>
)

// ── Recommendations ───────────────────────────────────────────────────────────
data class RecommendationsDto(
    val recommendations: List<String>,
    val dailyTip: String
)

data class ChatRequest(val message: String, val language: String = "en")

data class ChatResponseDto(val reply: String, val timestamp: String)

// ── Gamification ──────────────────────────────────────────────────────────────
data class ChallengeDtoItem(
    val id: String,
    val title: String,
    val description: String,
    val pointsReward: Int,
    val isCompleted: Boolean,
    val progress: Float,
    val expiresAt: Long
)

data class LeaderboardDto(
    val entries: List<LeaderboardEntryDto>,
    val userRank: Int
)

data class LeaderboardEntryDto(
    val rank: Int,
    val name: String,
    val ecoScore: Int,
    val avatarUrl: String?,
    val isCurrentUser: Boolean
)
