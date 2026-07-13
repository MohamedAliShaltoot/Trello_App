package com.example.trello.presentation.common.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// Shared with the rest of the dialog family so every AlertDialog in the app
// reads as one deliberate shape language rather than mismatched defaults.
private val DialogShape = RoundedCornerShape(20.dp)
private val FieldShape = RoundedCornerShape(16.dp)
private val ButtonShape = RoundedCornerShape(8.dp)

/** Simple single-field text input dialog, used for "new list" / "new card". */
@Composable
fun InputDialog(
    title: String,
    label: String,
    confirmLabel: String = "Create",
    initialValue: String = "",
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var text by remember { mutableStateOf(initialValue) }
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = DialogShape,
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        tonalElevation = 6.dp,
        title = { Text(title, style = MaterialTheme.typography.titleLarge) },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                singleLine = true,
                label = { Text(label) },
                shape = FieldShape,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    focusedLabelColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(text.trim()) },
                enabled = text.isNotBlank(),
                shape = ButtonShape
            ) { Text(confirmLabel) }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss, shape = ButtonShape) { Text("Cancel") }
        }
    )
}

/** Generic "are you sure?" destructive-action confirmation dialog. */
@Composable
fun ConfirmDialog(
    title: String,
    text: String,
    confirmLabel: String = "Delete",
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = DialogShape,
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        tonalElevation = 6.dp,
        title = { Text(title, style = MaterialTheme.typography.titleLarge) },
        text = { Text(text, color = MaterialTheme.colorScheme.onSurfaceVariant) },
        confirmButton = {
            Button(
                onClick = {
                    onDismiss()
                    onConfirm()
                },
                shape = ButtonShape,
                colors = ButtonDefaults.buttonColors(Color.Red)
//                colors = ButtonDefaults.buttonColors(
//                    containerColor = MaterialTheme.colorScheme.error,
//                    contentColor = MaterialTheme.colorScheme.onError
//                )
            ) { Text(confirmLabel,color = Color.White) }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss, shape = ButtonShape) { Text("Cancel") }
        }
    )
}