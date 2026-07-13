package com.example.trello.presentation.board.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.trello.domain.model.BoardList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
 fun MoveCardDialog(
    currentListId: Long,
    lists: List<BoardList>,
    onDismiss: () -> Unit,
    onListSelected: (Long) -> Unit
) {
    val candidates = lists.filter { it.id != currentListId }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Move card to…") },
        text = {
            if (candidates.isEmpty()) {
                Text("No other lists on this board yet.")
            } else {
                Column {
                    candidates.forEach { list ->
                        TextButton(
                            onClick = { onListSelected(list.id) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(list.title, modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}