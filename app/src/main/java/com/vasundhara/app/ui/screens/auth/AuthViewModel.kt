package com.vasundhara.app.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vasundhara.app.data.model.ApiResult
import com.vasundhara.app.data.repository.VasundharaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthUiState(
    val isLoading: Boolean = false,
    val isAuthenticated: Boolean = false,
    val isNewUser: Boolean = false,
    val error: String? = null,
    val isLoginMode: Boolean = true
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repo: VasundharaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) { _uiState.value = _uiState.value.copy(error = "Please fill all fields"); return }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            when (val r = repo.login(email, password)) {
                is ApiResult.Success -> {
                    repo.saveSession(r.data.token, r.data.user.id)
                    _uiState.value = _uiState.value.copy(isLoading = false, isAuthenticated = true, isNewUser = false)
                }
                is ApiResult.Error -> _uiState.value = _uiState.value.copy(isLoading = false, error = r.message)
                else -> Unit
            }
        }
    }

    fun register(name: String, email: String, password: String) {
        if (name.isBlank() || email.isBlank() || password.length < 8) {
            _uiState.value = _uiState.value.copy(error = "Name, valid email and 8+ char password required"); return
        }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            when (val r = repo.register(name, email, password, "en")) {
                is ApiResult.Success -> {
                    repo.saveSession(r.data.token, r.data.user.id)
                    _uiState.value = _uiState.value.copy(isLoading = false, isAuthenticated = true, isNewUser = true)
                }
                is ApiResult.Error -> _uiState.value = _uiState.value.copy(isLoading = false, error = r.message)
                else -> Unit
            }
        }
    }

    fun googleSignIn() {
        // Trigger Google Sign-In flow from Activity — handled via ActivityResultLauncher
        // For demo: simulate success
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            kotlinx.coroutines.delay(1000)
            _uiState.value = _uiState.value.copy(isLoading = false, isAuthenticated = true, isNewUser = false)
        }
    }

    fun toggleMode() = run { _uiState.value = _uiState.value.copy(isLoginMode = !_uiState.value.isLoginMode, error = null) }
}
