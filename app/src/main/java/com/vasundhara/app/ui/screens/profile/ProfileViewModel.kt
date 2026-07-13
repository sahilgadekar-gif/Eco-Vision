package com.vasundhara.app.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vasundhara.app.data.model.*
import com.vasundhara.app.data.repository.VasundharaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(val isLoading: Boolean = true, val user: User? = null, val error: String? = null)

@HiltViewModel
class ProfileViewModel @Inject constructor(private val repo: VasundharaRepository) : ViewModel() {
    private val _state = MutableStateFlow(ProfileUiState())
    val state: StateFlow<ProfileUiState> = _state.asStateFlow()
    init { viewModelScope.launch { when (val r = repo.getProfile()) { is ApiResult.Success -> _state.value = ProfileUiState(user = r.data); is ApiResult.Error -> _state.value = ProfileUiState(error = r.message); else -> Unit } } }
}
