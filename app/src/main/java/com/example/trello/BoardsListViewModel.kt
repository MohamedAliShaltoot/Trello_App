package com.example.trello


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trello.data.local.entity.BoardEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BoardsListViewModel @Inject constructor(
    private val repository: TrelloRepository
) : ViewModel() {

    val boards: StateFlow<List<BoardEntity>> =
        repository.getBoards()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun addBoard(title: String) {
        if (title.isBlank()) return
        viewModelScope.launch { repository.createBoard(title) }
    }
}