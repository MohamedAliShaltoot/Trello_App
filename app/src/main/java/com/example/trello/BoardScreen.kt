package com.example.trello

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BoardScreen(viewModel: BoardViewModel = hiltViewModel()) {
    val lists by viewModel.boardContents.collectAsState()

    // Which dialog is currently open — null means none
    var showAddListDialog by remember { mutableStateOf(false) }
    var addCardForListId by remember { mutableStateOf<Long?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Board") })
        }
    ) { innerPadding ->
        LazyRow(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Render each list column with its cards
            items(lists, key = { it.list.id }) { listWithCards ->
                Card(
                    modifier = Modifier.width(260.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(
                            text = listWithCards.list.title,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(listWithCards.cards, key = { it.id }) { card ->
                                ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                                    Text(
                                        text = card.title,
                                        modifier = Modifier.padding(10.dp)
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        // "+ Add card" row inside each list column
                        TextButton(
                            onClick = { addCardForListId = listWithCards.list.id },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add card")
                        }
                    }
                }
            }

            // The last column is always an "+ Add list" button
            item {
                Card(
                    modifier = Modifier.width(200.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp)
                    ) {
                        TextButton(onClick = { showAddListDialog = true }) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add list")
                        }
                    }
                }
            }
        }
    }

    // Dialog to create a new list on this board
    if (showAddListDialog) {
        InputDialog(
            title = "New list",
            label = "List name",
            onDismiss = { showAddListDialog = false },
            onConfirm = { name ->
                viewModel.addList(name)
                showAddListDialog = false
            }
        )
    }

    // Dialog to create a card in a specific list
    addCardForListId?.let { listId ->
        InputDialog(
            title = "New card",
            label = "Card title",
            onDismiss = { addCardForListId = null },
            onConfirm = { title ->
                viewModel.addCard(listId, title)
                addCardForListId = null
            }
        )
    }
}

/** Reusable single-field dialog used by both add-list and add-card. */
@Composable
private fun InputDialog(
    title: String,
    label: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var text by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                singleLine = true,
                label = { Text(label) }
            )
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(text) },
                enabled = text.isNotBlank()
            ) { Text("Create") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}