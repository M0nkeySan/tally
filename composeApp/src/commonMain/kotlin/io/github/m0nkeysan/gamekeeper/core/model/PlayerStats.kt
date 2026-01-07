package io.github.m0nkeysan.gamekeeper.core.model

data class PlayerStats(
    val playerId: String,
    val playerName: String,
    val avatarColor: String = "#FF6200",
    val totalGamesPlayed: Int = 0,
    val tarotGamesPlayed: Int = 0,
    val tarotGamesWon: Int = 0,
    val tarotTotalScore: Int = 0,
    val yahtzeeGamesPlayed: Int = 0,
    val yahtzeeGamesWon: Int = 0,
    val yahtzeeHighestScore: Int = 0,
    val yahtzeeTotalScore: Int = 0,
    val counterGamesPlayed: Int = 0,
    val counterGamesWon: Int = 0,
    val counterTotalScore: Int = 0,
    val lastPlayedAt: Long = 0
) {
    val tarotWinRate: Float
        get() = if (tarotGamesPlayed > 0) tarotGamesWon.toFloat() / tarotGamesPlayed else 0f

    val yahtzeeWinRate: Float
        get() = if (yahtzeeGamesPlayed > 0) yahtzeeGamesWon.toFloat() / yahtzeeGamesPlayed else 0f

    val counterWinRate: Float
        get() = if (counterGamesPlayed > 0) counterGamesWon.toFloat() / counterGamesPlayed else 0f

    val yahtzeeAverageScore: Float
        get() = if (yahtzeeGamesPlayed > 0) yahtzeeTotalScore.toFloat() / yahtzeeGamesPlayed else 0f
}

enum class GameType(val displayName: String) {
    TAROT("Tarot"),
    YAHTZEE("Yahtzee"),
    COUNTER("Counter")
}
