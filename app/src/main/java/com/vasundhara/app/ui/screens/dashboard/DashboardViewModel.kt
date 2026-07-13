package com.vasundhara.app.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vasundhara.app.data.model.*
import com.vasundhara.app.data.repository.VasundharaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DashboardUiState(
    val isLoading: Boolean = true,
    val stats: DashboardStats? = null,
    val error: String? = null
)

@HiltViewModel
class DashboardViewModel @Inject constructor(private val repo: VasundharaRepository) : ViewModel() {
    private val _state = MutableStateFlow(DashboardUiState())
    val state: StateFlow<DashboardUiState> = _state.asStateFlow()

    init { load() }

    fun load() = viewModelScope.launch {
        _state.value = DashboardUiState(isLoading = true)
        when (val r = repo.getDashboard()) {
            is ApiResult.Success -> _state.value = DashboardUiState(stats = r.data)
            is ApiResult.Error   -> _state.value = DashboardUiState(error = r.message)
            else -> Unit
        }
    }
}
