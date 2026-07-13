package com.example.trello.presentation.board.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.trello.domain.model.BoardList
import com.example.trello.domain.model.Card as TrelloCard
import com.example.trello.presentation.common.components.ConfirmDialog

@Composable
fun ListColumn(
    list: BoardList,
    accentColor: Color,
    onAddCardClick: () -> Unit,
    onCardClick: (TrelloCard) -> Unit,
    onToggleComplete: (TrelloCard) -> Unit,
    onDeleteCard: (TrelloCard) -> Unit,
    onMoveCard: (TrelloCard) -> Unit,
    onDeleteList: () -> Unit
) {
    var showDeleteListDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.width(272.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {

            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(accentColor, CircleShape)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = list.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "${list.completedCount}/${list.cardCount}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                IconButton(onClick = { showDeleteListDialog = true }, modifier = Modifier.size(32.dp)) {
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
                items(list.cards, key = { it.id }) { card ->
                    CardItem(
                        card = card,
                        onClick = { onCardClick(card) },
                        onToggleComplete = { onToggleComplete(card) },
                        onDelete = { onDeleteCard(card) },
                        onMove = { onMoveCard(card) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(onClick = onAddCardClick, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add card")
            }
        }
    }

    if (showDeleteListDialog) {
        ConfirmDialog(
            title = "Delete list?",
            text = "\"${list.title}\" and all its cards will be deleted.",
            onDismiss = { showDeleteListDialog = false },
            onConfirm = onDeleteList
        )
    }
}