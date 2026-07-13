package com.example.trello.presentation.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.trello.domain.model.Priority
import com.example.trello.presentation.theme.PriorityHigh
import com.example.trello.presentation.theme.PriorityLow
import com.example.trello.presentation.theme.PriorityMedium
import com.example.trello.presentation.theme.PriorityNone

fun Priority.color(): Color = when (this) {
    Priority.HIGH -> PriorityHigh
    Priority.MEDIUM -> PriorityMedium
    Priority.LOW -> PriorityLow
    Priority.NONE -> PriorityNone
}

/** Small read-only pill shown on a card, e.g. "High". Hidden for NONE. */
@Composable
fun PriorityChip(priority: Priority, modifier: Modifier = Modifier) {
    if (priority == Priority.NONE) return
    Row(
        modifier = modifier
            .background(priority.color().copy(alpha = 0.16f), RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = priority.label,
            style = MaterialTheme.typography.labelSmall,
            color = priority.color()
        )
    }
}

/** Row of tappable chips used to pick a priority in the card editor. */
@Composable
fun PrioritySelector(
    selected: Priority,
    onSelect: (Priority) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Priority.entries.forEach { priority ->
            val isSelected = priority == selected
            Row(
                modifier = Modifier
                    .background(
                        color = if (isSelected) priority.color().copy(alpha = 0.22f) else MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .clickable { onSelect(priority) }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = priority.label,
                    style = MaterialTheme.typography.labelMedium,
                    color = if (isSelected) priority.color() else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
