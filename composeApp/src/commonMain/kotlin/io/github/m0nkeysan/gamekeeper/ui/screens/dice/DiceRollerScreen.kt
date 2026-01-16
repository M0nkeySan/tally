package io.github.m0nkeysan.gamekeeper.ui.screens.dice

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.South
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
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
import io.github.m0nkeysan.gamekeeper.ui.strings.AppStrings.CD_BACK
import io.github.m0nkeysan.gamekeeper.ui.strings.AppStrings.CD_SETTINGS

/**
 * Main Dice Roller Screen
 *
 * Features:
 * - Roll dice with configurable number and type (d4-d20 or custom 2-99)
 * - Real-time configuration display in TopAppBar
 * - Visual animation during rolls (optional)
 * - Shake-to-roll detection (optional)
 * - Settings dialog for configuration
 * - Custom input validation with error messages
 * - Full dark mode support
 *
 * Interactions:
 * - Tap anywhere: Roll dice
 * - Long-press dice box: Open settings
 * - Tap settings icon: Open settings
 * - Shake device: Roll dice (if enabled)
 *
 * @param onBack Callback to navigate back from this screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiceRollerScreen(onBack: () -> Unit) {
    val viewModel: DiceRollerViewModel = viewModel()

    val configuration by viewModel.configuration.collectAsState()
    val isRolling by viewModel.isRolling.collectAsState()
    val currentRollState = viewModel.currentRoll.collectAsState()

    var showSettingsDialog by remember { mutableStateOf(false) }
    val settingsSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val hapticFeedback = rememberHapticFeedbackController()

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
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                             Text(
                                 AppStrings.DICE_SCREEN_TITLE,
                                 style = MaterialTheme.typography.titleLarge,
                                 fontWeight = FontWeight.Bold
                             )
                         }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(GameIcons.ArrowBack, CD_BACK) }
                },
                actions = {
                    IconButton(onClick = { showSettingsDialog = true }) {
                        Icon(
                            GameIcons.Settings,
                            CD_SETTINGS
                        )
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
                             text = AppStrings.DICE_DISPLAY_FORMAT.format(configuration.numberOfDice, configuration.diceType.sides),
                             color = MaterialTheme.colorScheme.onPrimaryContainer,
                             fontWeight = FontWeight.Bold,
                             style = MaterialTheme.typography.titleSmall,
                             fontSize = 16.sp
                         )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            DiceResultBox(
                currentRoll = currentRollState.value,
                isRolling = isRolling,
                animationEnabled = configuration.animationEnabled,
                onTap = {
                    hapticFeedback.performHapticFeedback(HapticType.LIGHT)
                    viewModel.rollDice()
                },
                onLongPress = {
                    hapticFeedback.performHapticFeedback(HapticType.MEDIUM)
                    showSettingsDialog = true
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            val roll = currentRollState.value
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(bottom = 40.dp, start = 16.dp, end = 16.dp)
            ) {
                 if (roll != null && roll.individualResults.isNotEmpty()) {
                     val rollsString = roll.individualResults.joinToString(" ") { "[$it]" }
                     Text(
                         text = AppStrings.DICE_ROLLS_FORMAT.format(rollsString),
                         style = MaterialTheme.typography.titleSmall,
                         color = MaterialTheme.colorScheme.onBackground,
                         fontSize = 18.sp,
                         fontWeight = FontWeight.Bold
                     )
                    Spacer(modifier = Modifier.height(8.dp))
                    Icon(
                        Icons.Default.South,
                        null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                } else {
                    Spacer(modifier = Modifier.height(60.dp))
                }

                 Text(
                     text = AppStrings.DICE_TOTAL_FORMAT.format(roll?.total ?: 0),
                     style = MaterialTheme.typography.headlineSmall,
                     color = MaterialTheme.colorScheme.primary,
                     fontWeight = FontWeight.Bold,
                     fontSize = 28.sp
                 )

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = AppStrings.DICE_INSTRUCTION_TAP,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    }

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
                onDismiss = { showSettingsDialog = false }
            )
        }
    }
}

/**
 * Display box showing the current dice roll total.
 *
 * Provides visual feedback during rolling:
 * - Scale animation during roll (if animationEnabled)
 * - Text alpha fade during roll (if animationEnabled)
 * - Spring bounce animation when roll completes
 *
 * Interactions:
 * - Single tap: Roll dice
 * - Long press: Open settings
 *
 * @param currentRoll The current dice roll result, or null if no roll yet
 * @param isRolling Whether the dice are currently being rolled
 * @param animationEnabled Whether to show visual animations
 * @param onTap Callback when dice box is tapped
 * @param onLongPress Callback when dice box is long-pressed
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun DiceResultBox(
    currentRoll: DiceRoll?,
    isRolling: Boolean,
    animationEnabled: Boolean,
    onTap: () -> Unit,
    onLongPress: () -> Unit
) {
    val boxScale by animateFloatAsState(
        targetValue = if (isRolling) DiceConstants.BOX_SCALE_RATIO else 1f,
        animationSpec = if (isRolling && animationEnabled) {
            tween(durationMillis = DiceConstants.BOX_SCALE_ANIMATION_DURATION_MS, easing = FastOutSlowInEasing)
        } else {
            spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
        },
        label = "boxScale"
    )

    val textAlpha by animateFloatAsState(
        targetValue = if (isRolling && animationEnabled) 0.5f else 1f,
        label = "textAlpha"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(DiceConstants.DICE_BOX_SIZE)
            .graphicsLayer {
                scaleX = boxScale
                scaleY = boxScale
            }
            .background(
                color = MaterialTheme.colorScheme.primary,
                shape = MaterialTheme.shapes.large
            )
            .combinedClickable(
                enabled = !isRolling,
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = onTap,
                onLongClick = onLongPress
            )
    ) {
        Text(
            text = (currentRoll?.total ?: 0).toString(),
            fontSize = 96.sp,
            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = textAlpha),
            fontWeight = FontWeight.Bold
        )
    }
}

/**
 * Bottom sheet content for dice settings configuration.
 *
 * Allows users to:
 * - Adjust number of dice (1-5) with slider
 * - Select dice type (d4, d6, d8, d10, d12, d20)
 * - Enter custom dice sides (2-99) with validation
 * - Toggle animation on/off
 * - Toggle shake-to-roll on/off
 *
 * Features:
 * - Real-time validation with error messages
 * - Live configuration preview in state variables
 * - Cancel/Save buttons for confirmation
 *
 * @param configuration Current dice configuration
 * @param onConfirm Callback when Save is clicked with new configuration
 * @param onDismiss Callback when Cancel is clicked or sheet is dismissed
 */
