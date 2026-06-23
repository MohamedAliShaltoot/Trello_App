package com.example.trello

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.example.trello.data.local.entity.CardEntity
import com.example.trello.data.local.entity.CardListEntity


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BoardScreen(
    onNavigateBack: () -> Unit,
    onCardClick: (Long) -> Unit,
    viewModel: BoardViewModel = hiltViewModel()
) {
    val lists by viewModel.boardContents.collectAsState()
    var showAddListDialog by remember { mutableStateOf(false) }
    var addCardForListId by remember { mutableStateOf<Long?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Board") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyRow(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(lists, key = { it.list.id }) { listWithCards ->
                ListColumn(
                    listWithCards = listWithCards,
                    onAddCardClick = { addCardForListId = listWithCards.list.id },
                    onCardClick = onCardClick,
                    onDeleteCard = { card -> viewModel.deleteCard(card) },
                    onDeleteList = { list -> viewModel.deleteList(list) }
                )
            }
            item {
                AddListButton(onClick = { showAddListDialog = true })
            }
        }
    }

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


@Composable
private fun ListColumn(
    listWithCards: ListWithCards,
    onAddCardClick: () -> Unit,
    onCardClick: (Long) -> Unit,
    onDeleteCard: (CardEntity) -> Unit,
    onDeleteList: (CardListEntity) -> Unit
) {
    var showDeleteListDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.width(260.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(8.dp)) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = listWithCards.list.title,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = { showDeleteListDialog = true },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete list",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                items(listWithCards.cards, key = { it.id }) { card ->
                    CardItem(
                        card = card,
                        onClick = { onCardClick(card.id) },
                        onDelete = { onDeleteCard(card) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(
                onClick = onAddCardClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add card")
            }
        }
    }

    if (showDeleteListDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteListDialog = false },
            title = { Text("Delete list?") },
            text = {
                Text(
                    "\"${listWithCards.list.title}\" and all its cards " +
                            "will be deleted. This cannot be undone."
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteListDialog = false
                    onDeleteList(listWithCards.list)
                }) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteListDialog = false }) { Text("Cancel") }
            }
        )
    }
}


@Composable
private fun CardItem(
    card: CardEntity,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    ElevatedCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, top = 4.dp, bottom = 4.dp, end = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = card.title,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyMedium
            )
            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete card",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}


@Composable
private fun AddListButton(onClick: () -> Unit) {
    Card(
        modifier = Modifier.width(200.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(modifier = Modifier.padding(8.dp)) {
            TextButton(onClick = onClick) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add list")
            }
        }
    }
}

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
            ) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}