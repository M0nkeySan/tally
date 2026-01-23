package io.github.m0nkeysan.gamekeeper.ui.screens.yahtzee

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import io.github.m0nkeysan.gamekeeper.core.model.CategoryStat
import io.github.m0nkeysan.gamekeeper.core.model.YahtzeeCategory
import io.github.m0nkeysan.gamekeeper.ui.theme.LocalCustomColors

/**
 * Determine color for a category based on performance
 */
@Composable
fun getCategoryColor(stat: CategoryStat): Color {
    val category = stat.category
    val avg = stat.average
    return getCategoryColorByAverage(category, avg)
}

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
 * Internal helper to calculate category color
 */
@Composable
private fun getCategoryColorByAverage(category: YahtzeeCategory, avg: Double): Color {
    val customColors = LocalCustomColors.current
    val colorScheme = MaterialTheme.colorScheme
    return when {
        // Upper section categories (max 30 for sixes)
        category.isUpperSection() -> when {
            avg >= 20 -> customColors.success           // 67%+ of max
            avg >= 10 -> customColors.warning           // 33%+ of max
            else -> colorScheme.error                  // Poor
        }
        
        // Yahtzee (max 50)
        category == YahtzeeCategory.YAHTZEE -> when {
            avg >= 40 -> customColors.success           // 80%+ of max
            avg >= 20 -> customColors.warning           // 40%+ of max
            else -> colorScheme.error                  // Poor
        }
        
        // Full House, Small Straight, Large Straight
        category in listOf(
            YahtzeeCategory.FULL_HOUSE,
            YahtzeeCategory.SMALL_STRAIGHT,
            YahtzeeCategory.LARGE_STRAIGHT
        ) -> when {
            avg >= 25 -> customColors.success
            avg >= 10 -> customColors.warning
            else -> colorScheme.error
        }
        
        // Three of a Kind, Four of a Kind, Chance
        else -> when {
            avg >= 20 -> customColors.success
            avg >= 10 -> customColors.warning
            else -> colorScheme.error
        }
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
