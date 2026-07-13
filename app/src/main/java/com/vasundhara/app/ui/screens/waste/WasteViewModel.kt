package com.vasundhara.app.ui.screens.waste

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vasundhara.app.data.local.UserPreferences
import com.vasundhara.app.data.model.*
import com.vasundhara.app.data.repository.VasundharaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WasteUiState(
    val isAnalyzing: Boolean = false, 
    val result: WasteResult? = null, 
    val hasImage: Boolean = false, 
    val error: String? = null,
    val currentLocation: androidx.location.Location? = null,
    val riverLocationData: RiverLocationData? = null
)

@HiltViewModel
class WasteViewModel @Inject constructor(private val repo: VasundharaRepository, private val prefs: UserPreferences) : ViewModel() {
    private val _state = MutableStateFlow(WasteUiState())
    val state: StateFlow<WasteUiState> = _state.asStateFlow()

    fun capture() { 
        _state.value = _state.value.copy(hasImage = true, result = null); 
        getCurrentLocation()
        analyze() 
    }
    fun upload() { 
        _state.value = _state.value.copy(hasImage = true, result = null); 
        getCurrentLocation()
        analyze() 
    }
    fun reset() { 
        _state.value = WasteUiState() 
    }
    
    private fun getCurrentLocation() {
        // Simulate getting current location (in real app, use LocationManager)
        val mockLocation = androidx.location.Location("mock").apply {
            latitude = 19.8762
            longitude = 75.3433
        }
        
        _state.value = _state.value.copy(currentLocation = mockLocation)
        
        // Calculate distance to river
        viewModelScope.launch {
            val riverData = repo.getRiverLocationData(mockLocation.latitude, mockLocation.longitude)
            when (riverData) {
                is ApiResult.Success -> {
                    _state.value = _state.value.copy(riverLocationData = riverData.data)
                }
                else -> Unit
            }
        }
    }

    private fun analyze() = viewModelScope.launch {
        _state.value = _state.value.copy(isAnalyzing = true)
        val lang = prefs.language.firstOrNull()?.code ?: "en"
        when (val r = repo.detectWaste("mock_base64", lang)) {
            is ApiResult.Success -> _state.value = _state.value.copy(isAnalyzing = false, result = r.data)
            is ApiResult.Error   -> _state.value = _state.value.copy(isAnalyzing = false, error = r.message)
            else -> Unit
        }
    }
}
