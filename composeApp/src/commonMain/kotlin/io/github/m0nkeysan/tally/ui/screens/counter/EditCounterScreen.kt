package io.github.m0nkeysan.tally.ui.screens.counter

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.m0nkeysan.tally.GameIcons
import io.github.m0nkeysan.tally.core.model.sanitizeCounterName
import io.github.m0nkeysan.tally.generated.resources.Res
import io.github.m0nkeysan.tally.generated.resources.action_back
import io.github.m0nkeysan.tally.generated.resources.action_cancel
import io.github.m0nkeysan.tally.generated.resources.action_delete
import io.github.m0nkeysan.tally.generated.resources.counter_edit_action_save
import io.github.m0nkeysan.tally.generated.resources.counter_edit_cd_delete
import io.github.m0nkeysan.tally.generated.resources.counter_edit_field_name
import io.github.m0nkeysan.tally.generated.resources.counter_edit_field_value
import io.github.m0nkeysan.tally.generated.resources.counter_edit_label_color
import io.github.m0nkeysan.tally.generated.resources.counter_edit_placeholder_name
import io.github.m0nkeysan.tally.generated.resources.counter_edit_placeholder_value
import io.github.m0nkeysan.tally.generated.resources.counter_edit_title
import io.github.m0nkeysan.tally.generated.resources.dialog_delete_counter_message
import io.github.m0nkeysan.tally.generated.resources.dialog_delete_counter_title
import io.github.m0nkeysan.tally.ui.components.ColorSelectorRow
import io.github.m0nkeysan.tally.ui.components.FieldLabel
import io.github.m0nkeysan.tally.ui.components.FlatTextField
import io.github.m0nkeysan.tally.ui.utils.parseColor
import org.jetbrains.compose.resources.stringResource

@Composable
fun EditCounterScreen(
    id: String,
    initialName: String,
    initialCount: Int,
    initialColor: Long,
    onBack: () -> Unit,
    onSave: (String, String, Int, Long) -> Unit,
    onDelete: (String) -> Unit
) {
    var name by remember { mutableStateOf(initialName) }
    var countText by remember { mutableStateOf(initialCount.toString()) }
    var selectedColor by remember { mutableStateOf(Color(initialColor)) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val keyboardController = LocalSoftwareKeyboardController.current

    val selectedColorHex = remember(selectedColor) {
        val argb = selectedColor.toArgb()
        val hex = (0xFFFFFF and argb).toString(16).padStart(6, '0').uppercase()
        "#$hex"
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            containerColor = MaterialTheme.colorScheme.surface,
            title = {
                Text(
                    stringResource(Res.string.dialog_delete_counter_title),
                    fontWeight = FontWeight.Bold
                )
            },
            text = { Text(stringResource(Res.string.dialog_delete_counter_message)) },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    onDelete(id)
                }) {
                    Text(
                        stringResource(Res.string.action_delete),
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(
                        stringResource(Res.string.action_cancel),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            // Flat, Immersive Header
            Surface(
                color = selectedColor,
                contentColor = Color.White,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .height(80.dp)
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(
                            GameIcons.ArrowBack,
                            contentDescription = stringResource(Res.string.action_back)
                        )
                    }
                    Text(
                        text = stringResource(Res.string.counter_edit_title),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold
                    )
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(
                            GameIcons.Delete,
                            contentDescription = stringResource(Res.string.counter_edit_cd_delete)
                        )
                    }
                }
            }
        },
        bottomBar = {
            // Prominent Flat Save Button
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface
            ) {
                Button(
                    onClick = {
                        val sanitizedName = sanitizeCounterName(name) ?: return@Button
                        val count = countText.toIntOrNull() ?: 0
                        val colorArgb = (selectedColor.alpha * 255).toLong().shl(24) or
                                (selectedColor.red * 255).toLong().shl(16) or
                                (selectedColor.green * 255).toLong().shl(8) or
                                (selectedColor.blue * 255).toLong()

                        onSave(id, sanitizedName, count, colorArgb)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(24.dp)
                        .height(64.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = selectedColor,
                        contentColor = Color.White
                    ),
                    shape = MaterialTheme.shapes.extraLarge,
                    elevation = null
                ) {
                    Text(
                        text = stringResource(Res.string.counter_edit_action_save),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding())
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // 1. Unified Color Selection
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                FieldLabel(text = stringResource(Res.string.counter_edit_label_color))
                ColorSelectorRow(
                    selectedColorHex = selectedColorHex,
                    onColorSelected = { hex ->
                        selectedColor = parseColor(hex)
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // 2. Name Field
            FlatTextField(
                value = name,
                onValueChange = { name = it },
                label = stringResource(Res.string.counter_edit_field_name),
                placeholder = stringResource(Res.string.counter_edit_placeholder_name),
                accentColor = selectedColor,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done,
                    autoCorrectEnabled = false,
                    keyboardType = KeyboardType.Text
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        keyboardController?.hide()
                    }
                ),
            )

            // 3. Value Field
            FlatTextField(
                value = countText,
                onValueChange = {
                    if (it.isEmpty() || it == "-" || it.all { char -> char.isDigit() || char == '-' }) countText =
                        it
                },
                label = stringResource(Res.string.counter_edit_field_value),
                placeholder = stringResource(Res.string.counter_edit_placeholder_value),
                accentColor = selectedColor,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done,
                    autoCorrectEnabled = false,
                    keyboardType = KeyboardType.Number
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        keyboardController?.hide()
                    }
                )
            )
        }
    }
}


