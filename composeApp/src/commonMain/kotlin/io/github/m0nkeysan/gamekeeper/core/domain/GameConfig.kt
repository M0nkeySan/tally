package io.github.m0nkeysan.gamekeeper.core.domain

object GameConfig {
    const val YAHTZEE_MIN_PLAYERS = 1
    const val YAHTZEE_MAX_PLAYERS = 8

    const val TAROT_MIN_PLAYERS = 3
    const val TAROT_MAX_PLAYERS = 5
    
    // Deprecated aliases for backward compatibility
    @Deprecated("Use YAHTZEE_MIN_PLAYERS", replaceWith = ReplaceWith("YAHTZEE_MIN_PLAYERS"))
    const val yahtzeeMinPlayers = YAHTZEE_MIN_PLAYERS
    @Deprecated("Use YAHTZEE_MAX_PLAYERS", replaceWith = ReplaceWith("YAHTZEE_MAX_PLAYERS"))
    const val yahtzeeMaxPlayers = YAHTZEE_MAX_PLAYERS
    @Deprecated("Use TAROT_MIN_PLAYERS", replaceWith = ReplaceWith("TAROT_MIN_PLAYERS"))
    const val tarotMinPlayers = TAROT_MIN_PLAYERS
    @Deprecated("Use TAROT_MAX_PLAYERS", replaceWith = ReplaceWith("TAROT_MAX_PLAYERS"))
    const val tarotMaxPlayers = TAROT_MAX_PLAYERS
}