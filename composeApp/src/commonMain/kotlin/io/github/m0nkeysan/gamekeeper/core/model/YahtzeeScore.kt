package io.github.m0nkeysan.gamekeeper.core.model

import kotlinx.serialization.Serializable

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
enum class YahtzeeCategory(val displayName: String) {
    ACES("Ones"),
    TWOS("Twos"),
    THREES("Threes"),
    FOURS("Fours"),
    FIVES("Fives"),
    SIXES("Sixes"),
    CHANCE("Chance (total of 5 dices)"),
    THREE_OF_KIND("Three of a Kind"),
    FOUR_OF_KIND("Four of a Kind"),
    FULL_HOUSE("Full House"),
    SMALL_STRAIGHT("Small Straight"),
    LARGE_STRAIGHT("Large Straight"),
    YAHTZEE("Yahtzee");

    fun isUpperSection(): Boolean {
        return this in ACES..SIXES
    }

    fun isLowerSection(): Boolean {
        return !isUpperSection()
    }
}
