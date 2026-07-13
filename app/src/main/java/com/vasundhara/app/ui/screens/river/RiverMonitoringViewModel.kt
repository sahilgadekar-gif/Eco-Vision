package com.vasundhara.app.ui.screens.river

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vasundhara.app.data.model.*
import com.vasundhara.app.data.repository.VasundharaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RiverMonitoringUiState(
    val hotspots: List<RiverHotspot> = emptyList(),
    val selectedUser: UserProfile? = null,
    val isLoading: Boolean = true,
    val filter: String = "All",
    val stats: Map<String, Any> = emptyMap()
)

@HiltViewModel
class RiverMonitoringViewModel @Inject constructor(
    private val repo: VasundharaRepository
) : ViewModel() {
    private val _state = MutableStateFlow(RiverMonitoringUiState())
    val state: StateFlow<RiverMonitoringUiState> = _state.asStateFlow()
    
    init {
        loadRiverHotspots()
    }
    
    private fun loadRiverHotspots() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            
            val result = repo.getRiverHotspots()
            val hotspots = when (result) {
                is ApiResult.Success -> result.data
                is ApiResult.Error -> {
                    // Return sample data for demo
                    getSampleHotspots()
                }
                else -> emptyList()
            }
            
            _state.value = _state.value.copy(
                hotspots = hotspots.filter { hotspot ->
                    _state.value.filter == "All" || hotspot.status == _state.value.filter
                },
                isLoading = false
            )
        }
    }
    
    fun refreshHotspots() {
        loadRiverHotspots()
    }
    
    fun updateFilter(filter: String) {
        _state.value = _state.value.copy(filter = filter)
        loadRiverHotspots()
    }
    
    fun getUserProfile(userId: String) {
        viewModelScope.launch {
            val result = repo.getUserProfile(userId)
            val user = when (result) {
                is ApiResult.Success -> result.data
                is ApiResult.Error -> {
                    // Return sample user for demo
                    getSampleUser(userId)
                }
                else -> null
            }
            
            _state.value = _state.value.copy(selectedUser = user)
        }
    }
    
    fun clearSelectedUser() {
        _state.value = _state.value.copy(selectedUser = null)
    }
    
    fun updateHotspotStatus(hotspotId: String, status: String) {
        viewModelScope.launch {
            repo.updateHotspotStatus(hotspotId, status)
            loadRiverHotspots() // Refresh the list
        }
    }
    
    private fun getSampleHotspots(): List<RiverHotspot> {
        return listOf(
            RiverHotspot(
                id = "1",
                location = "Godavari Ram Kund",
                description = "Severe river pollution detected at Godavari Ram Kund. Large amounts of garbage including plastic waste, religious idols, and debris are floating in the water. The water appears murky and dark, indicating high levels of contamination. Immediate cleanup action required by Municipal Corporation. Waste includes plastic bags, bottles, discarded clothing, organic matter, and construction debris. The banks are heavily littered with piles of trash extending into the background. This poses a serious environmental and health hazard to the local community.",
                imageUrl = "https://example.com/godavari-ram-kund-pollution.jpg",
                reporterName = "Kalpesh Patil",
                reporterId = "USR2024KP001",
                status = "Pending Cleanup",
                severity = "High",
                coordinates = Pair(19.8762, 75.3433),
                municipalNotified = true
            ),
            RiverHotspot(
                id = "2",
                location = "Godavari Tapovan",
                description = "River pollution hotspot identified at Godavari Tapovan area. Significant accumulation of waste materials including plastic bottles, food wrappers, and religious offerings. Water quality appears degraded with visible floating debris. The area shows signs of neglect with waste being regularly dumped along the riverbanks. Environmental impact assessment shows high contamination levels requiring immediate municipal intervention.",
                imageUrl = "https://example.com/godavari-tapovan-pollution.jpg",
                reporterName = "Kalpesh Patil",
                reporterId = "USR2024KP001",
                status = "Pending Cleanup",
                severity = "High",
                coordinates = Pair(19.8823, 75.3567),
                municipalNotified = true
            ),
            RiverHotspot(
                id = "3",
                location = "Nashik Road Bridge",
                description = "Moderate pollution levels detected near Nashik Road bridge crossing. Plastic waste and organic debris accumulated along riverbanks. Water appears slightly turbid but less severe than other hotspots. Regular monitoring recommended.",
                imageUrl = "https://example.com/nashik-road-bridge.jpg",
                reporterName = "Priya Sharma",
                reporterId = "USR2024PS002",
                status = "In Progress",
                severity = "Medium",
                coordinates = Pair(19.8912, 75.3678),
                municipalNotified = false
            )
        )
    }
    
    private fun getSampleUser(userId: String): UserProfile {
        return UserProfile(
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
        )
    }
}
