package com.vasundhara.app.ui.screens.ai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vasundhara.app.data.local.UserPreferences
import com.vasundhara.app.data.model.*
import com.vasundhara.app.data.repository.VasundharaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AiUiState(
    val messages: List<ChatMessage> = listOf(ChatMessage("welcome", "Hi! I'm Vasundhara AI 🌿 Ask me anything about sustainability, carbon footprint, or eco-friendly living. I support English, Hindi & Marathi!", false)),
    val isTyping: Boolean = false,
    val input: String = ""
)

@HiltViewModel
class AiViewModel @Inject constructor(private val repo: VasundharaRepository, private val prefs: UserPreferences) : ViewModel() {
    private val _state = MutableStateFlow(AiUiState())
    val state: StateFlow<AiUiState> = _state.asStateFlow()

    fun updateInput(t: String) { _state.value = _state.value.copy(input = t) }

    fun send() {
        val text = _state.value.input.trim(); if (text.isBlank()) return
        val userMsg = ChatMessage(System.currentTimeMillis().toString(), text, true)
        _state.value = _state.value.copy(messages = _state.value.messages + userMsg, input = "", isTyping = true)
        viewModelScope.launch {
            val lang = prefs.language.firstOrNull()?.code ?: "en"
            when (val r = repo.sendChat(text, lang)) {
                is ApiResult.Success -> _state.value = _state.value.copy(messages = _state.value.messages + r.data, isTyping = false)
                is ApiResult.Error   -> _state.value = _state.value.copy(messages = _state.value.messages + ChatMessage("err_${System.currentTimeMillis()}", "Sorry, I couldn't connect. Please try again.", false), isTyping = false)
                else -> Unit
            }
        }
    }

    fun sendQuick(prompt: String) { _state.value = _state.value.copy(input = prompt); send() }
}
