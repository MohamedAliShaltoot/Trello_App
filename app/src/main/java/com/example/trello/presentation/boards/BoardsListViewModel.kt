package com.example.trello.presentation.boards

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trello.domain.model.Board
import com.example.trello.domain.repository.TrelloRepository
import com.example.trello.presentation.boards.BoardsListContract.Effect
import com.example.trello.presentation.boards.BoardsListContract.Intent
import com.example.trello.presentation.boards.BoardsListContract.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BoardsListViewModel @Inject constructor(
    private val repository: TrelloRepository
) : ViewModel() {

    private val searchQuery = MutableStateFlow("")
    private val showCreateDialog = MutableStateFlow(false)

    private val boards: Flow<List<Board>> = searchQuery
        .debounce(250)
        .distinctUntilChanged()
        .flatMapLatest { query -> repository.observeBoards(query) }

    val state: StateFlow<State> = combine(
        boards, searchQuery, showCreateDialog
    ) { boardList, query, showDialog ->
        State(
            boards = boardList,
            searchQuery = query,
            isLoading = false,
            showCreateDialog = showDialog
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), State())

    private val _effect = Channel<Effect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    private var recentlyDeletedBoard: Board? = null

    fun onIntent(intent: Intent) {
        when (intent) {
            is Intent.OnSearchQueryChanged -> searchQuery.value = intent.query

            Intent.OnCreateBoardClicked -> showCreateDialog.value = true

            Intent.OnDismissCreateDialog -> showCreateDialog.value = false

            is Intent.OnCreateBoardConfirmed -> {
                if (intent.title.isBlank()) return
                showCreateDialog.value = false
                viewModelScope.launch {
                    repository.createBoard(
                        title = intent.title,
                        description = intent.description,
                        colorHex = intent.colorHex
                    )
                }
            }

            is Intent.OnBoardClicked -> viewModelScope.launch {
                _effect.send(Effect.NavigateToBoard(intent.boardId))
            }

            is Intent.OnToggleFavorite -> viewModelScope.launch {
                repository.setBoardFavorite(intent.board.id, !intent.board.isFavorite)
            }

            is Intent.OnDeleteBoard -> viewModelScope.launch {
                recentlyDeletedBoard = intent.board
                repository.deleteBoard(intent.board)
                _effect.send(Effect.ShowUndoSnackbar("Board \"${intent.board.title}\" deleted"))
            }

            Intent.OnUndoDeleteBoard -> viewModelScope.launch {
                recentlyDeletedBoard?.let { repository.restoreBoard(it) }
                recentlyDeletedBoard = null
            }
        }
    }
}
