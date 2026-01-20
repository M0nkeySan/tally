package io.github.m0nkeysan.gamekeeper.core.model

import androidx.compose.runtime.Composable
import io.github.m0nkeysan.gamekeeper.generated.resources.Res
import io.github.m0nkeysan.gamekeeper.generated.resources.yahtzee_category_aces
import io.github.m0nkeysan.gamekeeper.generated.resources.yahtzee_category_chance
import io.github.m0nkeysan.gamekeeper.generated.resources.yahtzee_category_fives
import io.github.m0nkeysan.gamekeeper.generated.resources.yahtzee_category_fours
import io.github.m0nkeysan.gamekeeper.generated.resources.yahtzee_category_four_of_kind
import io.github.m0nkeysan.gamekeeper.generated.resources.yahtzee_category_full_house
import io.github.m0nkeysan.gamekeeper.generated.resources.yahtzee_category_large_straight
import io.github.m0nkeysan.gamekeeper.generated.resources.yahtzee_category_sixes
import io.github.m0nkeysan.gamekeeper.generated.resources.yahtzee_category_small_straight
import io.github.m0nkeysan.gamekeeper.generated.resources.yahtzee_category_threes
import io.github.m0nkeysan.gamekeeper.generated.resources.yahtzee_category_three_of_kind
import io.github.m0nkeysan.gamekeeper.generated.resources.yahtzee_category_twos
import io.github.m0nkeysan.gamekeeper.generated.resources.yahtzee_category_yahtzee
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource

@Serializable
data class YahtzeeScore(
    val category: YahtzeeCategory,
    val value: Int,
    val isScored: Boolean = false
)

/**
 * Yahtzee score associated with a specific player in a game
 */
data class PlayerYahtzeeScore(
    val playerId: String,
    val score: YahtzeeScore
)

@Serializable
enum class YahtzeeCategory {
    ACES,
    TWOS,
    THREES,
    FOURS,
    FIVES,
    SIXES,
    CHANCE,
    THREE_OF_KIND,
    FOUR_OF_KIND,
    FULL_HOUSE,
    SMALL_STRAIGHT,
    LARGE_STRAIGHT,
    YAHTZEE;

    fun isUpperSection(): Boolean {
        return this in ACES..SIXES
    }

    fun isLowerSection(): Boolean {
        return !isUpperSection()
    }
}

/**
 * Extension function to get localized category name
 */
@Composable
fun YahtzeeCategory.getLocalizedName(): String {
    return stringResource(when (this) {
        YahtzeeCategory.ACES -> Res.string.yahtzee_category_aces
        YahtzeeCategory.TWOS -> Res.string.yahtzee_category_twos
        YahtzeeCategory.THREES -> Res.string.yahtzee_category_threes
        YahtzeeCategory.FOURS -> Res.string.yahtzee_category_fours
        YahtzeeCategory.FIVES -> Res.string.yahtzee_category_fives
        YahtzeeCategory.SIXES -> Res.string.yahtzee_category_sixes
        YahtzeeCategory.CHANCE -> Res.string.yahtzee_category_chance
        YahtzeeCategory.THREE_OF_KIND -> Res.string.yahtzee_category_three_of_kind
        YahtzeeCategory.FOUR_OF_KIND -> Res.string.yahtzee_category_four_of_kind
        YahtzeeCategory.FULL_HOUSE -> Res.string.yahtzee_category_full_house
        YahtzeeCategory.SMALL_STRAIGHT -> Res.string.yahtzee_category_small_straight
        YahtzeeCategory.LARGE_STRAIGHT -> Res.string.yahtzee_category_large_straight
        YahtzeeCategory.YAHTZEE -> Res.string.yahtzee_category_yahtzee
    })
}
