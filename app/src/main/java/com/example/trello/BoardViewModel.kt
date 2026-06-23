package com.example.trello


import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trello.data.local.entity.CardEntity
import com.example.trello.data.local.entity.CardListEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BoardViewModel @Inject constructor(
    private val repository: TrelloRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val boardId: Long = checkNotNull(savedStateHandle["boardId"])

    val boardContents: StateFlow<List<ListWithCards>> =
        repository.getBoardContents(boardId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun addList(title: String) {
        if (title.isBlank()) return
        viewModelScope.launch { repository.createList(boardId, title) }
    }

    fun addCard(listId: Long, title: String) {
        if (title.isBlank()) return
        viewModelScope.launch { repository.createCard(listId, title) }
    }

    fun deleteCard(card: CardEntity) {
        viewModelScope.launch { repository.deleteCard(card) }
    }

    fun deleteList(list: CardListEntity) {
        viewModelScope.launch { repository.deleteList(list) }
    }

    fun moveCard(card: CardEntity, targetListId: Long) {
        viewModelScope.launch {
            repository.moveCard(
                card = card,
                newListId = targetListId,
                newPosition = repository.getMaxPositionInList(targetListId) + 1.0
            )
        }
    }
}