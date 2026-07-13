package com.vasundhara.app.ui.screens.rhi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vasundhara.app.data.model.*
import com.vasundhara.app.data.repository.VasundharaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RhiInputs(val waterPollution: Float = 30f, val wasteDumped: Float = 20f, val industrial: Float = 25f, val carbon: Float = 15f)
data class RhiUiState(val isLoading: Boolean = false, val inputs: RhiInputs = RhiInputs(), val result: RhiData? = null, val error: String? = null)

@HiltViewModel
class RhiViewModel @Inject constructor(private val repo: VasundharaRepository) : ViewModel() {
    private val _state = MutableStateFlow(RhiUiState())
    val state: StateFlow<RhiUiState> = _state.asStateFlow()

    fun update(water: Float?, waste: Float?, industrial: Float?, carbon: Float?) {
        val cur = _state.value.inputs
        _state.value = _state.value.copy(inputs = cur.copy(
            waterPollution = water ?: cur.waterPollution,
            wasteDumped = waste ?: cur.wasteDumped,
            industrial = industrial ?: cur.industrial,
            carbon = carbon ?: cur.carbon
        ))
    }

    fun calculate() = viewModelScope.launch {
        _state.value = _state.value.copy(isLoading = true)
        val i = _state.value.inputs
        when (val r = repo.calculateRhi(i.waterPollution, i.wasteDumped, i.industrial, i.carbon)) {
            is ApiResult.Success -> _state.value = _state.value.copy(isLoading = false, result = r.data)
            is ApiResult.Error   -> _state.value = _state.value.copy(isLoading = false, error = r.message)
            else -> Unit
        }
    }
}
