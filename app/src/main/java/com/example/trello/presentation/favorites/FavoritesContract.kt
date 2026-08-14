package com.example.trello.presentation.favorites

import com.example.trello.domain.model.Board


object FavoritesContract {

    data class State(
        val boards: List<Board> = emptyList(),
        val isLoading: Boolean = true
    ) {
        val isEmpty: Boolean get() = !isLoading && boards.isEmpty()
    }

    sealed interface Intent {
        data class OnBoardClicked(val boardId: Long) : Intent
        data class OnToggleFavorite(val board: Board) : Intent
        data class OnDeleteBoard(val board: Board) : Intent
        data object OnUndoDeleteBoard : Intent
    }

    sealed interface Effect {
        data class NavigateToBoard(val boardId: Long) : Effect
        data class ShowUndoSnackbar(val message: String) : Effect
    }
}
