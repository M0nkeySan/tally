package io.github.m0nkeysan.gamekeeper.core.model

/**
 * Configuration for dice rolling settings
 * Persisted to user preferences
 */
data class DiceConfiguration(
    val numberOfDice: Int = 1,
    val diceType: DiceType = DiceType.D6,
    val animationEnabled: Boolean = true,
    val shakeEnabled: Boolean = false
)

/**
 * Sealed class representing different dice types
 * Supports standard RPG dice (d4-d20) and custom sides
 */
sealed class DiceType(val sides: Int, val displayName: String) {
    object D4 : DiceType(4, "d4")
    object D6 : DiceType(6, "d6")
    object D8 : DiceType(8, "d8")
    object D10 : DiceType(10, "d10")
    object D12 : DiceType(12, "d12")
    object D20 : DiceType(20, "d20")
    data class Custom(val customSides: Int) : DiceType(customSides, "d$customSides")
    
    companion object {
        fun fromSides(sides: Int): DiceType = when (sides) {
            4 -> D4
            6 -> D6
            8 -> D8
            10 -> D10
            12 -> D12
            20 -> D20
            else -> Custom(sides)
        }
    }
}

/**
 * Represents a single dice roll result
 * Contains individual results and total sum
 */
data class DiceRoll(
    val individualResults: List<Int>,
    val total: Int
)
