package com.example.trello.presentation.favorites

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.trello.presentation.common.components.BoardListItem
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    onBoardClick: (Long) -> Unit,
    viewModel: FavoritesViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is FavoritesContract.Effect.NavigateToBoard -> onBoardClick(effect.boardId)
                is FavoritesContract.Effect.ShowUndoSnackbar -> scope.launch {
                    val result = snackbarHostState.showSnackbar(
                        message = effect.message,
                        actionLabel = "Undo",
                        withDismissAction = true,
                        duration = SnackbarDuration.Long
                    )
                    if (result == SnackbarResult.ActionPerformed) {
                        viewModel.onIntent(FavoritesContract.Intent.OnUndoDeleteBoard)
                    }
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = { TopAppBar(title = { Text("Favorites") }) }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            when {
                state.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }

                state.isEmpty -> Column(
                    modifier = Modifier.fillMaxSize().padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Favorite,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "No favorite boards yet",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(top = 12.dp)
                    )
                    Text(
                        text = "Tap the heart btn on any board to pin it here.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                else -> LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.boards, key = { it.id }) { board ->
                        BoardListItem(
                            board = board,
                            onClick = { viewModel.onIntent(FavoritesContract.Intent.OnBoardClicked(board.id)) },
                            onToggleFavorite = { viewModel.onIntent(FavoritesContract.Intent.OnToggleFavorite(board)) },
                            onEdit = {},
                            onDelete = { viewModel.onIntent(FavoritesContract.Intent.OnToggleFavorite(board)) },
                            requireDeleteConfirmation = false
                        )
                    }
                }
            }
        }
    }
}
