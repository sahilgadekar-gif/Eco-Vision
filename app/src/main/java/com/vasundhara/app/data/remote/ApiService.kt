package com.vasundhara.app.data.remote

import com.vasundhara.app.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // ── Auth ──────────────────────────────────────────────────────────────────
    @POST("auth/register")
    suspend fun register(@Body r: RegisterRequest): Response<ApiResponse<AuthData>>

    @POST("auth/login")
    suspend fun login(@Body r: LoginRequest): Response<ApiResponse<AuthData>>

    @POST("auth/google")
    suspend fun googleAuth(@Body r: GoogleAuthRequest): Response<ApiResponse<AuthData>>

    @POST("auth/refresh")
    suspend fun refreshToken(@Body body: Map<String, String>): Response<ApiResponse<Map<String, String>>>

    // ── User ──────────────────────────────────────────────────────────────────
    @GET("user/profile")
    suspend fun getProfile(): Response<ApiResponse<UserDto>>

    @GET("user/dashboard")
    suspend fun getDashboard(): Response<ApiResponse<DashboardDto>>

    @PUT("user/profile")
    suspend fun updateProfile(@Body body: Map<String, String>): Response<ApiResponse<UserDto>>

    @PUT("user/preferences")
    suspend fun updatePreferences(@Body body: Map<String, String>): Response<ApiResponse<UserDto>>

    // ── Waste ─────────────────────────────────────────────────────────────────
    @POST("waste/detect")
    suspend fun detectWaste(@Body r: WasteDetectRequest): Response<ApiResponse<WasteResultDto>>

    @GET("waste/history")
    suspend fun getWasteHistory(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<ApiResponse<List<Any>>>

    // ── Carbon ────────────────────────────────────────────────────────────────
    @POST("carbon/calculate")
    suspend fun calculateCarbon(@Body r: CarbonCalculateRequest): Response<ApiResponse<CarbonResultDto>>

    @GET("carbon/history")
    suspend fun getCarbonHistory(
        @Query("period") period: String = "30d"
    ): Response<ApiResponse<CarbonHistoryDto>>

    // ── RHI ───────────────────────────────────────────────────────────────────
    @POST("rhi/calculate")
    suspend fun calculateRhi(@Body r: RhiRequest): Response<ApiResponse<RhiResultDto>>

    @GET("rhi/latest")
    suspend fun getLatestRhi(): Response<ApiResponse<RhiResultDto>>

    // ── Recommendations ───────────────────────────────────────────────────────
    @GET("recommendations")
    suspend fun getRecommendations(
        @Query("lang") lang: String = "en"
    ): Response<ApiResponse<RecommendationsDto>>

    @POST("recommendations/chat")
    suspend fun chat(@Body r: ChatRequest): Response<ApiResponse<ChatResponseDto>>

    // ── Gamification ──────────────────────────────────────────────────────────
    @GET("gamification/challenges")
    suspend fun getChallenges(): Response<ApiResponse<List<ChallengeDtoItem>>>

    @POST("gamification/complete/{id}")
    suspend fun completeChallenge(@Path("id") id: String): Response<ApiResponse<Map<String, Any>>>

    @GET("gamification/leaderboard")
    suspend fun getLeaderboard(
        @Query("scope") scope: String = "global"
    ): Response<ApiResponse<LeaderboardDto>>
}
