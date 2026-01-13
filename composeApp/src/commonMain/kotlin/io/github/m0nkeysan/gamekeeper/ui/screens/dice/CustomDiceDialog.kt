package io.github.m0nkeysan.gamekeeper.ui.screens.dice

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import io.github.m0nkeysan.gamekeeper.core.model.DiceType
import io.github.m0nkeysan.gamekeeper.ui.components.FieldLabel
import io.github.m0nkeysan.gamekeeper.ui.theme.GameColors

/**
 * Dialog for selecting custom dice sides (2-99)
 *
 * Features:
 * - Number input field (2-99)
 * - Real-time validation with error messages
 * - Quick select buttons for common values
 * - Save and cancel buttons
 *
 * @param onDismiss Callback when dialog is dismissed
 * @param onConfirm Callback with selected custom sides when confirmed
 * @param initialSides Initial value to display (from current custom dice if exists)
 */
@Composable
fun CustomDiceDialog(
    onDismiss: () -> Unit,
    onConfirm: (DiceType.Custom) -> Unit,
    initialSides: Int = 20
) {
    var inputValue by remember { mutableStateOf(initialSides.toString()) }
    var error by remember { mutableStateOf<String?>(null) }
    
    // Validate input whenever it changes
    LaunchedEffect(inputValue) {
        error = when {
            inputValue.isBlank() -> "Please enter a number"
            inputValue.toIntOrNull() == null -> "Please enter a valid number"
            inputValue.toInt() < 2 -> "Minimum is 2 sides"
            inputValue.toInt() > 99 -> "Maximum is 99 sides"
            else -> null
        }
    }
    
    val isValid = error == null && inputValue.isNotBlank()
    
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(vertical = 24.dp)
        ) {
            Column {
                // Colored Header
                Surface(
                    color = GameColors.Primary,
                    contentColor = GameColors.Surface0,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Custom Dice",
                        modifier = Modifier.padding(24.dp),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black
                    )
                }
                
                // Content
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Instructions
                    Text(
                        text = "Enter the number of sides (2-99)",
                        style = MaterialTheme.typography.bodyMedium,
                        color = GameColors.TextSecondary
                    )
                    
                    // Number Input Field
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        FieldLabel(text = "NUMBER OF SIDES")
                        TextField(
                            value = inputValue,
                            onValueChange = { inputValue = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Enter 2-99", color = Color.Gray) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = GameColors.Surface0,
                                unfocusedContainerColor = GameColors.Surface1,
                                focusedIndicatorColor = if (error != null) GameColors.Error else GameColors.Primary,
                                unfocusedIndicatorColor = if (error != null) GameColors.Error else Color.Transparent,
                                cursorColor = GameColors.Primary,
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black
                            ),
                            shape = MaterialTheme.shapes.large,
                            textStyle = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                            isError = error != null
                        )
                        
                        // Error message
                        if (error != null) {
                            Text(
                                text = error!!,
                                color = GameColors.Error,
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                            )
                        }
                    }
                    
                    // Quick select buttons
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Quick Select",
                            style = MaterialTheme.typography.labelMedium,
                            color = GameColors.TextSecondary,
                            fontWeight = FontWeight.Bold
                        )
                        
                        val quickValues = listOf(4, 6, 8, 10, 12, 20, 30, 50, 99)
                        
                        // Grid of quick select buttons
                        quickValues.chunked(3).forEach { row ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                row.forEach { value ->
                                    OutlinedButton(
                                        onClick = {
                                            inputValue = value.toString()
                                        },
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(40.dp)
                                            .border(
                                                width = 1.5.dp,
                                                color = if (inputValue == value.toString())
                                                    GameColors.Primary
                                                else
                                                    GameColors.TextSecondary,
                                                shape = MaterialTheme.shapes.small
                                            ),
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            contentColor = if (inputValue == value.toString()) 
                                                GameColors.Primary 
                                            else 
                                                GameColors.TextSecondary,
                                            containerColor = if (inputValue == value.toString())
                                                GameColors.PrimaryLight
                                            else
                                                Color.Transparent
                                        )
                                    ) {
                                        Text(
                                            text = "d$value",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                                // Fill remaining space if row is incomplete
                                repeat(3 - row.size) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                    
                    // Buttons
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = GameColors.Primary
                            )
                        ) {
                            Text("Cancel")
                        }
                        Button(
                            onClick = {
                                if (isValid) {
                                    onConfirm(DiceType.Custom(inputValue.toInt()))
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            enabled = isValid,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = GameColors.Primary,
                                contentColor = GameColors.Surface0,
                                disabledContainerColor = GameColors.Surface2,
                                disabledContentColor = GameColors.TextSecondary
                            )
                        ) {
                            Text("Confirm")
                        }
                    }
                }
            }
        }
    }
}
