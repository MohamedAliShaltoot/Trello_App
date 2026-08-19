package com.example.trello.presentation.ai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trello.domain.repository.AiRepository
import com.example.trello.domain.usecase.InteractWithAiUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val isError: Boolean = false
)

data class AiAssistantUiState(
    val messages: List<ChatMessage> = emptyList(),
    val isLoading: Boolean = false
)

@HiltViewModel
class AiAssistantViewModel @Inject constructor(
    private val interactWithAiUseCase: InteractWithAiUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AiAssistantUiState())
    val uiState: StateFlow<AiAssistantUiState> = _uiState.asStateFlow()

    fun sendMessage(prompt: String) {
        if (prompt.isBlank()) return
        
        // Add user message and set loading
        val userMsg = ChatMessage(text = prompt, isUser = true)
        _uiState.value = _uiState.value.copy(
            messages = _uiState.value.messages + userMsg,
            isLoading = true
        )
        
        viewModelScope.launch {
            val result = interactWithAiUseCase(prompt)
            result.onSuccess { response ->
                val aiMsg = ChatMessage(text = response, isUser = false)
                _uiState.value = _uiState.value.copy(
                    messages = _uiState.value.messages + aiMsg,
                    isLoading = false
                )
            }.onFailure { error ->
                val errorMsg = ChatMessage(
                    text = "Error: ${error.localizedMessage ?: "Unknown error occurred"}",
                    isUser = false,
                    isError = true
                )
                _uiState.value = _uiState.value.copy(
                    messages = _uiState.value.messages + errorMsg,
                    isLoading = false
                )
            }
        }
    }

    fun reset() {
        _uiState.value = AiAssistantUiState()
    }
}
