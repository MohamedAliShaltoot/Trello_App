package com.example.trello.presentation.board

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trello.domain.model.BoardList
import com.example.trello.domain.model.Card
import com.example.trello.domain.model.Priority
import com.example.trello.domain.repository.TrelloRepository
import com.example.trello.presentation.board.BoardContract.Effect
import com.example.trello.presentation.board.BoardContract.Intent
import com.example.trello.presentation.board.BoardContract.State
import com.example.trello.presentation.navigation.NavDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BoardViewModel @Inject constructor(
    private val repository: TrelloRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val boardId: Long = checkNotNull(savedStateHandle[NavDestination.BoardDetail.ARG_BOARD_ID])

    private val searchQuery = MutableStateFlow("")
    private val priorityFilter = MutableStateFlow<Priority?>(null)
    private val showAddListDialog = MutableStateFlow(false)
    private val addCardForListId = MutableStateFlow<Long?>(null)
    private val moveCardTarget = MutableStateFlow<Card?>(null)
    private val listToRename = MutableStateFlow<BoardList?>(null)


    private data class UiFlags(
        val query: String,
        val priority: Priority?,
        val showAddListDialog: Boolean,
        val addCardForListId: Long?,
        val moveCardTarget: Card?,
        val listToRename: BoardList?
    )

    private val uiFlags = combine(
        searchQuery, priorityFilter, showAddListDialog, addCardForListId, moveCardTarget
    ) { query, priority, showDialog, addCardListId, moveTarget ->
        UiFlags(query, priority, showDialog, addCardListId, moveTarget, null)
    }.combine(listToRename) { flags, listRename ->
        flags.copy(listToRename = listRename)
    }

    val state: StateFlow<State> = combine(
        repository.observeBoard(boardId),
        repository.observeLists(boardId),
        uiFlags
    ) { board, lists, flags ->
        State(
            board = board,
            lists = lists,
            isLoading = false,
            searchQuery = flags.query,
            priorityFilter = flags.priority,
            showAddListDialog = flags.showAddListDialog,
            addCardForListId = flags.addCardForListId,
            moveCardTarget = flags.moveCardTarget,
            listToRename = flags.listToRename
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), State())

    private val _effect = Channel<Effect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    private var recentlyDeletedCard: Card? = null
    private var recentlyDeletedList: BoardList? = null

    fun onIntent(intent: Intent) {
        when (intent) {
            is Intent.OnSearchQueryChanged -> searchQuery.value = intent.query
            is Intent.OnPriorityFilterChanged -> priorityFilter.value = intent.priority

            Intent.OnAddListClicked -> showAddListDialog.value = true
            Intent.OnDismissAddListDialog -> showAddListDialog.value = false
            is Intent.OnAddListConfirmed -> {
                if (intent.title.isBlank()) return
                showAddListDialog.value = false
                viewModelScope.launch { repository.createList(boardId, intent.title) }
            }

            is Intent.OnRenameListClicked -> listToRename.value = intent.list
            
            Intent.OnDismissRenameListDialog -> listToRename.value = null
            
            is Intent.OnRenameListConfirmed -> {
                val list = listToRename.value ?: return
                if (intent.title.isBlank()) return
                listToRename.value = null
                viewModelScope.launch { repository.renameList(list, intent.title) }
            }

            is Intent.OnDeleteList -> viewModelScope.launch {
                recentlyDeletedList = intent.list
                repository.deleteList(intent.list)
                _effect.send(Effect.ShowUndoListSnackbar("List \"${intent.list.title}\" deleted"))
            }
            Intent.OnUndoDeleteList -> viewModelScope.launch {
                recentlyDeletedList?.let { repository.restoreList(it) }
                recentlyDeletedList = null
            }

            is Intent.OnAddCardClicked -> addCardForListId.value = intent.listId
            Intent.OnDismissAddCardDialog -> addCardForListId.value = null
            is Intent.OnAddCardConfirmed -> {
                if (intent.title.isBlank()) return
                addCardForListId.value = null
                viewModelScope.launch { repository.createCard(intent.listId, intent.title) }
            }

            is Intent.OnCardClicked -> viewModelScope.launch {
                _effect.send(Effect.NavigateToCard(intent.cardId))
            }

            is Intent.OnToggleCardCompleted -> viewModelScope.launch {
                repository.setCardCompleted(intent.card.id, !intent.card.isCompleted)
            }

            is Intent.OnDeleteCard -> viewModelScope.launch {
                recentlyDeletedCard = intent.card
                repository.deleteCard(intent.card)
                _effect.send(Effect.ShowUndoCardSnackbar("Card \"${intent.card.title}\" deleted"))
            }
            Intent.OnUndoDeleteCard -> viewModelScope.launch {
                recentlyDeletedCard?.let { repository.restoreCard(it) }
                recentlyDeletedCard = null
            }

            is Intent.OnMoveCardClicked -> moveCardTarget.value = intent.card
            Intent.OnDismissMoveCardDialog -> moveCardTarget.value = null
            is Intent.OnMoveCardConfirmed -> {
                moveCardTarget.value = null
                viewModelScope.launch { 
                    repository.moveCard(intent.card, intent.targetListId, intent.targetCardId, intent.isTopHalf) 
                }
            }
            
            is Intent.OnMoveList -> viewModelScope.launch {
                repository.moveList(intent.list, intent.targetListId, intent.isLeftHalf)
            }
        }
    }
}
