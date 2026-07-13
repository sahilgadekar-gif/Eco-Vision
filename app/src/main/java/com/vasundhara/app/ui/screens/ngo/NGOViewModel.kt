package com.vasundhara.app.ui.screens.ngo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vasundhara.app.data.model.*
import com.vasundhara.app.data.repository.VasundharaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NGOUiState(
    val ngos: List<NGO> = emptyList(),
    val competitions: List<NGOCompetition> = emptyList(),
    val selectedNGO: NGO? = null,
    val selectedCompetition: NGOCompetition? = null,
    val selectedGuideline: NGOSupportGuideline? = null,
    val isLoading: Boolean = true
)

@HiltViewModel
class NGOViewModel @Inject constructor(
    private val repo: VasundharaRepository
) : ViewModel() {
    private val _state = MutableStateFlow(NGOUiState())
    val state: StateFlow<NGOUiState> = _state.asStateFlow()
    
    init {
        loadNGOs()
        loadCompetitions()
    }
    
    private fun loadNGOs() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            
            val result = repo.getNGOs()
            val ngos = when (result) {
                is ApiResult.Success -> result.data
                is ApiResult.Error -> {
                    // Return real Maharashtra NGO data for demo
                    getRealMaharashtraNGOs()
                }
                else -> emptyList()
            }
            
            _state.value = _state.value.copy(
                ngos = ngos,
                isLoading = false
            )
        }
    }
    
    private fun loadCompetitions() {
        viewModelScope.launch {
            val result = repo.getNGOCompetitions()
            val competitions = when (result) {
                is ApiResult.Success -> result.data
                is ApiResult.Error -> {
                    // Return sample competition data
                    getRealCompetitions()
                }
                else -> emptyList()
            }
            
            _state.value = _state.value.copy(competitions = competitions)
        }
    }
    
    fun refreshNGOs() {
        loadNGOs()
        loadCompetitions()
    }
    
    fun selectNGO(ngo: NGO) {
        _state.value = _state.value.copy(selectedNGO = ngo.takeIf { it.id.isNotEmpty() })
    }
    
    fun selectCompetition(competition: NGOCompetition) {
        _state.value = _state.value.copy(selectedCompetition = competition.takeIf { it.id.isNotEmpty() })
    }
    
    fun selectSupport(guideline: NGOSupportGuideline) {
        _state.value = _state.value.copy(selectedGuideline = guideline.takeIf { it.id.isNotEmpty() })
    }
    
    private fun getRealMaharashtraNGOs(): List<NGO> {
        return listOf(
            NGO(
                id = "ngo_001",
                name = "Godavari River Conservation Trust",
                registrationNumber = "MH/1234/2015",
                establishedYear = 2015,
                location = "Ramkund, Nashik",
                city = "Nashik",
                state = "Maharashtra",
                latitude = 19.8762,
                longitude = 75.3433,
                contactEmail = "info@godavaritrust.org",
                contactPhone = "+91-253-2345678",
                website = "www.godavaritrust.org",
                description = "Dedicated to the conservation and restoration of the Godavari River and its tributaries in Nashik district. We organize regular cleanup drives, water quality monitoring, and community awareness programs.",
                focusAreas = listOf("River Conservation", "Water Quality", "Community Awareness", "Waste Management"),
                totalProjects = 45,
                activeProjects = 12,
                volunteersCount = 1250,
                rating = 4.8f,
                verified = true,
                logoUrl = "https://example.com/godavari-logo.png",
                coverImageUrl = "https://example.com/godavari-cover.jpg"
            ),
            NGO(
                id = "ngo_002",
                name = "Nashik Environmental Action Group",
                registrationNumber = "MH/5678/2018",
                establishedYear = 2018,
                location = "College Road, Nashik",
                city = "Nashik",
                state = "Maharashtra",
                latitude = 19.8823,
                longitude = 75.3567,
                contactEmail = "contact@nashikenviro.org",
                contactPhone = "+91-253-3456789",
                website = "www.nashikenviro.org",
                description = "A youth-led environmental organization working on sustainable development, waste management, and climate action in Nashik. We focus on creating awareness and implementing practical solutions.",
                focusAreas = listOf("Climate Action", "Waste Management", "Sustainable Development", "Youth Engagement"),
                totalProjects = 28,
                activeProjects = 8,
                volunteersCount = 890,
                rating = 4.6f,
                verified = true,
                logoUrl = "https://example.com/nashikenviro-logo.png",
                coverImageUrl = "https://example.com/nashikenviro-cover.jpg"
            ),
            NGO(
                id = "ngo_003",
                name = "Maharashtra River Foundation",
                registrationNumber = "MH/9012/2012",
                establishedYear = 2012,
                location = "CIDCO, Nashik",
                city = "Nashik",
                state = "Maharashtra",
                latitude = 19.8912,
                longitude = 75.3678,
                contactEmail = "info@maharashtrarivers.org",
                contactPhone = "+91-253-4567890",
                website = "www.maharashtrarivers.org",
                description = "State-level organization working on river conservation across Maharashtra. Our Nashik chapter focuses on Godavari river restoration, pollution control, and sustainable water management.",
                focusAreas = listOf("River Conservation", "Pollution Control", "Water Management", "Policy Advocacy"),
                totalProjects = 67,
                activeProjects = 15,
                volunteersCount = 2100,
                rating = 4.9f,
                verified = true,
                logoUrl = "https://example.com/maharashtrarivers-logo.png",
                coverImageUrl = "https://example.com/maharashtrarivers-cover.jpg"
            ),
            NGO(
                id = "ngo_004",
                name = "Green Nashik Initiative",
                registrationNumber = "MH/3456/2020",
                establishedYear = 2020,
                location = "Satpur, Nashik",
                city = "Nashik",
                state = "Maharashtra",
                latitude = 19.8654,
                longitude = 75.3321,
                contactEmail = "contact@greennashik.org",
                contactPhone = "+91-253-5678901",
                website = "www.greennashik.org",
                description = "Focused on urban greening, tree plantation, and creating green spaces in Nashik city. We work with local communities to increase green cover and improve air quality.",
                focusAreas = listOf("Tree Plantation", "Urban Greening", "Air Quality", "Community Gardens"),
                totalProjects = 23,
                activeProjects = 6,
                volunteersCount = 650,
                rating = 4.5f,
                verified = false,
                logoUrl = "https://example.com/greennashik-logo.png",
                coverImageUrl = "https://example.com/greennashik-cover.jpg"
            ),
            NGO(
                id = "ngo_005",
                name = "Panchavati Biodiversity Conservation",
                registrationNumber = "MH/7890/2016",
                establishedYear = 2016,
                location = "Panchavati, Nashik",
                city = "Nashik",
                state = "Maharashtra",
                latitude = 19.8789,
                longitude = 75.3456,
                contactEmail = "info@panchavatibio.org",
                contactPhone = "+91-253-6789012",
                website = "www.panchavatibio.org",
                description = "Working on biodiversity conservation in the Panchavati area of Nashik. We protect native species, create awareness about local flora and fauna, and maintain ecological balance.",
                focusAreas = listOf("Biodiversity", "Native Species", "Ecological Balance", "Education"),
                totalProjects = 19,
                activeProjects = 5,
                volunteersCount = 420,
                rating = 4.4f,
                verified = true,
                logoUrl = "https://example.com/panchavatibio-logo.png",
                coverImageUrl = "https://example.com/panchavatibio-cover.jpg"
            ),
            NGO(
                id = "ngo_006",
                name = "Nashik Waste Warriors",
                registrationNumber = "MH/2345/2019",
                establishedYear = 2019,
                location = "Mhasrul, Nashik",
                city = "Nashik",
                state = "Maharashtra",
                latitude = 19.8876,
                longitude = 75.3543,
                contactEmail = "contact@wastewarriors.org",
                contactPhone = "+91-253-7890123",
                website = "www.wastewarriors.org",
                description = "Specialized in waste management and recycling solutions for Nashik city. We promote zero waste practices, conduct cleanup drives, and work with municipal authorities for better waste management.",
                focusAreas = listOf("Waste Management", "Recycling", "Zero Waste", "Cleanliness Drives"),
                totalProjects = 34,
                activeProjects = 9,
                volunteersCount = 780,
                rating = 4.7f,
                verified = true,
                logoUrl = "https://example.com/wastewarriors-logo.png",
                coverImageUrl = "https://example.com/wastewarriors-cover.jpg"
            )
        )
    }
    
    private fun getRealCompetitions(): List<NGOCompetition> {
        return listOf(
            NGOCompetition(
                id = "comp_001",
                name = "Nashik River Cleanup League 2024",
                description = "Territory-wise competition to clean Godavari river banks. Join teams from your area and compete for the biggest environmental impact!",
                startDate = "2024-01-01",
                endDate = "2024-12-31",
                type = "territory",
                territoryId = null,
                participants = getCompetitionParticipants(),
                prizes = listOf(
                    CompetitionPrize(1, "Environmental Champion Trophy", "₹50,000", "Cash prize + certificate + media recognition"),
                    CompetitionPrize(2, "Green Warrior Award", "₹30,000", "Cash prize + certificate"),
                    CompetitionPrize(3, "Eco Guardian Medal", "₹20,000", "Cash prize + certificate"),
                    CompetitionPrize(4, "Sustainability Star", "₹10,000", "Cash prize + certificate")
                ),
                rules = listOf(
                    "Teams must be from the same territory",
                    "Minimum 5 members per team",
                    "Activities must be documented with photos",
                    "Monthly progress reports required",
                    "Environmental impact will be measured",
                    "Fair play and community respect mandatory"
                ),
                status = "Active",
                currentLeaderboard = getLeaderboardEntries()
            ),
            NGOCompetition(
                id = "comp_002",
                name = "Maharashtra Green Marathon",
                description = "State-wide competition for maximum tree plantation and environmental activities. Represent your territory and compete for the top spot!",
                startDate = "2024-06-01",
                endDate = "2024-11-30",
                type = "state",
                territoryId = null,
                participants = getCompetitionParticipants(),
                prizes = listOf(
                    CompetitionPrize(1, "State Environmental Champion", "₹100,000", "Cash prize + state-level recognition"),
                    CompetitionPrize(2, "Maharashtra Green Star", "₹75,000", "Cash prize + recognition"),
                    CompetitionPrize(3, "Eco Warrior Maharashtra", "₹50,000", "Cash prize + recognition")
                ),
                rules = listOf(
                    "Open to all Maharashtra territories",
                    "Focus on tree plantation and green activities",
                    "Regular monitoring and reporting required",
                    "Collaboration with local NGOs encouraged",
                    "Sustainability of projects will be evaluated"
                ),
                status = "Active",
                currentLeaderboard = getLeaderboardEntries()
            ),
            NGOCompetition(
                id = "comp_003",
                name = "Ramkund Area Challenge",
                description = "Special competition focused on cleaning and maintaining the Ramkund area of Nashik. Local teams compete for the biggest impact in this sacred area.",
                startDate = "2024-03-01",
                endDate = "2024-08-31",
                type = "territory",
                territoryId = "territory_001",
                participants = getCompetitionParticipants().filter { it.territoryName == "Ramkund Area" },
                prizes = listOf(
                    CompetitionPrize(1, "Ramkund Guardian", "₹25,000", "Cash prize + special recognition"),
                    CompetitionPrize(2, "Sacred River Protector", "₹15,000", "Cash prize + recognition"),
                    CompetitionPrize(3, "Cultural Heritage Hero", "₹10,000", "Cash prize + recognition")
                ),
                rules = listOf(
                    "Focus on Ramkund area only",
                    "Respect cultural and religious sentiments",
                    "Coordinate with local authorities",
                    "Maintain cleanliness and hygiene",
                    "Educate visitors about environmental protection"
                ),
                status = "Active",
                currentLeaderboard = getLeaderboardEntries().filter { it.territory == "Ramkund Area" }
            )
        )
    }
    
    private fun getCompetitionParticipants(): List<CompetitionParticipant> {
        return listOf(
            CompetitionParticipant(
                userId = "user_001",
                userName = "Ramesh Patil",
                territoryName = "Ramkund Area",
                score = 2450.5f,
                rank = 1,
                activities = listOf(
                    UserActivity("act_001", "user_001", "River Cleanup", 150.0f, "Cleaned 500kg waste from Godavari", "Ramkund", "2024-01-15", null),
                    UserActivity("act_002", "user_001", "Tree Plantation", 100.0f, "Planted 50 trees", "Ramkund", "2024-01-20", null),
                    UserActivity("act_003", "user_001", "Community Awareness", 80.0f, "Organized awareness camp", "Ramkund", "2024-01-25", null)
                )
            ),
            CompetitionParticipant(
                userId = "user_002",
                userName = "Suresh Nimse",
                territoryName = "Tapovan Region",
                score = 2380.2f,
                rank = 2,
                activities = listOf(
                    UserActivity("act_004", "user_002", "River Cleanup", 140.0f, "Cleaned 450kg waste", "Tapovan", "2024-01-18", null),
                    UserActivity("act_005", "user_002", "Water Quality Testing", 120.0f, "Tested water parameters", "Tapovan", "2024-01-22", null),
                    UserActivity("act_006", "user_002", "Tree Plantation", 90.0f, "Planted 45 trees", "Tapovan", "2024-01-28", null)
                )
            ),
            CompetitionParticipant(
                userId = "user_003",
                userName = "Vikas Gadekar",
                territoryName = "Gangapur Zone",
                score = 2290.8f,
                rank = 3,
                activities = listOf(
                    UserActivity("act_007", "user_003", "Waste Management", 130.0f, "Organized waste segregation", "Gangapur", "2024-01-12", null),
                    UserActivity("act_008", "user_003", "River Cleanup", 110.0f, "Cleaned 400kg waste", "Gangapur", "2024-01-19", null),
                    UserActivity("act_009", "user_003", "Community Education", 85.0f, "Conducted environmental workshop", "Gangapur", "2024-01-26", null)
                )
            ),
            CompetitionParticipant(
                userId = "user_004",
                userName = "Anil Deshmukh",
                territoryName = "Panchavati Sector",
                score = 2150.3f,
                rank = 4,
                activities = listOf(
                    UserActivity("act_010", "user_004", "Biodiversity Conservation", 125.0f, "Protected native species", "Panchavati", "2024-01-14", null),
                    UserActivity("act_011", "user_004", "Tree Plantation", 95.0f, "Planted 48 trees", "Panchavati", "2024-01-21", null),
                    UserActivity("act_012", "user_004", "Cleanliness Drive", 80.0f, "Organized area cleanup", "Panchavati", "2024-01-27", null)
                )
            ),
            CompetitionParticipant(
                userId = "user_005",
                userName = "Mahesh Patil",
                territoryName = "Nashik Road",
                score = 2080.7f,
                rank = 5,
                activities = listOf(
                    UserActivity("act_013", "user_005", "Commercial Area Cleanup", 115.0f, "Cleaned commercial area", "Nashik Road", "2024-01-16", null),
                    UserActivity("act_014", "user_005", "Plastic Collection", 100.0f, "Collected 300kg plastic", "Nashik Road", "2024-01-23", null),
                    UserActivity("act_015", "user_005", "Tree Plantation", 85.0f, "Planted 42 trees", "Nashik Road", "2024-01-30", null)
                )
            )
        )
    }
    
    private fun getLeaderboardEntries(): List<LeaderboardEntry> {
        return getCompetitionParticipants().map { participant ->
            LeaderboardEntry(
                rank = participant.rank,
                name = participant.userName,
                ecoScore = participant.score,
                territory = participant.territoryName,
                isCurrentUser = participant.userId == "user_001"
            )
        }.sortedBy { it.rank }
    }
}
