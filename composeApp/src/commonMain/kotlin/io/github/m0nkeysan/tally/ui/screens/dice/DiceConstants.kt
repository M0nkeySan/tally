package io.github.m0nkeysan.tally.ui.screens.dice

import androidx.compose.ui.unit.dp

/**
 * Constants for Dice Roller feature.
 * Centralized configuration values to improve maintainability.
 */
object DiceConstants {
    // ============ Animation & Timing ============
    /** Number of iterations for number scrambling animation */
    const val SCRAMBLE_ITERATIONS = 12
    
    /** Delay between number changes during scramble (milliseconds) */
    const val SCRAMBLE_DELAY_MS = 60L
    
    /** Scale ratio when dice box is being rolled */
    const val BOX_SCALE_RATIO = 0.85f
    
    /** Duration of box scale animation (milliseconds) */
    const val BOX_SCALE_ANIMATION_DURATION_MS = 150
    
    // ============ Dimensions ============
    /** Size of the dice result display box */
    val DICE_BOX_SIZE = 240.dp
    
    // ============ Configuration Ranges ============
    /** Minimum number of dice that can be rolled */
    const val MIN_NUMBER_OF_DICE = 1
    
    /** Maximum number of dice that can be rolled */
    const val MAX_NUMBER_OF_DICE = 5
    
    /** Number of steps for the dice count slider (excluding min/max) */
    const val DICE_SLIDER_STEPS = 3
    
    /** Minimum number of sides for custom dice */
    const val MIN_CUSTOM_SIDES = 2
    
    /** Maximum number of sides for custom dice */
    const val MAX_CUSTOM_SIDES = 99
}
