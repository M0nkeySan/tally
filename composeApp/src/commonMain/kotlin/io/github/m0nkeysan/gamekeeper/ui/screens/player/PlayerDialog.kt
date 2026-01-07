package io.github.m0nkeysan.gamekeeper.ui.screens.player

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.m0nkeysan.gamekeeper.core.model.Player
import io.github.m0nkeysan.gamekeeper.ui.components.ColorSelectorRow

@Composable
fun PlayerDialog(
    initialPlayer: Player? = null,
    existingPlayers: List<Player>,
    onDismiss: () -> Unit,
    onConfirm: (name: String, color: String) -> Unit
) {
    var name by remember { mutableStateOf(initialPlayer?.name ?: "") }
    // Default color if adding, or player's color if editing
    var selectedColor by remember { mutableStateOf(initialPlayer?.avatarColor ?: "#FF6200") }

    val isNameTaken = remember(name, existingPlayers, initialPlayer) {
        val cleanName = name.trim()
        cleanName.isNotEmpty() && 
        !cleanName.equals(initialPlayer?.name?.trim(), ignoreCase = true) &&
        existingPlayers.any {
            it.name.equals(cleanName, ignoreCase = true)
        }
    }

    // Simple validation
    val isNameValid = name.isNotBlank() && !isNameTaken

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initialPlayer == null) "New Player" else "Edit Player") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Player Name") },
                    singleLine = true,
                    isError = isNameTaken,
                    supportingText = {
                        if (isNameTaken) {
                            Text("This name is already taken", color = MaterialTheme.colorScheme.error)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Avatar Color", style = MaterialTheme.typography.labelLarge)

                ColorSelectorRow(
                    selectedColorHex = selectedColor,
                    onColorSelected = { selectedColor = it },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(name.trim(), selectedColor) },
                enabled = isNameValid
            ) {
                Text(if (initialPlayer == null) "Add" else "Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
