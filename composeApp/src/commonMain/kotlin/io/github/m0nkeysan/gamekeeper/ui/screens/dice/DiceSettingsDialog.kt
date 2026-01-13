package io.github.m0nkeysan.gamekeeper.ui.screens.dice

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import io.github.m0nkeysan.gamekeeper.core.model.DiceConfiguration
import io.github.m0nkeysan.gamekeeper.core.model.DiceType
import io.github.m0nkeysan.gamekeeper.ui.components.FieldLabel
import io.github.m0nkeysan.gamekeeper.ui.theme.GameColors

/**
 * Dialog for configuring dice roller settings
 *
 * Features:
 * - Number of dice slider (1-5)
 * - Dice type selection (d4, d6, d8, d10, d12, d20, custom)
 * - Animation toggle
 * - Shake-to-roll toggle
 * - Save and cancel buttons
 *
 * @param configuration Current configuration
 * @param onDismiss Callback when dialog is dismissed
 * @param onConfirm Callback with new configuration when confirmed
 * @param onShowCustomDialog Callback to show custom dice dialog
 */
@Composable
fun DiceSettingsDialog(
    configuration: DiceConfiguration,
    onDismiss: () -> Unit,
    onConfirm: (DiceConfiguration) -> Unit,
    onShowCustomDialog: () -> Unit
) {
    var numberOfDice by remember { mutableStateOf(configuration.numberOfDice.toFloat()) }
    var diceType by remember { mutableStateOf(configuration.diceType) }
    var animationEnabled by remember { mutableStateOf(configuration.animationEnabled) }
    var shakeEnabled by remember { mutableStateOf(configuration.shakeEnabled) }
    
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
                        text = "Dice Settings",
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
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // Number of Dice Slider
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        FieldLabel(text = "NUMBER OF DICE")
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Slider(
                                value = numberOfDice,
                                onValueChange = { numberOfDice = it },
                                valueRange = 1f..5f,
                                steps = 3,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = numberOfDice.toInt().toString(),
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier
                                    .background(
                                        color = GameColors.PrimaryLight,
                                        shape = MaterialTheme.shapes.small
                                    )
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                    
                    // Dice Type Selection
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        FieldLabel(text = "DICE TYPE")
                        DiceTypeSelector(
                            selectedType = diceType,
                            onTypeSelected = { diceType = it },
                            onCustomSelected = onShowCustomDialog
                        )
                    }
                    
                    // Animation Toggle
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = GameColors.Surface1,
                                shape = MaterialTheme.shapes.medium
                            )
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Animation",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Switch(
                            checked = animationEnabled,
                            onCheckedChange = { animationEnabled = it }
                        )
                    }
                    
                    // Shake Toggle
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = GameColors.Surface1,
                                shape = MaterialTheme.shapes.medium
                            )
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Shake to Roll",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Switch(
                            checked = shakeEnabled,
                            onCheckedChange = { shakeEnabled = it }
                        )
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
                                val newConfig = DiceConfiguration(
                                    numberOfDice = numberOfDice.toInt(),
                                    diceType = diceType,
                                    animationEnabled = animationEnabled,
                                    shakeEnabled = shakeEnabled
                                )
                                onConfirm(newConfig)
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = GameColors.Primary,
                                contentColor = GameColors.Surface0
                            )
                        ) {
                            Text("Save")
                        }
                    }
                }
            }
        }
    }
}

/**
 * Dice type selector with chips for quick selection
 * Shows d4, d6, d8, d10, d12, d20, and custom button
 */
@Composable
private fun DiceTypeSelector(
    selectedType: DiceType,
    onTypeSelected: (DiceType) -> Unit,
    onCustomSelected: () -> Unit
) {
    val standardDiceTypes = listOf(
        DiceType.D4,
        DiceType.D6,
        DiceType.D8,
        DiceType.D10,
        DiceType.D12,
        DiceType.D20
    )
    
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        // Standard dice chips
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            standardDiceTypes.chunked(3).forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    row.forEach { type ->
                        DiceTypeChip(
                            text = type.displayName.uppercase(),
                            selected = selectedType == type,
                            onClick = { onTypeSelected(type) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    // Fill remaining space if row is incomplete
                    repeat(3 - row.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
        
        // Custom dice button
        OutlinedButton(
            onClick = onCustomSelected,
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .border(
                    width = 2.dp,
                    color = if (selectedType is DiceType.Custom) GameColors.Primary else GameColors.TextSecondary,
                    shape = MaterialTheme.shapes.medium
                ),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = if (selectedType is DiceType.Custom) GameColors.Primary else GameColors.TextSecondary
            )
        ) {
            Text(
                text = if (selectedType is DiceType.Custom) 
                    "Custom (${selectedType.sides})" 
                else 
                    "Custom",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * Individual dice type chip
 */
@Composable
private fun DiceTypeChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(40.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) GameColors.Primary else GameColors.Surface1,
            contentColor = if (selected) GameColors.Surface0 else GameColors.TextPrimary
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold
        )
    }
}
