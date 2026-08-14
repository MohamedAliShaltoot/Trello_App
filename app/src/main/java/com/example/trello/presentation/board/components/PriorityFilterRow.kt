package com.example.trello.presentation.board.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.trello.domain.model.Priority

@Composable
 fun PriorityFilterRow(selected: Priority?, onSelect: (Priority?) -> Unit) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        item {
            FilterChip(selected = selected == null, onClick = { onSelect(null) }, label = { Text("All") })
        }
        items(Priority.entries.filter { it != Priority.NONE }) { priority ->
            FilterChip(
                selected = selected == priority,
                onClick = { onSelect(if (selected == priority) null else priority) },
                label = { Text(priority.label) }
            )
        }
    }
}