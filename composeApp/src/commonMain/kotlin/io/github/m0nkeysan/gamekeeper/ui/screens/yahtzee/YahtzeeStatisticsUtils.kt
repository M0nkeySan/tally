package io.github.m0nkeysan.gamekeeper.ui.screens.yahtzee

import androidx.compose.ui.graphics.Color
import io.github.m0nkeysan.gamekeeper.core.model.CategoryStat
import io.github.m0nkeysan.gamekeeper.core.model.YahtzeeCategory
import io.github.m0nkeysan.gamekeeper.ui.theme.GameColors

/**
 * Determine color for a category based on performance
 */
fun getCategoryColor(stat: CategoryStat): Color {
    val category = stat.category
    val avg = stat.average
    return getCategoryColorByAverage(category, avg)
}

/**
 * Determine color for a category based on average score
 */
fun getCategoryColor(average: Double): Color {
    // For global stats, use generic scoring thresholds
    return when {
        average >= 20 -> GameColors.Success
        average >= 10 -> GameColors.Warning
        else -> GameColors.Error
    }
}

/**
 * Internal helper to calculate category color
 */
private fun getCategoryColorByAverage(category: YahtzeeCategory, avg: Double): Color {
    return when {
        // Upper section categories (max 30 for sixes)
        category.isUpperSection() -> when {
            avg >= 20 -> GameColors.Success           // 67%+ of max
            avg >= 10 -> GameColors.Warning           // 33%+ of max
            else -> GameColors.Error                  // Poor
        }
        
        // Yahtzee (max 50)
        category == YahtzeeCategory.YAHTZEE -> when {
            avg >= 40 -> GameColors.Success           // 80%+ of max
            avg >= 20 -> GameColors.Warning           // 40%+ of max
            else -> GameColors.Error                  // Poor
        }
        
        // Full House, Small Straight, Large Straight
        category in listOf(
            YahtzeeCategory.FULL_HOUSE,
            YahtzeeCategory.SMALL_STRAIGHT,
            YahtzeeCategory.LARGE_STRAIGHT
        ) -> when {
            avg >= 25 -> GameColors.Success
            avg >= 10 -> GameColors.Warning
            else -> GameColors.Error
        }
        
        // Three of a Kind, Four of a Kind, Chance
        else -> when {
            avg >= 20 -> GameColors.Success
            avg >= 10 -> GameColors.Warning
            else -> GameColors.Error
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
