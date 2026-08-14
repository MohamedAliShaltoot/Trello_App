package com.example.trello.presentation.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trello.domain.model.Board
import com.example.trello.domain.repository.TrelloRepository
import com.example.trello.presentation.favorites.FavoritesContract.Effect
import com.example.trello.presentation.favorites.FavoritesContract.Intent
import com.example.trello.presentation.favorites.FavoritesContract.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val repository: TrelloRepository
) : ViewModel() {

    val state: StateFlow<State> = repository.observeBoards()
        .map { boards -> State(boards = boards.filter { it.isFavorite }, isLoading = false) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), State())

    private val _effect = Channel<Effect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    private var recentlyDeletedBoard: Board? = null

    fun onIntent(intent: Intent) {
        when (intent) {
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
