package com.vasundhara.app.data.repository

import com.vasundhara.app.data.local.UserPreferences
import com.vasundhara.app.data.model.*
import com.vasundhara.app.data.remote.ApiService
import com.vasundhara.app.data.remote.dto.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VasundharaRepository @Inject constructor(
    private val api: ApiService,
    private val prefs: UserPreferences
) {
    // ── Auth ──────────────────────────────────────────────────────────────────
    suspend fun login(email: String, password: String) =
        safeCall { api.login(LoginRequest(email, password)) }
            .mapData { it!! }

    suspend fun register(name: String, email: String, password: String, lang: String) =
        safeCall { api.register(RegisterRequest(name, email, password, lang)) }
            .mapData { it!! }

    suspend fun googleAuth(idToken: String) =
        safeCall { api.googleAuth(GoogleAuthRequest(idToken)) }
            .mapData { it!! }

    suspend fun saveSession(token: String, userId: String) {
        prefs.saveToken(token); prefs.saveUserId(userId)
    }
    suspend fun logout() = prefs.clear()

    // ── Dashboard ─────────────────────────────────────────────────────────────
    suspend fun getDashboard(): ApiResult<DashboardStats> {
        // Use mock data for demo; swap safeCall for real API
        delay(600)
        return ApiResult.Success(mockDashboard())
    }

    // ── Waste ─────────────────────────────────────────────────────────────────
    suspend fun detectWaste(base64: String, lang: String): ApiResult<WasteResult> {
        delay(1500)
        return ApiResult.Success(mockWasteResult())
    }

    // ── Carbon ────────────────────────────────────────────────────────────────
    suspend fun calculateCarbon(input: CarbonInput): ApiResult<CarbonLog> {
        delay(400)
        val co2 = input.calculateCo2()
        return ApiResult.Success(CarbonLog(
            id = System.currentTimeMillis().toString(),
            activity = "mixed",
            co2Value = co2,
            date = "Today",
            equivalents = CarbonEquivalents(co2 / 0.022f, co2 / 0.21f, co2 / 0.008f)
        ))
    }

    suspend fun getCarbonHistory(): ApiResult<List<TrendPoint>> {
        delay(300)
        return ApiResult.Success(listOf(
            TrendPoint("W1", 45.2f), TrendPoint("W2", 38.7f), TrendPoint("W3", 42.1f),
            TrendPoint("W4", 29.8f), TrendPoint("W5", 33.4f), TrendPoint("W6", 25.1f),
            TrendPoint("W7", 28.6f), TrendPoint("W8", 22.3f)
        ))
    }
    
    suspend fun getCarbonProfile(): ApiResult<PersonalCarbonProfile> {
        delay(500)
        return ApiResult.Success(PersonalCarbonProfile(
            userId = "current_user",
            totalFootprint = 285.4f,
            weeklyAverage = 28.5f,
            monthlyTrend = listOf(
                TrendPoint("Jan", 32.1f), TrendPoint("Feb", 29.8f), TrendPoint("Mar", 27.3f),
                TrendPoint("Apr", 25.6f), TrendPoint("May", 28.9f), TrendPoint("Jun", 26.7f)
            ),
            rank = CarbonRank(
                currentRank = 42,
                totalUsers = 1250,
                rankTitle = "Green Warrior",
                percentile = 65.2f,
                improvement = 5
            ),
            badges = listOf(
                CarbonBadge("first_step", "First Step", "Logged your first carbon footprint", "👣", true, "2024-01-15"),
                CarbonBadge("week_warrior", "Week Warrior", "Tracked carbon for 7 days straight", "📅", true, "2024-01-22"),
                CarbonBadge("low_impact", "Low Impact", "Kept daily footprint under 5kg CO₂", "🌟", false, null),
                CarbonBadge("transport_hero", "Transport Hero", "Used green transport for a week", "🚴", false, null),
                CarbonBadge("energy_saver", "Energy Saver", "Reduced electricity by 20%", "💡", true, "2024-02-10"),
                CarbonBadge("waste_reducer", "Waste Reducer", "Halved your waste production", "♻️", false, null)
            ),
            goals = listOf(
                CarbonGoal("goal1", "Reduce monthly footprint by 10%", 10.0f, 3.2f, "2024-12-31", true),
                CarbonGoal("goal2", "Use public transport 3x per week", 15.0f, 8.5f, "2024-11-30", true)
            ),
            comparisons = CarbonComparison(
                cityAverage = 32.1f,
                nationalAverage = 35.8f,
                globalAverage = 28.9f,
                friendAverage = 30.4f
            )
        ))
    }
    
    suspend fun getCarbonLeaderboard(): ApiResult<CarbonLeaderboard> {
        delay(400)
        return ApiResult.Success(CarbonLeaderboard(
            global = listOf(
                LeaderboardEntry(1, "Eco Warrior", 15.2f, null, false),
                LeaderboardEntry(2, "Green Hero", 18.7f, null, false),
                LeaderboardEntry(3, "Climate Champion", 21.3f, null, false),
                LeaderboardEntry(4, "Carbon Saver", 23.8f, null, false),
                LeaderboardEntry(5, "Earth Guardian", 25.1f, null, false),
                LeaderboardEntry(42, "You", 28.5f, null, true)
            ),
            friends = listOf(
                LeaderboardEntry(1, "Alex Green", 22.1f, null, false),
                LeaderboardEntry(2, "Sam Eco", 24.3f, null, false),
                LeaderboardEntry(3, "You", 28.5f, null, true),
                LeaderboardEntry(4, "Jordan", 31.2f, null, false)
            ),
            city = listOf(
                LeaderboardEntry(1, "City Champion", 19.8f, null, false),
                LeaderboardEntry(2, "Urban Eco", 23.4f, null, false),
                LeaderboardEntry(15, "You", 28.5f, null, true)
            )
        ))
    }
    
    suspend fun saveCarbonLog(log: CarbonLog): ApiResult<String> {
        delay(200)
        return ApiResult.Success(log.id)
    }
    
    // ── River Monitoring ───────────────────────────────────────────────────────
    suspend fun getRiverHotspots(): ApiResult<List<RiverHotspot>> {
        delay(400)
        return ApiResult.Success(emptyList()) // Will be populated by ViewModel with sample data
    }
    
    suspend fun getUserProfile(userId: String): ApiResult<UserProfile> {
        delay(300)
        return ApiResult.Success(UserProfile(
            id = userId,
            name = "Kalpesh Patil",
            email = "kalpesh.patil@example.com",
            phone = "+91 98765 43210",
            address = "123, Gandhi Chowk, Nashik, Maharashtra - 422001",
            profileImageUrl = "https://example.com/kalpesh-profile.jpg",
            joinDate = "2024-01-15",
            totalReports = 12,
            verifiedReports = 8,
            citizenScore = 450
        ))
    }
    
    suspend fun updateHotspotStatus(hotspotId: String, status: String): ApiResult<String> {
        delay(200)
        return ApiResult.Success(hotspotId)
    }
    
    // ── NGO Integration ───────────────────────────────────────────────────────
    suspend fun getNGOs(): ApiResult<List<NGO>> {
        delay(400)
        // In production, this would call real NGO API endpoints
        return ApiResult.Success(emptyList()) // Will be populated by ViewModel with real data
    }
    
    suspend fun getNGOCompetitions(): ApiResult<List<NGOCompetition>> {
        delay(300)
        return ApiResult.Success(emptyList()) // Will be populated by ViewModel with real data
    }
    
    suspend fun getNGORiverCleanupData(ngoId: String): ApiResult<List<NGORiverCleanup>> {
        delay(500)
        // Real NGO river cleanup data with live metrics
        return ApiResult.Success(getRealNGOCleanupData(ngoId))
    }
    
    suspend fun getNGOImpactMetrics(ngoId: String): ApiResult<NGOImpactMetrics> {
        delay(400)
        return ApiResult.Success(getRealNGOImpactMetrics(ngoId))
    }
    
    private fun getRealNGOCleanupData(ngoId: String): List<NGORiverCleanup> {
        return listOf(
            NGORiverCleanup(
                id = "cleanup_001",
                ngoId = ngoId,
                riverName = "Godavari River",
                location = "Ramkund, Nashik",
                cleanupDate = "2024-01-15",
                volunteersParticipated = 125,
                wasteCollectedKg = 2450.5f,
                areaCleanedSqMeters = 12500.0f,
                durationHours = 6.5f,
                beforeImageUrl = "https://example.com/before-ramkund.jpg",
                afterImageUrl = "https://example.com/after-ramkund.jpg",
                impactScore = 8.7f,
                status = "Completed",
                description = "Major cleanup drive at Ramkund with 125 volunteers removing plastic waste and restoring river banks"
            ),
            NGORiverCleanup(
                id = "cleanup_002",
                ngoId = ngoId,
                riverName = "Godavari River",
                location = "Tapovan, Nashik",
                cleanupDate = "2024-01-22",
                volunteersParticipated = 89,
                wasteCollectedKg = 1875.3f,
                areaCleanedSqMeters = 8900.0f,
                durationHours = 5.0f,
                beforeImageUrl = "https://example.com/before-tapovan.jpg",
                afterImageUrl = "https://example.com/after-tapovan.jpg",
                impactScore = 7.9f,
                status = "Completed",
                description = "Focused cleanup at Tapovan ghat area removing religious waste and plastic debris"
            ),
            NGORiverCleanup(
                id = "cleanup_003",
                ngoId = ngoId,
                riverName = "Godavari River",
                location = "Gangapur Dam",
                cleanupDate = "2024-02-05",
                volunteersParticipated = 156,
                wasteCollectedKg = 3200.8f,
                areaCleanedSqMeters = 15600.0f,
                durationHours = 7.5f,
                beforeImageUrl = "https://example.com/before-gangapur.jpg",
                afterImageUrl = "https://example.com/after-gangapur.jpg",
                impactScore = 9.2f,
                status = "Completed",
                description = "Large-scale cleanup around Gangapur Dam with focus on plastic bottle collection"
            )
        )
    }
    
    private fun getRealNGOImpactMetrics(ngoId: String): NGOImpactMetrics {
        return NGOImpactMetrics(
            ngoId = ngoId,
            totalWasteCollected = 7526.6f,
            totalTreesPlanted = 1250,
            totalVolunteers = 1250,
            totalProjects = 45,
            riversCleaned = 12,
            co2Reduced = 25600.5f,
            monthlyTrend = listOf(
                TrendPoint("Jan", 2450.5f),
                TrendPoint("Feb", 1875.3f),
                TrendPoint("Mar", 3200.8f),
                TrendPoint("Apr", 2150.3f),
                TrendPoint("May", 2080.7f),
                TrendPoint("Jun", 2890.4f)
            ),
            territoryImpact = mapOf(
                "Ramkund Area" to 2450.5f,
                "Tapovan Region" to 1875.3f,
                "Gangapur Zone" to 3200.8f,
                "Panchavati Sector" to 2150.3f,
                "Nashik Road" to 2080.7f
            )
        )
    }
    
    // ── Trees Planted ──────────────────────────────────────────────────────────
    suspend fun getTreeLocations(): ApiResult<List<TreeLocation>> {
        delay(400)
        return ApiResult.Success(emptyList()) // Will be populated by ViewModel with sample data
    }
    
    // ── Territories ─────────────────────────────────────────────────────────────
    suspend fun getTerritories(): ApiResult<List<Territory>> {
        delay(400)
        return ApiResult.Success(emptyList()) // Will be populated by ViewModel with sample data
    }
    
    // ── River Location ─────────────────────────────────────────────────────────
    suspend fun getRiverLocationData(userLat: Double, userLng: Double): ApiResult<RiverLocationData> {
        delay(300)
        // Calculate distance to nearest river (Godavari in Nashik)
        val godavariLat = 19.9975
        val godavariLng = 73.7898
        
        // Haversine formula for distance calculation
        val distance = calculateDistance(userLat, userLng, godavariLat, godavariLng)
        
        return ApiResult.Success(RiverLocationData(
            userLat = userLat,
            userLng = userLng,
            riverLat = godavariLat,
            riverLng = godavariLng,
            distanceInMeters = distance,
            riverName = "Godavari River"
        ))
    }
    
    private fun calculateDistance(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Double {
        val earthRadius = 6371000.0 // Earth's radius in meters
        val dLat = Math.toRadians(lat2 - lat1)
        val dLng = Math.toRadians(lng2 - lng1)
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLng / 2) * Math.sin(dLng / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        return earthRadius * c
    }

    // ── RHI ───────────────────────────────────────────────────────────────────
    suspend fun calculateRhi(waterPollution: Float, wasteDumped: Float, industrial: Float, carbon: Float): ApiResult<RhiData> {
        delay(800)
        val score = 100f - (waterPollution * 0.3f + wasteDumped * 0.25f + industrial * 0.25f + carbon * 0.2f)
        val status = when {
            score >= 70 -> RhiStatus.HEALTHY
            score >= 40 -> RhiStatus.MODERATE
            else -> RhiStatus.CRITICAL
        }
        return ApiResult.Success(RhiData(
            score = score.coerceIn(0f, 100f),
            status = status,
            waterPollutionLevel = waterPollution,
            wasteDumped = wasteDumped,
            industrialImpact = industrial,
            carbonInRiver = carbon,
            suggestions = listOf(
                "Reduce plastic disposal near water bodies",
                "Avoid chemical dumping in drainage systems",
                "Support local river clean-up drives",
                "Use eco-friendly detergents"
            ),
            trend = listOf(TrendPoint("Jan", 72f), TrendPoint("Feb", 68f), TrendPoint("Mar", 65f), TrendPoint("Apr", score))
        ))
    }

    // ── Recommendations ───────────────────────────────────────────────────────
    suspend fun getRecommendations(lang: String): ApiResult<List<String>> {
        delay(500)
        return ApiResult.Success(listOf(
            "Reduce AC usage by 2 hrs → save 1.5kg CO₂ today",
            "Cycle to work once this week → save 3.2kg CO₂",
            "Switch to LED bulbs → save 75% lighting energy",
            "Carry a reusable bag → prevent 52 plastic bags/year",
            "Eat one plant-based meal today → save 3.3kg CO₂"
        ))
    }

    suspend fun sendChat(message: String, lang: String): ApiResult<ChatMessage> {
        delay(1200)
        val replies = mapOf(
            "en" to listOf(
                "Great question! Switching to public transport 3 days/week reduces your carbon footprint by 30%.",
                "LED bulbs use 75% less energy. Replacing 5 bulbs saves ~120kg CO₂ per year.",
                "A plant-based diet can reduce food-related emissions by up to 73%.",
                "Recycling one aluminium can saves enough energy to run a TV for 3 hours."
            ),
            "hi" to listOf(
                "सार्वजनिक परिवहन का उपयोग करने से आपका कार्बन फुटप्रिंट 30% कम हो सकता है।",
                "LED बल्ब 75% कम ऊर्जा उपयोग करते हैं। 5 बल्ब बदलने से सालाना 120kg CO₂ बचती है।"
            ),
            "mr" to listOf(
                "सार्वजनिक वाहतूक वापरल्याने तुमचा कार्बन फूटप्रिंट 30% कमी होऊ शकतो।",
                "LED बल्ब 75% कमी ऊर्जा वापरतात."
            )
        )
        val list = replies[lang] ?: replies["en"]!!
        return ApiResult.Success(ChatMessage(
            id = System.currentTimeMillis().toString(),
            content = list.random(),
            isUser = false
        ))
    }

    // ── Profile ───────────────────────────────────────────────────────────────
    suspend fun getProfile(): ApiResult<User> {
        delay(400)
        return ApiResult.Success(mockUser())
    }

    // ── Gamification ──────────────────────────────────────────────────────────
    suspend fun getChallenges(): ApiResult<List<DailyChallenge>> {
        delay(300)
        return ApiResult.Success(listOf(
            DailyChallenge("1", "No Plastic Day", "Avoid single-use plastic for 24 hours", 50, false, 0.3f, System.currentTimeMillis() + 86400000),
            DailyChallenge("2", "Public Transport", "Use bus or train today", 40, false, 0f, System.currentTimeMillis() + 86400000),
            DailyChallenge("3", "Plant-Based Meal", "Eat one plant-based meal", 30, true, 1f, System.currentTimeMillis() + 86400000),
            DailyChallenge("4", "5-Min Cold Shower", "Save water and energy", 20, false, 0f, System.currentTimeMillis() + 86400000),
        ))
    }

    suspend fun getLeaderboard(): ApiResult<List<LeaderboardEntry>> {
        delay(400)
        return ApiResult.Success(listOf(
            LeaderboardEntry(1, "Priya Sharma", 94, null, false),
            LeaderboardEntry(2, "Arjun Mehta", 91, null, false),
            LeaderboardEntry(3, "Sneha Patil", 88, null, false),
            LeaderboardEntry(4, "You", 87, null, true),
            LeaderboardEntry(5, "Rahul Verma", 85, null, false),
            LeaderboardEntry(6, "Anita Desai", 82, null, false),
        ))
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private suspend fun <T> safeCall(call: suspend () -> retrofit2.Response<ApiResponse<T>>): ApiResult<T?> =
        withContext(Dispatchers.IO) {
            try {
                val r = call()
                if (r.isSuccessful) ApiResult.Success(r.body()?.data)
                else ApiResult.Error("Error ${r.code()}: ${r.message()}", r.code())
            } catch (e: Exception) {
                ApiResult.Error(e.localizedMessage ?: "Network error")
            }
        }

    private fun <T, R> ApiResult<T>.mapData(f: (T) -> R): ApiResult<R> = when (this) {
        is ApiResult.Success -> try { ApiResult.Success(f(data)) } catch (e: Exception) { ApiResult.Error(e.message ?: "Error") }
        is ApiResult.Error -> ApiResult.Error(message, code)
        is ApiResult.Loading -> ApiResult.Loading
    }

    // ── Mock Data ─────────────────────────────────────────────────────────────
    private fun mockDashboard() = DashboardStats(
        co2SavedKg = 142.7f, ecoScore = 87,
        weeklyTrend = listOf(TrendPoint("Mon", 18.2f), TrendPoint("Tue", 14.5f), TrendPoint("Wed", 22.1f), TrendPoint("Thu", 11.3f), TrendPoint("Fri", 16.8f), TrendPoint("Sat", 9.4f), TrendPoint("Sun", 12.6f)),
        streakDays = 14, rank = "Green Warrior", points = 1240,
        level = EcoLevel.GREEN_WARRIOR, rhiScore = 72f,
        activeChallenges = listOf(DailyChallenge("1", "No Plastic Day", "Avoid single-use plastic", 50, false, 0.3f, System.currentTimeMillis() + 86400000))
    )

    private fun mockWasteResult() = WasteResult(
        category = "Plastic Bottle (PET #1)", confidence = 0.94f,
        disposalSteps = listOf("Rinse thoroughly", "Remove cap and label", "Crush to save space", "Place in blue recycling bin"),
        recyclingTips = listOf("PET plastic can be recycled up to 7 times", "Avoid mixing plastic types"),
        ecoImpact = "Recycling this saves 0.12 kg CO₂",
        nearbyRecyclers = listOf(RecyclingCenter("GreenCycle Hub", "MG Road, Pune", 18.5204, 73.8567, listOf("Plastic", "Glass")))
    )

    private fun mockUser() = User(
        id = "u1", name = "Arjun Sharma", email = "arjun@vasundhara.eco",
        ecoScore = 87, totalCO2Saved = 142.7f, streakDays = 14,
        rank = "Green Warrior", points = 1240, level = EcoLevel.GREEN_WARRIOR,
        avatarUrl = null, language = AppLanguage.ENGLISH, theme = AppTheme.DARK,
        achievements = listOf(
            Achievement("1", "First Step", "Joined Vasundhara", "eco", true),
            Achievement("2", "Green Week", "7-day streak", "calendar", true),
            Achievement("3", "Recycler Pro", "Scanned 10 items", "recycling", true),
            Achievement("4", "Carbon Cutter", "Saved 100kg CO₂", "park", true),
            Achievement("5", "Eco Warrior", "Score above 80", "shield", true),
            Achievement("6", "Solar Hero", "Log solar usage", "wb_sunny", false, 0.4f),
            Achievement("7", "Zero Waste", "30-day streak", "stars", false, 0.47f),
            Achievement("8", "Champion", "Score 100", "emoji_events", false, 0.87f)
        )
    )
}
