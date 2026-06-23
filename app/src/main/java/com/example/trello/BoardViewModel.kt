package com.example.trello

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    // Passed in via navigation arguments once you wire up Navigation Compose.
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
}