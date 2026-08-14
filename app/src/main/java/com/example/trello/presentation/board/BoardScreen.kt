package com.example.trello.presentation.board


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.trello.presentation.board.components.AddListButton
import com.example.trello.presentation.board.components.ListColumn
import com.example.trello.presentation.board.components.MoveCardDialog
import com.example.trello.presentation.board.components.PriorityFilterRow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.zIndex
import androidx.compose.runtime.CompositionLocalProvider
import com.example.trello.presentation.board.components.CardItem
import com.example.trello.presentation.board.components.draganddrop.LocalDragDropState
import com.example.trello.presentation.board.components.draganddrop.rememberDragDropState
import com.example.trello.presentation.common.components.InputDialog
import com.example.trello.presentation.common.components.toComposeColor
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BoardScreen(
    onNavigateBack: () -> Unit,
    onCardClick: (Long) -> Unit,
    viewModel: BoardViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is BoardContract.Effect.NavigateToCard -> onCardClick(effect.cardId)
                is BoardContract.Effect.ShowUndoCardSnackbar -> scope.launch {
                    val result = snackbarHostState.showSnackbar(
                        message = effect.message,
                        actionLabel = "Undo",
                        duration = SnackbarDuration.Long
                    )
                    if (result == SnackbarResult.ActionPerformed) {
                        viewModel.onIntent(BoardContract.Intent.OnUndoDeleteCard)
                    }
                }
                is BoardContract.Effect.ShowUndoListSnackbar -> scope.launch {
                    val result = snackbarHostState.showSnackbar(
                        message = effect.message,
                        actionLabel = "Undo",
                        duration = SnackbarDuration.Long
                    )
                    if (result == SnackbarResult.ActionPerformed) {
                        viewModel.onIntent(BoardContract.Intent.OnUndoDeleteList)
                    }
                }
                is BoardContract.Effect.ShowMessage -> scope.launch {
                    snackbarHostState.showSnackbar(
                        message = effect.message,
                        duration = SnackbarDuration.Short
                    )
                }
            }
        }
    }

    val accentColor = remember(state.board?.colorHex) {
        (state.board?.colorHex ?: "#0079BF").toComposeColor()
    }
    
    val dragDropState = rememberDragDropState()

    CompositionLocalProvider(LocalDragDropState provides dragDropState) {
        Box(modifier = Modifier.fillMaxSize()) {
            Scaffold(
                containerColor = accentColor,
                snackbarHost = { SnackbarHost(snackbarHostState) },
                topBar = {
                    TopAppBar(
                        title = { Text(state.board?.title ?: "Board") },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color.Transparent,
                            titleContentColor = Color.White,
                            navigationIconContentColor = Color.White,
                            actionIconContentColor = Color.White
                        ),
                        navigationIcon = {
                            IconButton(onClick = onNavigateBack) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                            }
                        }
                    )
                }
            ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {

            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = { viewModel.onIntent(BoardContract.Intent.OnSearchQueryChanged(it)) },
                singleLine = true,
                placeholder = { Text("Search cards") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    disabledContainerColor = MaterialTheme.colorScheme.surface,
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent
                ),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 6.dp)
            )

            PriorityFilterRow(
                selected = state.priorityFilter,
                onSelect = { viewModel.onIntent(BoardContract.Intent.OnPriorityFilterChanged(it)) }
            )

            if (state.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                val listState = rememberLazyListState()
                val configuration = LocalConfiguration.current
                val density = LocalDensity.current
                val screenWidthPx = with(density) { configuration.screenWidthDp.dp.toPx() }

                LaunchedEffect(dragDropState.isDragging) {
                    while (dragDropState.isDragging) {
                        val currentX = dragDropState.cardPosition.x + dragDropState.dragOffset.x
                        // The card's left edge. If it's near the right edge of screen, scroll right.
                        val rightEdgeThreshold = screenWidthPx - 300f 
                        val leftEdgeThreshold = 100f

                        var scrollAmount = 0f
                        if (currentX > rightEdgeThreshold) {
                            scrollAmount = (currentX - rightEdgeThreshold) * 0.15f
                        } else if (currentX < leftEdgeThreshold) {
                            scrollAmount = (currentX - leftEdgeThreshold) * 0.15f
                        }

                        if (scrollAmount != 0f) {
                            listState.scrollBy(scrollAmount)
                        }
                        delay(16)
                    }
                }

                LazyRow(
                    state = listState,
                    modifier = Modifier.fillMaxSize().padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.visibleLists, key = { it.id }) { list ->
                        ListColumn(
                            list = list,
                            accentColor = accentColor,
                            onAddCardClick = { viewModel.onIntent(BoardContract.Intent.OnAddCardClicked(list.id)) },
                            onCardClick = { card -> viewModel.onIntent(BoardContract.Intent.OnCardClicked(card.id)) },
                            onToggleComplete = { card -> viewModel.onIntent(BoardContract.Intent.OnToggleCardCompleted(card)) },
                            onDeleteCard = { card -> viewModel.onIntent(BoardContract.Intent.OnDeleteCard(card)) },
                            onMoveCard = { card, targetListId, targetCardId, isTopHalf -> 
                                if (targetListId == -1L) {
                                    viewModel.onIntent(BoardContract.Intent.OnMoveCardClicked(card))
                                } else {
                                    viewModel.onIntent(BoardContract.Intent.OnMoveCardConfirmed(card, targetListId, targetCardId, isTopHalf))
                                }
                            },
                            onMoveList = { listToMove, targetListId, isLeftHalf ->
                                viewModel.onIntent(BoardContract.Intent.OnMoveList(listToMove, targetListId, isLeftHalf))
                            },
                            onRenameList = { viewModel.onIntent(BoardContract.Intent.OnRenameListClicked(list)) },
                            onDeleteList = { viewModel.onIntent(BoardContract.Intent.OnDeleteList(list)) },
                            modifier = Modifier.animateItem()
                        )
                    }
                    item {
                        AddListButton(onClick = { viewModel.onIntent(BoardContract.Intent.OnAddListClicked) })
                    }
                }

                if (state.visibleLists.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "No lists found in this board.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }

    if (state.showAddListDialog) {
        InputDialog(
            title = "New list",
            label = "List name",
            onDismiss = { viewModel.onIntent(BoardContract.Intent.OnDismissAddListDialog) },
            onConfirm = { name -> viewModel.onIntent(BoardContract.Intent.OnAddListConfirmed(name)) }
        )
    }
    
    state.listToRename?.let { list ->
        InputDialog(
            title = "Rename list",
            label = "List name",
            initialValue = list.title,
            confirmLabel = "Save",
            onDismiss = { viewModel.onIntent(BoardContract.Intent.OnDismissRenameListDialog) },
            onConfirm = { name -> viewModel.onIntent(BoardContract.Intent.OnRenameListConfirmed(name)) }
        )
    }

    state.addCardForListId?.let { listId ->
        InputDialog(
            title = "New card",
            label = "Card title",
            onDismiss = { viewModel.onIntent(BoardContract.Intent.OnDismissAddCardDialog) },
            onConfirm = { title -> viewModel.onIntent(BoardContract.Intent.OnAddCardConfirmed(listId, title)) }
        )
    }

    state.moveCardTarget?.let { card ->
        MoveCardDialog(
            currentListId = card.listId,
            lists = state.lists,
            onDismiss = { viewModel.onIntent(BoardContract.Intent.OnDismissMoveCardDialog) },
            onListSelected = { targetListId ->
                viewModel.onIntent(BoardContract.Intent.OnMoveCardConfirmed(card, targetListId))
            }
        )
    }
            
            if (dragDropState.isDragging && dragDropState.draggedCard != null) {
                Box(
                    modifier = Modifier
                        .zIndex(10f)
                        .graphicsLayer {
                            translationX = (dragDropState.cardPosition.x + dragDropState.dragOffset.x)
                            translationY = (dragDropState.cardPosition.y + dragDropState.dragOffset.y)
                            alpha = 0.9f
                            scaleX = 1.05f
                            scaleY = 1.05f
                        }
                        .padding(horizontal = 8.dp) // padding to match list inner padding
                ) {
                    CardItem(
                        card = dragDropState.draggedCard!!,
                        onClick = {},
                        onToggleComplete = {},
                        onDelete = {},
                        onMove = {},
                        isOverlay = true
                    )
                }
            }

            if (dragDropState.isDragging && dragDropState.draggedList != null) {
                Box(
                    modifier = Modifier
                        .zIndex(10f)
                        .graphicsLayer {
                            translationX = (dragDropState.cardPosition.x + dragDropState.dragOffset.x)
                            translationY = (dragDropState.cardPosition.y + dragDropState.dragOffset.y)
                            alpha = 0.9f
                            scaleX = 1.05f
                            scaleY = 1.05f
                        }
                ) {
                    ListColumn(
                        list = dragDropState.draggedList!!,
                        accentColor = accentColor,
                        onAddCardClick = {},
                        onCardClick = {},
                        onToggleComplete = {},
                        onDeleteCard = {},
                        onMoveCard = { _, _, _, _ -> },
                        onMoveList = { _, _, _ -> },
                        onRenameList = {},
                        onDeleteList = {},
                        isOverlay = true
                    )
                }
            }
        }
    }
}












