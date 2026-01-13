package io.github.m0nkeysan.gamekeeper.ui.screens.dice

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
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

/**
 * Dice Roller Screen matching GameKeeper design
 *
 * Layout:
 * - Top: Centered title with back button
 * - Center: Large bordered box with total value
 * - Bottom: Individual rolls, total, instruction text
 * - Tap to roll or long-press big square for settings
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
                 title = { 
                     Box(
                         modifier = Modifier.fillMaxWidth(),
                         contentAlignment = Alignment.Center
                     ) {
                         Column(
                             horizontalAlignment = Alignment.CenterHorizontally
                         ) {
                             Text(
                                 "Dice",
                                 style = MaterialTheme.typography.titleLarge,
                                 fontWeight = FontWeight.Bold
                             )
                             Text(
                                 text = "${configuration.numberOfDice}d${configuration.diceType.sides}",
                                 style = MaterialTheme.typography.bodySmall
                             )
                         }
                     }
                 },
                 navigationIcon = {
                     IconButton(onClick = onBack) {
                         Icon(GameIcons.ArrowBack, contentDescription = "Back")
                     }
                 },
                 actions = {
                     IconButton(onClick = { showSettingsDialog = true }) {
                         Icon(GameIcons.Settings, contentDescription = "Settings")
                     }
                 }
             )
         },
         containerColor = MaterialTheme.colorScheme.background,
         modifier = Modifier.fillMaxSize()
     ) { paddingValues ->
          Column(
             modifier = Modifier
                 .fillMaxSize()
                 .padding(paddingValues)
                 .background(MaterialTheme.colorScheme.background)
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
             // Top: Configuration Badge
             Column(
                 horizontalAlignment = Alignment.CenterHorizontally,
                 modifier = Modifier.padding(top = 32.dp)
             ) {
                 Surface(
                     color = MaterialTheme.colorScheme.primaryContainer,
                     shape = RoundedCornerShape(50),
                     modifier = Modifier.height(48.dp)
                 ) {
                     Box(
                         contentAlignment = Alignment.Center,
                         modifier = Modifier.padding(horizontal = 32.dp)
                     ) {
                         Text(
                             text = "${configuration.numberOfDice} × d${configuration.diceType.sides}",
                             color = MaterialTheme.colorScheme.primary,
                             fontWeight = FontWeight.Bold,
                             style = MaterialTheme.typography.titleSmall,
                             fontSize = 16.sp
                         )
                     }
                 }
             }

             Spacer(modifier = Modifier.height(32.dp))

             // Center: Big box with total value
             Box(
                 contentAlignment = Alignment.Center,
                 modifier = Modifier
                     .size(240.dp)
                     .background(
                         color = MaterialTheme.colorScheme.primary,
                         shape = MaterialTheme.shapes.large
                     )
                     .combinedClickable(
                         enabled = !isRolling,
                         indication = null,
                         interactionSource = remember { MutableInteractionSource() },
                         onClick = {
                             hapticFeedback.performHapticFeedback(HapticType.LIGHT)
                             viewModel.rollDice()
                         },
                         onLongClick = {
                             hapticFeedback.performHapticFeedback(HapticType.MEDIUM)
                             showSettingsDialog = true
                         }
                     )
             ) {
                 Text(
                     text = (currentRoll?.total ?: 0).toString(),
                     fontSize = 96.sp,
                     color = MaterialTheme.colorScheme.onPrimary,
                     fontWeight = FontWeight.Bold
                 )
             }

             Spacer(modifier = Modifier.height(24.dp))

             // Bottom: Results summary
             Column(
                 horizontalAlignment = Alignment.CenterHorizontally,
                 modifier = Modifier.padding(bottom = 40.dp, start = 16.dp, end = 16.dp)
             ) {
                 if (currentRoll != null && currentRoll!!.individualResults.isNotEmpty()) {
                     val rollsString = currentRoll!!.individualResults.joinToString(" ") { "[$it]" }
                     
                     Text(
                         text = "Rolls: $rollsString",
                         style = MaterialTheme.typography.titleSmall,
                         color = MaterialTheme.colorScheme.onBackground,
                         fontSize = 18.sp,
                         fontWeight = FontWeight.Bold
                     )

                     Spacer(modifier = Modifier.height(8.dp))

                     // Down Arrow
                     Icon(
                         imageVector = Icons.Default.South,
                         contentDescription = null,
                         tint = MaterialTheme.colorScheme.onSurfaceVariant,
                         modifier = Modifier.size(20.dp)
                     )

                     Spacer(modifier = Modifier.height(8.dp))
                 } else {
                     Spacer(modifier = Modifier.height(60.dp))
                 }

                 // Total Text
                 Text(
                     text = "Total: ${currentRoll?.total ?: 0}",
                     style = MaterialTheme.typography.headlineSmall,
                     color = MaterialTheme.colorScheme.primary,
                     fontWeight = FontWeight.Bold,
                     fontSize = 28.sp
                 )
                 
                 Spacer(modifier = Modifier.height(32.dp))

                 // Helper Text
                 Text(
                     text = "Tap anywhere to roll. Long-press the box for settings.",
                     style = MaterialTheme.typography.labelMedium,
                     color = MaterialTheme.colorScheme.onSurfaceVariant,
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
             containerColor = MaterialTheme.colorScheme.surface,
             scrimColor = MaterialTheme.colorScheme.scrim.copy(alpha = 0.32f)
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
             containerColor = MaterialTheme.colorScheme.surface,
             scrimColor = MaterialTheme.colorScheme.scrim.copy(alpha = 0.32f)
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
    var numberOfDice by remember { mutableFloatStateOf(configuration.numberOfDice.toFloat()) }
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
             color = MaterialTheme.colorScheme.primary
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
                         .background(MaterialTheme.colorScheme.primaryContainer, MaterialTheme.shapes.small)
                         .padding(horizontal = 12.dp, vertical = 6.dp)
                 )
             }
         }
         
         // Dice Type Selection
          Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
             Text("Dice Type", style = MaterialTheme.typography.labelMedium)
             DiceTypeSelector(
                 selectedType = diceType,
                 onTypeSelected = { diceType = it }
             )
             
             // Custom Input
             Text("Custom Dice", style = MaterialTheme.typography.labelMedium)
             var customInput by remember { mutableStateOf(if (diceType is DiceType.Custom) diceType.sides.toString() else "") }
             TextField(
                 value = customInput,
                 onValueChange = { 
                     customInput = it
                     if (it.isNotBlank() && it.toIntOrNull() != null) {
                         val sides = it.toInt()
                         if (sides in 2..99) {
                             diceType = DiceType.Custom(sides)
                         }
                     }
                 },
                 modifier = Modifier.fillMaxWidth(),
                 placeholder = { Text("2-99", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                 singleLine = true,
                 keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                 colors = TextFieldDefaults.colors(
                     focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                     unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                     focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                     unfocusedIndicatorColor = Color.Transparent,
                     cursorColor = MaterialTheme.colorScheme.primary
                 ),
                 shape = MaterialTheme.shapes.medium
             )
         }
         
         // Toggles
         Row(
             modifier = Modifier
                 .fillMaxWidth()
                 .background(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.shapes.medium)
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
                 .background(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.shapes.medium)
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
                 colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.primary)
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
                     containerColor = MaterialTheme.colorScheme.primary,
                     contentColor = MaterialTheme.colorScheme.onPrimary
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
             color = MaterialTheme.colorScheme.primary
         )
         
         Text(
             text = "Enter the number of sides (2-99)",
             style = MaterialTheme.typography.bodyMedium,
             color = MaterialTheme.colorScheme.onSurfaceVariant
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
                 focusedContainerColor = MaterialTheme.colorScheme.surface,
                 unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                 focusedIndicatorColor = if (error != null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                 unfocusedIndicatorColor = if (error != null) MaterialTheme.colorScheme.error else Color.Transparent,
                 cursorColor = MaterialTheme.colorScheme.primary
             ),
             shape = MaterialTheme.shapes.large,
             isError = error != null
         )
         
         if (error != null) {
             Text(
                 text = error!!,
                 color = MaterialTheme.colorScheme.error,
                 style = MaterialTheme.typography.labelSmall,
                 modifier = Modifier.padding(start = 4.dp)
             )
         }
         
         // Quick Select Buttons
         Text(
             text = "Quick Select",
             style = MaterialTheme.typography.labelMedium,
             color = MaterialTheme.colorScheme.onSurfaceVariant
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
                             contentColor = if (inputValue == value.toString()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                             containerColor = if (inputValue == value.toString()) MaterialTheme.colorScheme.primaryContainer else Color.Transparent
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
                 colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.primary)
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
                     containerColor = MaterialTheme.colorScheme.primary,
                     contentColor = MaterialTheme.colorScheme.onPrimary,
                     disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                     disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
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
    onTypeSelected: (DiceType) -> Unit
) {
    val standardDiceTypes = listOf(
        DiceType.D4,
        DiceType.D6,
        DiceType.D8,
        DiceType.D10,
        DiceType.D12,
        DiceType.D20
    )
    
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
             containerColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
             contentColor = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
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
