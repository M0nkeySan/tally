package io.github.m0nkeysan.gamekeeper.ui.screens.dice

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.m0nkeysan.gamekeeper.core.model.DiceType
import io.github.m0nkeysan.gamekeeper.ui.theme.GameColors

/**
 * Reusable dice visual component with 2D rotation animation
 *
 * Features:
 * - 2D rotation animation (360°, 800ms, FastOutSlowInEasing)
 * - D6 special display with traditional dot patterns
 * - Number display for other dice types
 * - Customizable size and styling
 * - GPU-accelerated animation
 *
 * @param value The number to display (1-N based on dice type)
 * @param diceType The type of dice (D4, D6, D20, etc.)
 * @param isRolling Whether the dice is currently rolling/animating
 * @param size The size of the dice (width and height)
 * @param onClick Callback when dice is clicked
 */
@Composable
fun DiceView(
    value: Int,
    diceType: DiceType = DiceType.D6,
    isRolling: Boolean = false,
    size: Dp = 200.dp,
    onClick: (() -> Unit)? = null
) {
    val rotation = remember { Animatable(0f) }
    
    // Trigger animation when isRolling changes to true
    LaunchedEffect(isRolling) {
        if (isRolling) {
            rotation.animateTo(
                targetValue = 360f,
                animationSpec = tween(
                    durationMillis = 800,
                    easing = FastOutSlowInEasing
                )
            )
            // Reset rotation for next roll
            rotation.snapTo(0f)
        }
    }
    
    Box(
        modifier = Modifier
            .size(size)
            .clip(RoundedCornerShape(16.dp))
            .background(GameColors.PrimaryLight)
            .graphicsLayer {
                rotationZ = rotation.value
            },
        contentAlignment = Alignment.Center
    ) {
        if (diceType == DiceType.D6) {
            // D6 special display with traditional dot patterns
            D6DotDisplay(value = value)
        } else {
            // Number display for other dice types
            Text(
                text = value.toString(),
                fontSize = 64.sp,
                fontWeight = FontWeight.Bold,
                color = GameColors.Primary,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Traditional D6 dot pattern display
 * Shows 1-6 with standard dice dot arrangements
 */
@Composable
private fun D6DotDisplay(value: Int) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        when (value) {
            1 -> {
                // Single dot in center
                Dot(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            2 -> {
                // Two dots diagonal
                Dot(modifier = Modifier.align(Alignment.TopStart))
                Dot(modifier = Modifier.align(Alignment.BottomEnd))
            }
            3 -> {
                // Three dots diagonal line
                Dot(modifier = Modifier.align(Alignment.TopStart))
                Dot(modifier = Modifier.align(Alignment.Center))
                Dot(modifier = Modifier.align(Alignment.BottomEnd))
            }
            4 -> {
                // Four corners
                Dot(modifier = Modifier.align(Alignment.TopStart))
                Dot(modifier = Modifier.align(Alignment.TopEnd))
                Dot(modifier = Modifier.align(Alignment.BottomStart))
                Dot(modifier = Modifier.align(Alignment.BottomEnd))
            }
            5 -> {
                // Four corners + center
                Dot(modifier = Modifier.align(Alignment.TopStart))
                Dot(modifier = Modifier.align(Alignment.TopEnd))
                Dot(modifier = Modifier.align(Alignment.Center))
                Dot(modifier = Modifier.align(Alignment.BottomStart))
                Dot(modifier = Modifier.align(Alignment.BottomEnd))
            }
            6 -> {
                // Two columns of three
                Column(modifier = Modifier.align(Alignment.CenterStart)) {
                    Dot()
                    Dot()
                    Dot()
                }
                Column(modifier = Modifier.align(Alignment.CenterEnd)) {
                    Dot()
                    Dot()
                    Dot()
                }
            }
            else -> {
                // Fallback to number display
                Text(
                    text = value.toString(),
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    color = GameColors.Primary,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

/**
 * A single dot for D6 display
 */
@Composable
private fun Dot(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(16.dp)
            .clip(RoundedCornerShape(50))
            .background(GameColors.Primary)
    )
}

/**
 * Grid layout for multiple dice
 * Displays dice in a responsive grid (1, 2, 3, or more columns)
 *
 * @param diceCount Number of dice to display
 * @param values List of values to display (one per die)
 * @param diceType The type of dice
 * @param isRolling Whether dice are currently rolling
 * @param diceSize Size of each individual die
 */
@Composable
fun DiceGridView(
    diceCount: Int,
    values: List<Int>,
    diceType: DiceType = DiceType.D6,
    isRolling: Boolean = false,
    diceSize: Dp = 120.dp
) {
    val columns = when {
        diceCount <= 2 -> diceCount
        diceCount <= 4 -> 2
        else -> 3
    }
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Simple grid using Row/Column wrapping
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            for (row in 0 until (diceCount + columns - 1) / columns) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(
                        space = 16.dp,
                        alignment = Alignment.CenterHorizontally
                    )
                ) {
                    for (col in 0 until columns) {
                        val index = row * columns + col
                        if (index < diceCount) {
                            DiceView(
                                value = values.getOrElse(index) { 1 },
                                diceType = diceType,
                                isRolling = isRolling,
                                size = diceSize
                            )
                        } else {
                            // Empty space for grid alignment
                            Spacer(modifier = Modifier.size(diceSize))
                        }
                    }
                }
            }
        }
    }
}
