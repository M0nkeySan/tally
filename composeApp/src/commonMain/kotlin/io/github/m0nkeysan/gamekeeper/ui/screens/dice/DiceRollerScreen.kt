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
                                "Dice",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(GameIcons.ArrowBack, "Back") }
                },
                actions = {
                    IconButton(onClick = { showSettingsDialog = true }) {
                        Icon(
                            GameIcons.Settings,
                            "Settings"
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
                            text = "${configuration.numberOfDice} × d${configuration.diceType.sides}",
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
                        text = "Rolls: $rollsString",
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
                    text = "Total: ${roll?.total ?: 0}",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp
                )

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "Tap anywhere to roll. Long-press the box for settings.",
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun DiceResultBox(
    currentRoll: DiceRoll?,
    isRolling: Boolean,
    onTap: () -> Unit,
    onLongPress: () -> Unit
) {
    val boxScale by animateFloatAsState(
        targetValue = if (isRolling) 0.85f else 1f,
        animationSpec = if (isRolling) {
            tween(durationMillis = 150, easing = FastOutSlowInEasing)
        } else {
            spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
        },
        label = "boxScale"
    )

    val textAlpha by animateFloatAsState(
        targetValue = if (isRolling) 0.5f else 1f,
        label = "textAlpha"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(240.dp)
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
 * Bottom sheet content for dice settings
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
            text = "Dice Settings",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

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
                        .background(
                            MaterialTheme.colorScheme.primaryContainer,
                            MaterialTheme.shapes.small
                        )
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }

         Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
             Text("Dice Type", style = MaterialTheme.typography.labelMedium)
             DiceTypeSelector(
                 selectedType = diceType,
                 onTypeSelected = { diceType = it }
             )

             Text("Custom Dice", style = MaterialTheme.typography.labelMedium)
             var customInput by remember { mutableStateOf(if (diceType is DiceType.Custom) diceType.sides.toString() else "") }

             LaunchedEffect(diceType) {
                 if (diceType is DiceType.Custom) {
                     customInput = diceType.sides.toString()
                 } else {
                     customInput = ""
                 }
             }
             
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