@Composable
private fun DiceSettingsBottomSheetContent(
    configuration: DiceConfiguration,
    onConfirm: (DiceConfiguration) -> Unit,
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
        Text(
            text = AppStrings.DICE_DIALOG_TITLE,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(AppStrings.DICE_FIELD_NUMBER, style = MaterialTheme.typography.labelMedium)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Slider(
                    value = numberOfDice,
                    onValueChange = { numberOfDice = it },
                    valueRange = DiceConstants.MIN_NUMBER_OF_DICE.toFloat()..DiceConstants.MAX_NUMBER_OF_DICE.toFloat(),
                    steps = DiceConstants.DICE_SLIDER_STEPS,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = numberOfDice.toInt().toString(),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .background(
                            MaterialTheme.colorScheme.primaryContainer,
                            MaterialTheme.shapes.small
                        )
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }

         Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
             Text(AppStrings.DICE_FIELD_TYPE, style = MaterialTheme.typography.labelMedium)
             DiceTypeSelector(
                 selectedType = diceType,
                 onTypeSelected = { diceType = it }
             )

             Text(AppStrings.DICE_FIELD_CUSTOM, style = MaterialTheme.typography.labelMedium)
             var customInput by remember { mutableStateOf(if (diceType is DiceType.Custom) diceType.sides.toString() else "") }
             var customInputError by remember { mutableStateOf<String?>(null) }
             
             LaunchedEffect(diceType) {
                 if (diceType is DiceType.Custom) {
                     customInput = diceType.sides.toString()
                     customInputError = null
                 } else {
                     customInput = ""
                     customInputError = null
                 }
             }
             
              LaunchedEffect(customInput) {
                  customInputError = when {
                      customInput.isBlank() -> null
                      customInput.toIntOrNull() == null -> AppStrings.DICE_ERROR_NOT_VALID
                      customInput.toInt() < DiceConstants.MIN_CUSTOM_SIDES -> AppStrings.DICE_ERROR_MIN_SIDES.format(DiceConstants.MIN_CUSTOM_SIDES)
                      customInput.toInt() > DiceConstants.MAX_CUSTOM_SIDES -> AppStrings.DICE_ERROR_MAX_SIDES.format(DiceConstants.MAX_CUSTOM_SIDES)
                      else -> null
                  }
              }
             
             TextField(
                 value = customInput,
                 onValueChange = { 
                     customInput = it
                     if (it.isNotBlank() && it.toIntOrNull() != null) {
                         val sides = it.toInt()
                         if (sides in DiceConstants.MIN_CUSTOM_SIDES..DiceConstants.MAX_CUSTOM_SIDES) {
                             diceType = DiceType.Custom(sides)
                         }
                     }
                 },
                 modifier = Modifier.fillMaxWidth(),
                 placeholder = { Text(AppStrings.DICE_PLACEHOLDER_CUSTOM, color = MaterialTheme.colorScheme.onSurfaceVariant) },
                 singleLine = true,
                 keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                 colors = TextFieldDefaults.colors(
                     focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                     unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                     focusedIndicatorColor = if (customInputError != null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                     unfocusedIndicatorColor = if (customInputError != null) MaterialTheme.colorScheme.error else Color.Transparent,
                     cursorColor = MaterialTheme.colorScheme.primary
                 ),
                 shape = MaterialTheme.shapes.medium,
                 isError = customInputError != null
             )
             
             if (customInputError != null) {
                 Text(
                     text = customInputError!!,
                     color = MaterialTheme.colorScheme.error,
                     style = MaterialTheme.typography.labelSmall,
                     modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                 )
             }
         }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.shapes.medium)
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(AppStrings.DICE_SETTING_ANIMATION, style = MaterialTheme.typography.bodyMedium)
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
            Text(AppStrings.DICE_SETTING_SHAKE, style = MaterialTheme.typography.bodyMedium)
            Switch(checked = shakeEnabled, onCheckedChange = { shakeEnabled = it })
        }

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
                Text(AppStrings.DICE_ACTION_CANCEL)
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
                Text(AppStrings.DICE_ACTION_SAVE)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

/**
 * Selector for standard dice types.
 *
 * Displays 6 buttons for common RPG dice:
 * - d4, d6, d8, d10, d12, d20
 *
 * Arranged in 2 rows of 3 buttons each.
 *
 * @param selectedType Currently selected dice type
 * @param onTypeSelected Callback when a dice type is selected
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
 * Individual button chip for selecting a dice type.
 *
 * Visual feedback:
 * - Selected state: Primary color background
 * - Unselected state: Surface variant background
 *
 * @param text Display text for the chip (e.g., "D6")
 * @param selected Whether this chip is currently selected
 * @param onClick Callback when chip is clicked
 * @param modifier Optional modifier for customization
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
