package com.vasundhara.app.ui.screens.territory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vasundhara.app.data.model.Territory
import com.vasundhara.app.data.model.Coordinate
import com.vasundhara.app.data.repository.VasundharaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TerritoryUiState(
    val territories: List<Territory> = emptyList(),
    val selectedTerritory: Territory? = null,
    val totalTerritories: Int = 0,
    val totalUsers: Int = 0,
    val isLoading: Boolean = true
)

@HiltViewModel
class TerritoryViewModel @Inject constructor(
    private val repo: VasundharaRepository
) : ViewModel() {
    private val _state = MutableStateFlow(TerritoryUiState())
    val state: StateFlow<TerritoryUiState> = _state.asStateFlow()
    
    init {
        loadTerritories()
    }
    
    private fun loadTerritories() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            
            val result = repo.getTerritories()
            val territories = when (result) {
                is com.vasundhara.app.data.model.ApiResult.Success -> result.data
                is com.vasundhara.app.data.model.ApiResult.Error -> {
                    // Return sample data for demo
                    getSampleTerritories()
                }
                else -> emptyList()
            }
            
            _state.value = _state.value.copy(
                territories = territories,
                totalTerritories = territories.size,
                totalUsers = territories.map { it.ownerId }.distinct().size,
                isLoading = false
            )
        }
    }
    
    fun refreshTerritories() {
        loadTerritories()
    }
    
    fun selectTerritory(territory: Territory) {
        _state.value = _state.value.copy(selectedTerritory = territory)
    }
    
    private fun getSampleTerritories(): List<Territory> {
        return listOf(
            Territory(
                id = "territory_001",
                name = "Ramkund Area",
                ownerName = "Ramesh Patil",
                ownerId = "USR001",
                area = listOf(
                    Coordinate(19.8762, 75.3433),
                    Coordinate(19.8823, 75.3433),
                    Coordinate(19.8823, 75.3493),
                    Coordinate(19.8762, 75.3493)
                ),
                centerLat = 19.8793,
                centerLng = 75.3463,
                description = "Central Nashik area near Godavari Ram Kund",
                color = "#4CAF50",
                totalTrees = 156,
                population = 2500
            ),
            Territory(
                id = "territory_002", 
                name = "Tapovan Region",
                ownerName = "Suresh Nimse",
                ownerId = "USR002",
                area = listOf(
                    Coordinate(19.8823, 75.3567),
                    Coordinate(19.8883, 75.3567),
                    Coordinate(19.8883, 75.3627),
                    Coordinate(19.8823, 75.3627)
                ),
                centerLat = 19.8853,
                centerLng = 75.3597,
                description = "Northern Nashik region with high tree density",
                color = "#2196F3",
                totalTrees = 243,
                population = 1800
            ),
            Territory(
                id = "territory_003",
                name = "Gangapur Zone",
                ownerName = "Vikas Gadekar",
                ownerId = "USR003",
                area = listOf(
                    Coordinate(19.8912, 75.3678),
                    Coordinate(19.8972, 75.3678),
                    Coordinate(19.8972, 75.3738),
                    Coordinate(19.8912, 75.3738)
                ),
                centerLat = 19.8942,
                centerLng = 75.3708,
                description = "Eastern Nashik with mixed residential and commercial areas",
                color = "#FF9800",
                totalTrees = 189,
                population = 3200
            ),
            Territory(
                id = "territory_004",
                name = "Panchavati Sector",
                ownerName = "Anil Deshmukh",
                ownerId = "USR004",
                area = listOf(
                    Coordinate(19.8654, 75.3321),
                    Coordinate(19.8714, 75.3321),
                    Coordinate(19.8714, 75.3381),
                    Coordinate(19.8654, 75.3381)
                ),
                centerLat = 19.8684,
                centerLng = 75.3351,
                description = "Historical area with ancient temples and cultural heritage",
                color = "#9C27B0",
                totalTrees = 312,
                population = 4100
            ),
            Territory(
                id = "territory_005",
                name = "Nashik Road",
                ownerName = "Mahesh Patil",
                ownerId = "USR005",
                area = listOf(
                    Coordinate(19.8789, 75.3456),
                    Coordinate(19.8849, 75.3456),
                    Coordinate(19.8849, 75.3516),
                    Coordinate(19.8789, 75.3516)
                ),
                centerLat = 19.8819,
                centerLng = 75.3486,
                description = "Commercial hub with high traffic and pollution monitoring needs",
                color = "#F44336",
                totalTrees = 98,
                population = 5600
            ),
            Territory(
                id = "territory_006",
                name = "CIDCO Area",
                ownerName = "Rajesh Nimse",
                ownerId = "USR006",
                area = listOf(
                    Coordinate(19.8876, 75.3543),
                    Coordinate(19.8936, 75.3543),
                    Coordinate(19.8936, 75.3603),
                    Coordinate(19.8876, 75.3603)
                ),
                centerLat = 19.8906,
                centerLng = 75.3573,
                description = "Modern residential and industrial development zone",
                color = "#009688",
                totalTrees = 267,
                population = 7800
            ),
            Territory(
                id = "territory_007",
                name = "Mhasrul Region",
                ownerName = "Sanjay Gadekar",
                ownerId = "USR007",
                area = listOf(
                    Coordinate(19.8934, 75.3712),
                    Coordinate(19.8994, 75.3712),
                    Coordinate(19.8994, 75.3772),
                    Coordinate(19.8934, 75.3772)
                ),
                centerLat = 19.8964,
                centerLng = 75.3742,
                description = "Rural-urban fringe with agricultural lands",
                color = "#795548",
                totalTrees = 445,
                population = 1200
            ),
            Territory(
                id = "territory_008",
                name = "Satpur Zone",
                ownerName = "Prakash Deshmukh",
                ownerId = "USR008",
                area = listOf(
                    Coordinate(19.8712, 75.3389),
                    Coordinate(19.8772, 75.3389),
                    Coordinate(19.8772, 75.3449),
                    Coordinate(19.8712, 75.3449)
                ),
                centerLat = 19.8742,
                centerLng = 75.3419,
                description = "Industrial zone with high environmental monitoring requirements",
                color = "#607D8B",
                totalTrees = 178,
                population = 8900
            )
        )
    }
}
