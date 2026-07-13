package com.example.trello.presentation.board.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DriveFileMove
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.DriveFileMove
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.trello.domain.model.Card as TrelloCard
import com.example.trello.domain.model.Priority
import com.example.trello.presentation.common.components.DueDateChip
import com.example.trello.presentation.common.components.PriorityChip

@Composable
 fun CardItem(
    card: TrelloCard,
    onClick: () -> Unit,
    onToggleComplete: () -> Unit,
    onDelete: () -> Unit,
    onMove: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    ElevatedCard(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(start = 10.dp, top = 6.dp, bottom = 6.dp, end = 4.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onToggleComplete, modifier = Modifier.size(28.dp)) {
                    Icon(
                        imageVector = if (card.isCompleted) Icons.Filled.CheckCircle else Icons.Outlined.CheckCircle,
                        contentDescription = if (card.isCompleted) "Mark incomplete?" else "Mark complete 👍",
                        tint = if (card.isCompleted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Text(
                    text = card.title,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodyMedium,
                    textDecoration = if (card.isCompleted) TextDecoration.LineThrough else null,
                    color = if (card.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                )
                Box {
                    IconButton(onClick = { showMenu = true }, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.MoreHoriz, contentDescription = "Card actions", modifier = Modifier.size(18.dp))
                    }
                    DropdownMenu( expanded = showMenu,
                        onDismissRequest = { showMenu = false },
                        shape = RoundedCornerShape(16.dp),
                        tonalElevation = 8.dp,
                        shadowElevation = 12.dp,
                        containerColor = MaterialTheme.colorScheme.surface) {

                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = "Move to list",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.DriveFileMove,
                                    contentDescription = null
                                )
                            },
                            onClick = {
                                showMenu = false
                                onMove()
                            }
                        )
                        HorizontalDivider()

                        DropdownMenuItem(
                            text = {
                                Text(
                                    "Delete",
                                    color = MaterialTheme.colorScheme.error
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.DeleteOutline,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error
                                )
                            },
                            onClick = {
                                showMenu = false
                                onDelete()
                            }
                        )
                    }
                }
            }

            if (card.priority != Priority.NONE || card.dueDate != null) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(start = 34.dp, top = 2.dp)
                ) {
                    PriorityChip(card.priority)
                    card.dueDate?.let { due ->
                        DueDateChip(dueDateMillis = due, isOverdue = card.isOverdue)
                    }
                }
            }
        }
    }
}