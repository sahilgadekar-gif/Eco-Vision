package com.vasundhara.app.ui.screens.trees

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vasundhara.app.data.model.TreeLocation
import com.vasundhara.app.data.repository.VasundharaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TreesPlantedUiState(
    val trees: List<TreeLocation> = emptyList(),
    val totalTrees: Int = 0,
    val healthyTrees: Int = 0,
    val growingTrees: Int = 0,
    val needCareTrees: Int = 0,
    val isLoading: Boolean = true
)

@HiltViewModel
class TreesPlantedViewModel @Inject constructor(
    private val repo: VasundharaRepository
) : ViewModel() {
    private val _state = MutableStateFlow(TreesPlantedUiState())
    val state: StateFlow<TreesPlantedUiState> = _state.asStateFlow()
    
    init {
        loadTrees()
    }
    
    private fun loadTrees() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            
            val result = repo.getTreeLocations()
            val trees = when (result) {
                is com.vasundhara.app.data.model.ApiResult.Success -> result.data
                is com.vasundhara.app.data.model.ApiResult.Error -> {
                    // Return sample data for demo
                    getSampleTreeLocations()
                }
                else -> emptyList()
            }
            
            _state.value = _state.value.copy(
                trees = trees,
                totalTrees = trees.size,
                healthyTrees = trees.count { it.health == "Healthy" },
                growingTrees = trees.count { it.health == "Growing" },
                needCareTrees = trees.count { it.health == "Need Care" },
                isLoading = false
            )
        }
    }
    
    fun refreshTrees() {
        loadTrees()
    }
    
    private fun getSampleTreeLocations(): List<TreeLocation> {
        return listOf(
            TreeLocation(
                id = "tree_001",
                lat = 19.8762,
                lng = 75.3433,
                treeType = "Neem Tree",
                plantedBy = "Ramesh Patil",
                plantedDate = "2024-01-15",
                height = 2.5f,
                health = "Healthy",
                imageUrl = null
            ),
            TreeLocation(
                id = "tree_002",
                lat = 19.8823,
                lng = 75.3567,
                treeType = "Mango Tree",
                plantedBy = "Suresh Nimse",
                plantedDate = "2024-02-20",
                height = 1.8f,
                health = "Growing",
                imageUrl = null
            ),
            TreeLocation(
                id = "tree_003",
                lat = 19.8912,
                lng = 75.3678,
                treeType = "Banyan Tree",
                plantedBy = "Vikas Gadekar",
                plantedDate = "2024-03-10",
                height = 3.2f,
                health = "Healthy",
                imageUrl = null
            ),
            TreeLocation(
                id = "tree_004",
                lat = 19.8654,
                lng = 75.3321,
                treeType = "Peepal Tree",
                plantedBy = "Anil Deshmukh",
                plantedDate = "2024-01-25",
                height = 1.2f,
                health = "Need Care",
                imageUrl = null
            ),
            TreeLocation(
                id = "tree_005",
                lat = 19.8789,
                lng = 75.3456,
                treeType = "Teak Tree",
                plantedBy = "Mahesh Patil",
                plantedDate = "2024-04-05",
                height = 2.8f,
                health = "Healthy",
                imageUrl = null
            ),
            TreeLocation(
                id = "tree_006",
                lat = 19.8876,
                lng = 75.3543,
                treeType = "Gulmohar Tree",
                plantedBy = "Rajesh Nimse",
                plantedDate = "2024-02-15",
                height = 1.5f,
                health = "Growing",
                imageUrl = null
            ),
            TreeLocation(
                id = "tree_007",
                lat = 19.8934,
                lng = 75.3712,
                treeType = "Ashoka Tree",
                plantedBy = "Sanjay Gadekar",
                plantedDate = "2024-03-25",
                height = 2.1f,
                health = "Healthy",
                imageUrl = null
            ),
            TreeLocation(
                id = "tree_008",
                lat = 19.8712,
                lng = 75.3389,
                treeType = "Jamun Tree",
                plantedBy = "Prakash Deshmukh",
                plantedDate = "2024-01-30",
                height = 1.7f,
                health = "Need Care",
                imageUrl = null
            )
        )
    }
}
