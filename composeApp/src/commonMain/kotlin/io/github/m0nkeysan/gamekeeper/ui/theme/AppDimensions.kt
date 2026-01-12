package io.github.m0nkeysan.gamekeeper.ui.theme

/**
 * Game configuration constants - player counts for game creation validation.
 */
object GameConfig {
    // Yahtzee game constraints
    const val yahtzeeMinPlayers = 2                     // Minimum players for a game
    const val yahtzeeMaxPlayers = 8                     // Maximum players for a game

    // Tarot game constraints
    const val tarotMinPlayers = 3                       // Minimum players for a game
    const val tarotMaxPlayers = 5                       // Maximum players for a game
}

/**
 * Backward compatibility alias for AppDimensions.
 * @deprecated Use GameConfig instead
 */
@Deprecated("Use GameConfig instead", ReplaceWith("GameConfig"))
object AppDimensions {
    const val yahtzeeMinPlayers = GameConfig.yahtzeeMinPlayers
    const val yahtzeeMaxPlayers = GameConfig.yahtzeeMaxPlayers
    const val tarotMinPlayers = GameConfig.tarotMinPlayers
    const val tarotMaxPlayers = GameConfig.tarotMaxPlayers
}
