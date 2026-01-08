package io.github.m0nkeysan.gamekeeper.ui.theme

import androidx.compose.ui.unit.dp

/**
 * Centralized dimension constants for consistent spacing and sizing across the app.
 * Uses Compose dp (density-independent pixels) units for responsive design.
 *
 * This approach provides:
 * - Consistent spacing and sizing across the app
 * - Easy adjustment of all dimensions from one place
 * - Clear documentation of design system dimensions
 */
object AppDimensions {
    // Spacing (16dp base unit system)
    val spacing_4 = 4.dp
    val spacing_8 = 8.dp
    val spacing_12 = 12.dp
    val spacing_16 = 16.dp
    val spacing_24 = 24.dp
    val spacing_32 = 32.dp
    val spacing_48 = 48.dp
    val spacing_64 = 64.dp
    val spacing_80 = 80.dp
    
    // Common sizes
    val iconSize_small = 24.dp      // Standard icon size
    val iconSize_medium = 32.dp     // Medium icon size
    val iconSize_large = 48.dp      // Large icon size
    val iconSize_xlarge = 64.dp     // Extra large icon size
    
    // Card and elevation
    val cardElevation_flat = 0.dp   // Flat design standard
    val cardElevation_highlight = 2.dp  // Subtle highlight
    val cardCornerRadius_small = 8.dp
    val cardCornerRadius_medium = 12.dp
    val cardCornerRadius_large = 16.dp
    
    // Button sizes
    val buttonHeight_small = 40.dp
    val buttonHeight_medium = 48.dp
    val buttonHeight_large = 56.dp
    val buttonMinWidth = 48.dp
    
    // Text field sizes
    val textFieldMinHeight = 56.dp
    
    // Drag and drop
    val dragElevation = 8.dp
    val dragScale = 1.05f
    
    // Animation durations
    const val animationDuration_short = 300  // milliseconds
    const val animationDuration_medium = 600  // milliseconds
    const val animationDuration_long = 1000  // milliseconds
    
    // Countdown animation
    const val countdownStartDelay = 500L  // milliseconds
    const val countdownAnimationDuration = 2000f  // milliseconds (for animation value)
    
    // Game-specific constants
    const val yahtzeeMaxDiceSum = 30
    const val yahtzeeUpperBonusThreshold = 63
    const val yahtzeeUpperBonus = 35
    const val tarotMinPlayers = 3
    const val tarotMaxPlayers = 5
    const val yahtzeeMinPlayers = 2
    const val yahtzeeMaxPlayers = 8
    
    // Quick adjust values for Counter
    val quickAdjustValues = listOf(5, 10, 15, 20, 50, 100, 200)
}

/**
 * Commonly used dimension shortcuts for brevity in code.
 */
object Dimen {
    val xs = AppDimensions.spacing_4
    val sm = AppDimensions.spacing_8
    val md = AppDimensions.spacing_12
    val lg = AppDimensions.spacing_16
    val xl = AppDimensions.spacing_24
    val xxl = AppDimensions.spacing_32
}
