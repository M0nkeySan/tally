package io.github.m0nkeysan.gamekeeper.ui.screens.dice

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.South
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
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

// Dark theme colors
private val DarkBackground = Color(0xFF1C1C1E)
private val SurfaceDark = Color(0xFF2C2C2E)
private val AccentRed = Color(0xFFE57373)
private val TextWhite = Color(0xFFF2F2F7)
private val DarkChipBg = Color(0xFF252022)

/**
 * Dice Roller Screen with Dark Theme
 *
 * Layout:
 * - Top: "Dé" title and configuration chip (5 × d20)
 * - Center: Large bordered box with total value
 * - Bottom: Individual rolls, arrow, total, instruction text
 * - Clickable anywhere to roll
 * - ModalBottomSheet for settings
 * - Shake detection integration
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
    val settingsSheetState = rememberModalBottomSheetState()
    val customSheetState = rememberModalBottomSheetState()
    
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
                title = { },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(GameIcons.ArrowBack, contentDescription = "Back", tint = TextWhite)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkBackground
                ),
                modifier = Modifier.fillMaxWidth()
            )
        },
        containerColor = DarkBackground,
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(DarkBackground)
                .clickable(
                    enabled = !isRolling,
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                    onClick = {
                        hapticFeedback.performHapticFeedback(HapticType.LIGHT)
                        viewModel.rollDice()
                    }
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // --- TOP SECTION: Title and Chip ---
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 20.dp)
            ) {
                Text(
                    text = "Dé",
                    color = TextWhite,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(20.dp))
                
                // Configuration Chip
                Surface(
                    color = DarkChipBg,
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.height(40.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    ) {
                        Text(
                            text = "${configuration.numberOfDice} × d${configuration.diceType.sides}",
                            color = AccentRed,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // --- CENTER SECTION: The Big Box ---
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(300.dp)
                    .border(
                        width = 2.dp,
                        color = AccentRed,
                        shape = RoundedCornerShape(32.dp)
                    )
                    .clickable(
                        enabled = !isRolling,
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                        onClick = {
                            hapticFeedback.performHapticFeedback(HapticType.LIGHT)
                            viewModel.rollDice()
                        }
                    )
            ) {
                Text(
                    text = (currentRoll?.total ?: 0).toString(),
                    fontSize = 120.sp,
                    color = TextWhite,
                    fontWeight = FontWeight.Normal
                )
            }

            // --- BOTTOM SECTION: Details ---
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(bottom = 40.dp, start = 16.dp, end = 16.dp)
            ) {
                // Individual Rolls - [7] [3] [9]
                if (currentRoll != null && currentRoll!!.individualResults.isNotEmpty()) {
                    val rollsString = currentRoll!!.individualResults.joinToString(" ") { "[$it]" }
                    
                    Text(
                        text = "Lancer(s) : $rollsString",
                        color = TextWhite,
                        fontSize = 18.sp,
                        fontFamily = FontFamily.Monospace
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Down Arrow
                    Icon(
                        imageVector = Icons.Default.South,
                        contentDescription = null,
                        tint = TextWhite,
                        modifier = Modifier.size(20.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                } else {
                    Spacer(modifier = Modifier.height(60.dp))
                }

                // Total Text
                Text(
                    text = "Total : ${currentRoll?.total ?: 0}",
                    color = TextWhite,
                    fontSize = 20.sp,
                    fontFamily = FontFamily.Monospace
                )
                
                Spacer(modifier = Modifier.height(40.dp))

                // Helper Text
                Text(
                    text = "Pour lancer les dés, touchez n'importe où sur l'écran.",
                    color = Color.Gray,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
    
    // Settings Bottom Sheet
    if (showSettingsDialog) {
        ModalBottomSheet(
            onDismissRequest = { showSettingsDialog = false },
            sheetState = settingsSheetState,
            containerColor = SurfaceDark,
            scrimColor = Color.Black.copy(alpha = 0.32f)
        ) {
            DiceSettingsBottomSheetContent(
                configuration = configuration,
                onConfirm = { newConfig ->
                    viewModel.updateConfiguration(newConfig)
                    showSettingsDialog = false
                    hapticFeedback.performHapticFeedback(HapticType.SUCCESS)
                },
                onShowCustomDialog = {
                    showCustomDialog = true
                },
                onDismiss = { showSettingsDialog = false }
            )
        }
    }
    
    // Custom Dice Bottom Sheet
    if (showCustomDialog) {
        ModalBottomSheet(
            onDismissRequest = { showCustomDialog = false },
            sheetState = customSheetState,
            containerColor = SurfaceDark,
            scrimColor = Color.Black.copy(alpha = 0.32f)
        ) {
            CustomDiceBottomSheetContent(
                onConfirm = { customType ->
                    val newConfig = configuration.copy(diceType = customType)
                    viewModel.updateConfiguration(newConfig)
                    showCustomDialog = false
                    showSettingsDialog = false
                    hapticFeedback.performHapticFeedback(HapticType.SUCCESS)
                },
                onDismiss = { showCustomDialog = false },
                initialSides = if (configuration.diceType is DiceType.Custom) 
                    configuration.diceType.sides 
                else 
                    20
            )
        }
    }
}



/**
 * Bottom sheet content for dice settings
 */
