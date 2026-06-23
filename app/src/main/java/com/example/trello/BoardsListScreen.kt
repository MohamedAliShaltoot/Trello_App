package com.example.trello


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.trello.data.local.entity.BoardEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BoardsListScreen(
    onBoardClick: (Long) -> Unit,
    viewModel: BoardsListViewModel = hiltViewModel()
) {
    val boards by viewModel.boards.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Your Boards") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add board")
            }
        }
    ) { innerPadding ->
        if (boards.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text("No boards yet. Tap + to create one.")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(boards, key = { it.id }) { board ->
                    BoardRow(board = board, onClick = { onBoardClick(board.id) })
                }
            }
        }
    }

    if (showAddDialog) {
        AddBoardDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { title ->
                viewModel.addBoard(title)
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun BoardRow(board: BoardEntity, onClick: () -> Unit) {
    ElevatedCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = board.title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
private fun AddBoardDialog(onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var text by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New board") },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                singleLine = true,
                label = { Text("Board title") }
            )
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(text) }, enabled = text.isNotBlank()) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}