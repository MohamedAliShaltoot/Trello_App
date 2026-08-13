package com.example.trello.presentation.board

import com.example.trello.domain.model.Board
import com.example.trello.domain.model.BoardList
import com.example.trello.domain.model.Card
import com.example.trello.domain.model.Priority


object BoardContract {

    data class State(
        val board: Board? = null,
        val lists: List<BoardList> = emptyList(),
        val isLoading: Boolean = true,
        val searchQuery: String = "",
        val priorityFilter: Priority? = null,
        val showAddListDialog: Boolean = false,
        val addCardForListId: Long? = null,
        val moveCardTarget: Card? = null
    ) {
        val visibleLists: List<BoardList>
            get() = lists.map { list ->
                val filteredCards = list.cards.filter { card ->
                    (searchQuery.isBlank() || card.title.contains(searchQuery, ignoreCase = true)) &&
                        (priorityFilter == null || card.priority == priorityFilter)
                }
                list.copy(cards = filteredCards)
            }
    }

    sealed interface Intent {
        data class OnSearchQueryChanged(val query: String) : Intent
        data class OnPriorityFilterChanged(val priority: Priority?) : Intent

        data object OnAddListClicked : Intent
        data object OnDismissAddListDialog : Intent
        data class OnAddListConfirmed(val title: String) : Intent
        data class OnDeleteList(val list: BoardList) : Intent
        data object OnUndoDeleteList : Intent

        data class OnAddCardClicked(val listId: Long) : Intent
        data object OnDismissAddCardDialog : Intent
        data class OnAddCardConfirmed(val listId: Long, val title: String) : Intent
        data class OnCardClicked(val cardId: Long) : Intent
        data class OnToggleCardCompleted(val card: Card) : Intent
        data class OnDeleteCard(val card: Card) : Intent
        data object OnUndoDeleteCard : Intent

        data class OnMoveCardClicked(val card: Card) : Intent
        data object OnDismissMoveCardDialog : Intent
        data class OnMoveCardConfirmed(val card: Card, val targetListId: Long) : Intent
    }

    sealed interface Effect {
        data class NavigateToCard(val cardId: Long) : Effect
        data class ShowUndoCardSnackbar(val message: String) : Effect
        data class ShowUndoListSnackbar(val message: String) : Effect
        data class ShowMessage(val message: String) : Effect
    }
}
