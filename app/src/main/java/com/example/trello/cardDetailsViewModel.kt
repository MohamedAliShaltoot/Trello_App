package com.example.trello


import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trello.data.local.entity.CardEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CardDetailViewModel @Inject constructor(
    private val repository: TrelloRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val cardId: Long = checkNotNull(savedStateHandle["cardId"])

    val card: StateFlow<CardEntity?> =
        repository.getCardById(cardId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    fun saveChanges(newTitle: String, newDescription: String) {
        val current = card.value ?: return
        if (newTitle.isBlank()) return
        viewModelScope.launch {
            repository.updateCardDetails(current, newTitle.trim(), newDescription.trim())
        }
    }

    fun deleteCard(onDeleted: () -> Unit) {
        val current = card.value ?: return
        viewModelScope.launch {
            repository.deleteCard(current)
            onDeleted()
        }
    }
}