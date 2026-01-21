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
import io.github.m0nkeysan.gamekeeper.generated.resources.Res
import io.github.m0nkeysan.gamekeeper.generated.resources.action_cancel
import io.github.m0nkeysan.gamekeeper.generated.resources.action_create
import io.github.m0nkeysan.gamekeeper.generated.resources.action_save
import io.github.m0nkeysan.gamekeeper.generated.resources.player_dialog_edit_title
import io.github.m0nkeysan.gamekeeper.generated.resources.player_dialog_new_title
import io.github.m0nkeysan.gamekeeper.generated.resources.player_error_name_taken
import io.github.m0nkeysan.gamekeeper.generated.resources.player_field_name
import io.github.m0nkeysan.gamekeeper.generated.resources.player_label_color
import io.github.m0nkeysan.gamekeeper.generated.resources.player_placeholder_name
import io.github.m0nkeysan.gamekeeper.ui.components.ColorSelectorRow
import io.github.m0nkeysan.gamekeeper.ui.components.DIALOG_COLOR_PRESETS
import io.github.m0nkeysan.gamekeeper.ui.components.FieldLabel
import io.github.m0nkeysan.gamekeeper.ui.components.FlatTextField
import io.github.m0nkeysan.gamekeeper.ui.utils.parseColor
import org.jetbrains.compose.resources.stringResource

@Composable
fun PlayerDialog(
    initialPlayer: Player? = null,
    existingPlayers: List<Player>,
    onDismiss: () -> Unit,
    onConfirm: (name: String, color: String) -> Unit
) {
    var name by remember { mutableStateOf(initialPlayer?.name ?: "") }
    var selectedColor by remember { mutableStateOf(initialPlayer?.avatarColor ?: DIALOG_COLOR_PRESETS.random()) }
    val focusRequester = remember { FocusRequester() }

    val composeColor = remember(selectedColor) { parseColor(selectedColor) }
    val contentColor = if (composeColor.luminance() > 0.5f) Color.Black else Color.White

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
                        text = if (initialPlayer == null) stringResource(Res.string.player_dialog_new_title) else stringResource(Res.string.player_dialog_edit_title),
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
                         label = stringResource(Res.string.player_field_name),
                         placeholder = stringResource(Res.string.player_placeholder_name),
                         accentColor = composeColor,
                         modifier = Modifier.focusRequester(focusRequester)
                     )
                    
                    if (isNameTaken) {
                         Text(
                             text = stringResource(Res.string.player_error_name_taken), 
                             color = MaterialTheme.colorScheme.error,
                             style = MaterialTheme.typography.bodySmall,
                             modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                         )
                     }

                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        FieldLabel(text = stringResource(Res.string.player_label_color))

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
                         Text(stringResource(Res.string.action_cancel), color = MaterialTheme.colorScheme.onSurfaceVariant)
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
                             if (initialPlayer == null) stringResource(Res.string.action_create) else stringResource(Res.string.action_save),
                             fontWeight = FontWeight.Bold
                         )
                     }
                }
            }
        }
    }
}
