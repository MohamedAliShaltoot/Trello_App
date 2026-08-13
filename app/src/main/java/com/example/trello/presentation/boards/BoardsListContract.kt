package com.example.trello.presentation.boards

import com.example.trello.domain.model.Board

object BoardsListContract {

    data class State(
        val boards: List<Board> = emptyList(),
        val searchQuery: String = "",
        val isLoading: Boolean = true,
        val showCreateDialog: Boolean = false
    ) {
        val isEmpty: Boolean get() = !isLoading && boards.isEmpty()
    }

    sealed interface Intent {
        data class OnSearchQueryChanged(val query: String) : Intent
        data object OnCreateBoardClicked : Intent
        data object OnDismissCreateDialog : Intent
        data class OnCreateBoardConfirmed(
            val title: String,
            val description: String,
            val colorHex: String
        ) : Intent
        data class OnBoardClicked(val boardId: Long) : Intent
        data class OnToggleFavorite(val board: Board) : Intent
        data class OnDeleteBoard(val board: Board) : Intent
        data object OnUndoDeleteBoard : Intent
    }

    sealed interface Effect {
        data class NavigateToBoard(val boardId: Long) : Effect
        data class ShowUndoSnackbar(val message: String) : Effect
        data class ShowAddedSnackbar(val message: String) : Effect
        data class ShowMessage(val message: String) : Effect
    }
}
