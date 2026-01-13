package io.github.m0nkeysan.gamekeeper.ui.screens.dice

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.m0nkeysan.gamekeeper.GameIcons
import io.github.m0nkeysan.gamekeeper.core.model.DiceConfiguration
import io.github.m0nkeysan.gamekeeper.core.model.DiceRoll
import io.github.m0nkeysan.gamekeeper.core.model.DiceType
import io.github.m0nkeysan.gamekeeper.platform.HapticType
import io.github.m0nkeysan.gamekeeper.platform.rememberHapticFeedbackController
import io.github.m0nkeysan.gamekeeper.platform.rememberShakeDetector
import io.github.m0nkeysan.gamekeeper.ui.strings.AppStrings
import io.github.m0nkeysan.gamekeeper.ui.theme.GameColors

/**
 * Main Dice Roller Screen
 *
 * Features:
 * - TopAppBar with back button
 * - Configuration display (e.g., "1d6") with tap-to-open-settings
 * - Central dice display (single large or grid)
 * - Tap to roll interaction
 * - Long-press to open settings
 * - Results display (individual + total)
 * - Instruction text ("Tap to roll" or "Tap or shake to roll")
 * - Shake detection integration
 * - Haptic feedback on all interactions
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiceRollerScreen(onBack: () -> Unit) {
    val viewModel: DiceRollerViewModel = viewModel()
    val configuration by viewModel.configuration.collectAsState()
    val currentRoll by viewModel.currentRoll.collectAsState()
    val isRolling by viewModel.isRolling.collectAsState()
    
    var showSettingsDialog by remember { mutableStateOf(false) }
    var showCustomDialog by remember { mutableStateOf(false) }
    
    val hapticFeedback = rememberHapticFeedbackController()
    
    // Shake detection
    rememberShakeDetector(
        onShake = {
            if (configuration.shakeEnabled && !isRolling) {
                hapticFeedback.performHapticFeedback(HapticType.LIGHT)
                viewModel.rollDice()
            }
        },
        enabled = configuration.shakeEnabled
    )
    
    Scaffold(
            topBar = {
            TopAppBar(
                title = { 
                    Text("Dice Roller", fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(GameIcons.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
            modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(GameColors.Surface0),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Configuration Display & Settings
            ConfigurationBar(
                configuration = configuration,
                onTap = { showSettingsDialog = true },
                isRolling = isRolling
            )
            
            // Main Content Area
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Dice Display
                    if (configuration.numberOfDice == 1) {
                        // Single large die
                        Box(
                            modifier = Modifier.clickable(
                                enabled = !isRolling,
                                onClick = {
                                    hapticFeedback.performHapticFeedback(HapticType.LIGHT)
                                    viewModel.rollDice()
                                }
                            )
                        ) {
                            DiceView(
                                value = currentRoll?.individualResults?.getOrNull(0) ?: 1,
                                diceType = configuration.diceType,
                                isRolling = isRolling,
                                size = 200.dp
                            )
                        }
                    } else {
                        // Multiple dice grid
                        Box(
                            modifier = Modifier.clickable(
                                enabled = !isRolling,
                                onClick = {
                                    hapticFeedback.performHapticFeedback(HapticType.LIGHT)
                                    viewModel.rollDice()
                                }
                            )
                        ) {
                            DiceGridView(
                                diceCount = configuration.numberOfDice,
                                values = currentRoll?.individualResults ?: List(configuration.numberOfDice) { 1 },
                                diceType = configuration.diceType,
                                isRolling = isRolling,
                                diceSize = 120.dp
                            )
                        }
                    }
                    
                    // Results Display
                    if (currentRoll != null) {
                        ResultsDisplay(roll = currentRoll!!)
                    }
                }
            }
            
            // Instructions & Buttons
            InstructionsBar(
                shakeEnabled = configuration.shakeEnabled,
                isRolling = isRolling,
                onRoll = {
                    hapticFeedback.performHapticFeedback(HapticType.LIGHT)
                    viewModel.rollDice()
                },
                onSettings = { showSettingsDialog = true }
            )
        }
    }
    
    // Dialogs
    if (showSettingsDialog) {
        DiceSettingsDialog(
            configuration = configuration,
            onDismiss = { showSettingsDialog = false },
            onConfirm = { newConfig ->
                viewModel.updateConfiguration(newConfig)
                showSettingsDialog = false
                hapticFeedback.performHapticFeedback(HapticType.SUCCESS)
            },
            onShowCustomDialog = {
                showCustomDialog = true
            }
        )
    }
    
    if (showCustomDialog) {
        CustomDiceDialog(
            onDismiss = { showCustomDialog = false },
            onConfirm = { customType ->
                val newConfig = configuration.copy(diceType = customType)
                viewModel.updateConfiguration(newConfig)
                showCustomDialog = false
                showSettingsDialog = false
                hapticFeedback.performHapticFeedback(HapticType.SUCCESS)
            },
            initialSides = if (configuration.diceType is DiceType.Custom) 
                configuration.diceType.sides 
            else 
                20
        )
    }
}

/**
 * Configuration bar showing current settings
 * Tappable to open settings
 */
