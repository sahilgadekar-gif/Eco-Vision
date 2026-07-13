package com.vasundhara.app.ui.screens.gamification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vasundhara.app.data.model.*
import com.vasundhara.app.data.repository.VasundharaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GamificationUiState(
    val challenges: List<DailyChallenge> = emptyList(),
    val leaderboard: List<LeaderboardEntry> = emptyList(),
    val achievements: List<Achievement> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class GamificationViewModel @Inject constructor(private val repo: VasundharaRepository) : ViewModel() {
    private val _state = MutableStateFlow(GamificationUiState())
    val state: StateFlow<GamificationUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val challenges = (repo.getChallenges() as? ApiResult.Success)?.data ?: emptyList()
            val leaderboard = (repo.getLeaderboard() as? ApiResult.Success)?.data ?: emptyList()
            val profile = (repo.getProfile() as? ApiResult.Success)?.data
            _state.value = GamificationUiState(challenges = challenges, leaderboard = leaderboard, achievements = profile?.achievements ?: emptyList(), isLoading = false)
        }
    }

    fun complete(id: String) {
        _state.value = _state.value.copy(
            challenges = _state.value.challenges.map { if (it.id == id) it.copy(isCompleted = true, progress = 1f) else it }
        )
    }
}