@Composable
private fun DiceSettingsBottomSheetContent(
    configuration: DiceConfiguration,
    onConfirm: (DiceConfiguration) -> Unit,
    onShowCustomDialog: () -> Unit,
    onDismiss: () -> Unit
) {
    var numberOfDice by remember { mutableStateOf(configuration.numberOfDice.toFloat()) }
    var diceType by remember { mutableStateOf(configuration.diceType) }
    var animationEnabled by remember { mutableStateOf(configuration.animationEnabled) }
    var shakeEnabled by remember { mutableStateOf(configuration.shakeEnabled) }
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Header
        Text(
            text = "Dice Settings",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = GameColors.Primary
        )
        
        // Number of Dice Slider
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Number of Dice", style = MaterialTheme.typography.labelMedium)
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
                        .background(GameColors.PrimaryLight, MaterialTheme.shapes.small)
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
        
        // Dice Type Selection
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Dice Type", style = MaterialTheme.typography.labelMedium)
            DiceTypeSelector(
                selectedType = diceType,
                onTypeSelected = { diceType = it },
                onCustomSelected = onShowCustomDialog
            )
        }
        
        // Toggles
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(GameColors.Surface1, MaterialTheme.shapes.medium)
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Animation", style = MaterialTheme.typography.bodyMedium)
            Switch(checked = animationEnabled, onCheckedChange = { animationEnabled = it })
        }
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(GameColors.Surface1, MaterialTheme.shapes.medium)
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Shake to Roll", style = MaterialTheme.typography.bodyMedium)
            Switch(checked = shakeEnabled, onCheckedChange = { shakeEnabled = it })
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
                colors = ButtonDefaults.outlinedButtonColors(contentColor = GameColors.Primary)
            ) {
                Text("Cancel")
            }
            Button(
                onClick = {
                    onConfirm(
                        DiceConfiguration(
                            numberOfDice = numberOfDice.toInt(),
                            diceType = diceType,
                            animationEnabled = animationEnabled,
                            shakeEnabled = shakeEnabled
                        )
                    )
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
        
        Spacer(modifier = Modifier.height(16.dp))
    }
}

/**
 * Bottom sheet content for custom dice
 */
@Composable
private fun CustomDiceBottomSheetContent(
    onConfirm: (DiceType.Custom) -> Unit,
    onDismiss: () -> Unit,
    initialSides: Int = 20
) {
    var inputValue by remember { mutableStateOf(initialSides.toString()) }
    var error by remember { mutableStateOf<String?>(null) }
    
    LaunchedEffect(inputValue) {
        error = when {
            inputValue.isBlank() -> null
            inputValue.toIntOrNull() == null -> "Please enter a valid number"
            inputValue.toInt() < 2 -> "Minimum is 2 sides"
            inputValue.toInt() > 99 -> "Maximum is 99 sides"
            else -> null
        }
    }
    
    val isValid = error == null && inputValue.isNotBlank()
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Text(
            text = "Custom Dice",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = GameColors.Primary
        )
        
        Text(
            text = "Enter the number of sides (2-99)",
            style = MaterialTheme.typography.bodyMedium,
            color = GameColors.TextSecondary
        )
        
        // Input Field
        TextField(
            value = inputValue,
            onValueChange = { inputValue = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Enter 2-99") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = GameColors.Surface0,
                unfocusedContainerColor = GameColors.Surface1,
                focusedIndicatorColor = if (error != null) GameColors.Error else GameColors.Primary,
                unfocusedIndicatorColor = if (error != null) GameColors.Error else Color.Transparent,
                cursorColor = GameColors.Primary
            ),
            shape = MaterialTheme.shapes.large,
            isError = error != null
        )
        
        if (error != null) {
            Text(
                text = error!!,
                color = GameColors.Error,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
        
        // Quick Select Buttons
        Text(
            text = "Quick Select",
            style = MaterialTheme.typography.labelMedium,
            color = GameColors.TextSecondary
        )
        
        val quickValues = listOf(4, 6, 8, 10, 12, 20, 30, 50, 99)
        quickValues.chunked(3).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                row.forEach { value ->
                    OutlinedButton(
                        onClick = { inputValue = value.toString() },
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = if (inputValue == value.toString()) GameColors.Primary else GameColors.TextSecondary,
                            containerColor = if (inputValue == value.toString()) GameColors.PrimaryLight else Color.Transparent
                        )
                    ) {
                        Text("d$value", style = MaterialTheme.typography.labelSmall)
                    }
                }
                repeat(3 - row.size) {
                    Spacer(modifier = Modifier.weight(1f))
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
                colors = ButtonDefaults.outlinedButtonColors(contentColor = GameColors.Primary)
            ) {
                Text("Cancel")
            }
            Button(
                onClick = { if (isValid) onConfirm(DiceType.Custom(inputValue.toInt())) },
                enabled = isValid,
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
                Text("Confirm")
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
    }
}

/**
 * Dice type selector with chips for quick selection
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
                .height(40.dp),
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