@Composable
private fun ConfigurationBar(
    configuration: DiceConfiguration,
    onTap: () -> Unit,
    isRolling: Boolean
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !isRolling, onClick = onTap),
        color = GameColors.PrimaryLight,
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Configuration display (e.g., "2d6")
            Text(
                text = "${configuration.numberOfDice}${configuration.diceType.displayName}",
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                color = GameColors.Primary
            )
            
            // Settings info
            Text(
                text = buildString {
                    append("Animation: ${if (configuration.animationEnabled) "On" else "Off"} • ")
                    append("Shake: ${if (configuration.shakeEnabled) "On" else "Off"}")
                },
                style = MaterialTheme.typography.labelSmall,
                color = GameColors.TextSecondary,
                fontSize = 12.sp
            )
            
            Text(
                text = "Tap to customize",
                style = MaterialTheme.typography.labelSmall,
                color = GameColors.TextSecondary,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
            )
        }
    }
}

/**
 * Results display showing individual dice and total
 */
@Composable
private fun ResultsDisplay(roll: DiceRoll) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .background(
                color = GameColors.Surface1,
                shape = MaterialTheme.shapes.medium
            )
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        // Individual results
        if (roll.individualResults.size > 1) {
            Text(
                text = "Individual: ${roll.individualResults.joinToString(", ")}",
                style = MaterialTheme.typography.labelMedium,
                color = GameColors.TextSecondary,
                fontSize = 12.sp
            )
        }
        
        // Total
        Text(
            text = "${AppStrings.DICE_TOTAL}: ${roll.total}",
            style = MaterialTheme.typography.headlineSmall,
            color = GameColors.Primary,
            fontWeight = FontWeight.Bold
        )
    }
}

/**
 * Instructions and action buttons
 */
@Composable
private fun InstructionsBar(
    shakeEnabled: Boolean,
    isRolling: Boolean,
    onRoll: () -> Unit,
    onSettings: () -> Unit
) {
    Surface(
        color = GameColors.Surface1,
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Instructions text
            Text(
                text = if (shakeEnabled) 
                    AppStrings.DICE_TAP_OR_SHAKE 
                else 
                    AppStrings.DICE_TAP_TO_ROLL,
                style = MaterialTheme.typography.bodySmall,
                color = GameColors.TextSecondary,
                textAlign = TextAlign.Center,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
            )
            
            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onSettings,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = GameColors.Primary
                    )
                ) {
                    Icon(
                        imageVector = GameIcons.Settings,
                        contentDescription = "Settings",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Settings")
                }
                
                Button(
                    onClick = onRoll,
                    enabled = !isRolling,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GameColors.Primary,
                        contentColor = GameColors.Surface0,
                        disabledContainerColor = GameColors.Surface2,
                        disabledContentColor = GameColors.TextSecondary
                    )
                ) {
                    Icon(
                        imageVector = GameIcons.Casino,
                        contentDescription = "Roll",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (isRolling) "Rolling..." else "Roll")
                }
            }
        }
    }
}
