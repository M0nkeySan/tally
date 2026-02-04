package io.github.m0nkeysan.tally.ui.utils

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import io.github.m0nkeysan.tally.ui.theme.LocalCustomColors


/**
 * Determine color for a category based on average score
 */
@Composable
fun getCategoryColor(average: Double): Color {
    val customColors = LocalCustomColors.current
    val colorScheme = MaterialTheme.colorScheme
    // For global stats, use generic scoring thresholds
    return when {
        average >= 20 -> customColors.success
        average >= 10 -> customColors.warning
        else -> colorScheme.error
    }
}

/**
 * Format average score for display
 */
fun formatAverage(value: Double): String {
    val rounded = (value * 10).toInt() / 10.0
    val intPart = rounded.toInt()
    val decimalPart = ((rounded - intPart) * 10).toInt()
    return "$intPart.$decimalPart"
}

/**
 * Format percentage for display
 */
fun formatPercentage(value: Double): String {
    return "${value.toInt()}%"
}

/**
 * Medal emojis for top-3 rankings
 */
fun medalEmoji(rank: Int): String = when (rank) {
    1 -> "\uD83E\uDD47" // 🥇
    2 -> "\uD83E\uDD48" // 🥈
    3 -> "\uD83E\uDD49" // 🥉
    else -> ""
}
