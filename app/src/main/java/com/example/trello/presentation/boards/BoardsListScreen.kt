package com.example.trello.presentation.boards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.trello.presentation.common.components.BoardEditorDialog
import com.example.trello.presentation.common.components.BoardListItem
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BoardsListScreen(
    onBoardClick: (Long) -> Unit,
    viewModel: BoardsListViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is BoardsListContract.Effect.NavigateToBoard -> onBoardClick(effect.boardId)
                is BoardsListContract.Effect.ShowUndoSnackbar -> scope.launch {
                    val result = snackbarHostState.showSnackbar(
                        message = effect.message,
                        actionLabel = "Undo",
                        withDismissAction = true,
                        duration = SnackbarDuration.Short
                    )
                    if (result == SnackbarResult.ActionPerformed) {
                        viewModel.onIntent(BoardsListContract.Intent.OnUndoDeleteBoard)
                    }
                }
                is BoardsListContract.Effect.ShowMessage -> scope.launch {
                    snackbarHostState.showSnackbar(
                        message = effect.message,
                        duration = SnackbarDuration.Short
                    )
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = { TopAppBar(title = { Text("Your Boards") }) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text("New board") },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                onClick = { viewModel.onIntent(BoardsListContract.Intent.OnCreateBoardClicked) },
                shape = RoundedCornerShape(8.dp),
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = { viewModel.onIntent(BoardsListContract.Intent.OnSearchQueryChanged(it)) },
                singleLine = true,
                placeholder = { Text("Search boards") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )

            when {
                state.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }

                state.isEmpty -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = if (state.searchQuery.isBlank()) "No boards yet. Tap + to create one."
                        else "No boards match \"${state.searchQuery}\".",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                else -> LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.boards, key = { it.id }) { board ->
                        BoardListItem(
                            board = board,
                            onClick = { viewModel.onIntent(BoardsListContract.Intent.OnBoardClicked(board.id)) },
                            onToggleFavorite = { viewModel.onIntent(BoardsListContract.Intent.OnToggleFavorite(board)) },
                            onDelete = { viewModel.onIntent(BoardsListContract.Intent.OnDeleteBoard(board)) }
                        )
                    }
                }
            }
        }
    }

    if (state.showCreateDialog) {
        BoardEditorDialog(
            onDismiss = { viewModel.onIntent(BoardsListContract.Intent.OnDismissCreateDialog) },
            onConfirm = { title, description, colorHex ->
                viewModel.onIntent(BoardsListContract.Intent.OnCreateBoardConfirmed(title, description, colorHex))
            }
        )
    }
}
