package io.github.m0nkeysan.gamekeeper.ui.screens.player

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import io.github.m0nkeysan.gamekeeper.core.model.Player
import io.github.m0nkeysan.gamekeeper.core.model.playerNamesEqual
import io.github.m0nkeysan.gamekeeper.core.model.sanitizePlayerName
import io.github.m0nkeysan.gamekeeper.ui.components.ColorSelectorRow
import io.github.m0nkeysan.gamekeeper.ui.components.DIALOG_COLOR_PRESETS
import io.github.m0nkeysan.gamekeeper.ui.components.FieldLabel
import io.github.m0nkeysan.gamekeeper.ui.components.FlatTextField
import io.github.m0nkeysan.gamekeeper.ui.components.parseColor
import io.github.m0nkeysan.gamekeeper.ui.strings.AppStrings

@Composable
fun PlayerDialog(
    initialPlayer: Player? = null,
    existingPlayers: List<Player>,
    onDismiss: () -> Unit,
    onConfirm: (name: String, color: String) -> Unit
) {
    var name by remember { mutableStateOf(initialPlayer?.name ?: "") }
    // Default color if adding, or player's color if editing
    var selectedColor by remember { mutableStateOf(initialPlayer?.avatarColor ?: DIALOG_COLOR_PRESETS.random()) }
    val focusRequester = remember { FocusRequester() }

    val composeColor = remember(selectedColor) { parseColor(selectedColor) }
    val contentColor = if (composeColor.luminance() > 0.5f) Color.Black else Color.White
    
    // Request focus on name input when adding new player
    LaunchedEffect(initialPlayer) {
        if (initialPlayer == null) {
            focusRequester.requestFocus()
        }
    }

    val isNameTaken = remember(name, existingPlayers, initialPlayer) {
        val sanitized = sanitizePlayerName(name)
        sanitized != null && 
        (initialPlayer == null || !playerNamesEqual(sanitized, initialPlayer.name)) &&
        existingPlayers.any { existingPlayer ->
            // Check both active and inactive players
            playerNamesEqual(sanitized, existingPlayer.name)
        }
    }

    // Simple validation
    val isNameValid = sanitizePlayerName(name) != null && !isNameTaken

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp)
        ) {
            Column {
                // Colored Header
                Surface(
                    color = composeColor,
                    contentColor = contentColor,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (initialPlayer == null) AppStrings.PLAYER_DIALOG_NEW_TITLE else AppStrings.PLAYER_DIALOG_EDIT_TITLE,
                        modifier = Modifier.padding(24.dp),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black
                    )
                }

                // Neutral Content
                Column(
                    modifier = Modifier
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    FlatTextField(
                         value = name,
                         onValueChange = { name = it },
                         label = AppStrings.PLAYER_FIELD_NAME,
                         placeholder = AppStrings.PLAYER_PLACEHOLDER_NAME,
                         accentColor = composeColor,
                         modifier = Modifier.focusRequester(focusRequester)
                     )
                    
                    if (isNameTaken) {
                         Text(
                             text = AppStrings.PLAYER_ERROR_NAME_TAKEN, 
                             color = MaterialTheme.colorScheme.error,
                             style = MaterialTheme.typography.bodySmall,
                             modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                         )
                     }

                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        FieldLabel(text = AppStrings.PLAYER_LABEL_COLOR)

                        ColorSelectorRow(
                            selectedColorHex = selectedColor,
                            onColorSelected = { selectedColor = it },
                            modifier = Modifier.fillMaxWidth(),
                            presets = DIALOG_COLOR_PRESETS
                        )
                    }
                }

                // Footer Actions
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                         Text(AppStrings.ACTION_CANCEL, color = MaterialTheme.colorScheme.onSurfaceVariant)
                     }
                     Spacer(Modifier.width(8.dp))
                     Button(
                         onClick = { 
                             sanitizePlayerName(name)?.let { sanitized ->
                                 onConfirm(sanitized, selectedColor)
                             }
                         },
                         enabled = isNameValid,
                         colors = ButtonDefaults.buttonColors(
                             containerColor = composeColor,
                             contentColor = contentColor,
                             disabledContainerColor = composeColor.copy(alpha = 0.3f),
                             disabledContentColor = contentColor.copy(alpha = 0.5f)
                         ),
                         shape = MaterialTheme.shapes.medium
                     ) {
                         Text(
                             if (initialPlayer == null) AppStrings.ACTION_CREATE else AppStrings.ACTION_SAVE,
                             fontWeight = FontWeight.Bold
                         )
                     }
                }
            }
        }
    }
}
