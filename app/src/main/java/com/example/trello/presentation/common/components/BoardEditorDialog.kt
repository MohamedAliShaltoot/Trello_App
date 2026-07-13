package com.example.trello.presentation.common.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import com.example.trello.presentation.theme.BoardColorPalette
import androidx.core.graphics.toColorInt


private val DialogShape = RoundedCornerShape(28.dp)
private val FieldShape = RoundedCornerShape(16.dp)
private val ButtonShape = RoundedCornerShape(10.dp)


@Composable
fun BoardEditorDialog(
    initialTitle: String = "",
    initialDescription: String = "",
    initialColorHex: String = BoardColorPalette.first(),
    onDismiss: () -> Unit,
    onConfirm: (title: String, description: String, colorHex: String) -> Unit
) {
    var title by remember { mutableStateOf(initialTitle) }
    var description by remember { mutableStateOf(initialDescription) }
    var colorHex by remember { mutableStateOf(initialColorHex) }
    val accentColor = remember(colorHex) { colorHex.toComposeColor() }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = DialogShape,
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        tonalElevation = 6.dp,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .background(accentColor, CircleShape)
                )
                Text(
                    text = "New board",
                    modifier = Modifier.padding(start = 10.dp),
                    style = MaterialTheme.typography.titleLarge
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    singleLine = true,
                    label = { Text("Board title") },
                    shape = FieldShape,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = accentColor,
                        focusedLabelColor = accentColor,
                        cursorColor = accentColor
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description (optional)") },
                    shape = FieldShape,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = accentColor,
                        focusedLabelColor = accentColor,
                        cursorColor = accentColor
                    ),
                    minLines = 2,
                    maxLines = 4,
                    modifier = Modifier.fillMaxWidth()
                )
                Column {
                    Text(
                        text = "Color",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        items(BoardColorPalette) { hex ->
                            ColorSwatch(
                                hex = hex,
                                selected = hex == colorHex,
                                onClick = { colorHex = hex }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(title.trim(), description.trim(), colorHex) },
                enabled = title.isNotBlank(),
                shape = ButtonShape,
                colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                modifier = Modifier.padding(bottom = 4.dp, end = 4.dp)
            ) {
                Text("Create", color = Color.White)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = ButtonShape,
                modifier = Modifier.padding(bottom = 4.dp)
            ) {
                Text("Cancel", color = Color.Black)
            }
        }
    )
}

@Composable
fun ColorSwatch(hex: String, selected: Boolean, onClick: () -> Unit) {
    val color = remember(hex) { Color(hex.toColorInt()) }
    val scale by animateFloatAsState(targetValue = if (selected) 1.12f else 1f, label = "swatchScale")
    val elevation by animateDpAsState(targetValue = if (selected) 6.dp else 0.dp, label = "swatchElevation")

    Row(
        modifier = Modifier
            .size(36.dp)
            .scale(scale)
            .shadow(elevation = elevation, shape = CircleShape, clip = false)
            .clip(CircleShape)
            .background(color, CircleShape)
            .clickable(onClick = onClick)
            .border(
                width = if (selected) 2.dp else 0.dp,
                color = MaterialTheme.colorScheme.onSurface,
                shape = CircleShape
            ),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (selected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Selected",
                tint = Color.White,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

fun String.toComposeColor(): Color = Color(this.toColorInt())
fun Color.toHex(): String = "#%06X".format(0xFFFFFF and this.toArgb())

