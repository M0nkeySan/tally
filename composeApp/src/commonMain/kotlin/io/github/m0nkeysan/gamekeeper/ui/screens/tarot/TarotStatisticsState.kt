package io.github.m0nkeysan.gamekeeper.ui.screens.tarot

import io.github.m0nkeysan.gamekeeper.core.model.BidStatistic
import io.github.m0nkeysan.gamekeeper.core.model.GameHighlights
import io.github.m0nkeysan.gamekeeper.core.model.GameStatistics
import io.github.m0nkeysan.gamekeeper.core.model.PlayerMomentum
import io.github.m0nkeysan.gamekeeper.core.model.PlayerRanking
import io.github.m0nkeysan.gamekeeper.core.model.PlayerStatistics
import io.github.m0nkeysan.gamekeeper.core.model.RoundStatistic
import io.github.m0nkeysan.gamekeeper.core.model.TakerPerformance
import io.github.m0nkeysan.gamekeeper.core.model.TarotGame

/**
 * UI state for Tarot statistics screen.
 *
 * Holds all data needed for displaying game and player statistics,
 * including loading and error states.
 */
data class TarotStatisticsState(
    // Current game data
    val game: TarotGame? = null,
    
    // Current game statistics
    val gameStatistics: GameStatistics? = null,
    val roundBreakdown: List<RoundStatistic> = emptyList(),
    val currentGameRankings: List<PlayerRanking> = emptyList(),
    
    // Game progression statistics (only when 3+ rounds)
    val gameHighlights: GameHighlights? = null,
    val playerMomentum: Map<String, PlayerMomentum> = emptyMap(),
    val takerPerformance: Map<String, TakerPerformance> = emptyMap(),
    val hasMinimumRounds: Boolean = false,
    
    // Cross-game player statistics
    val playerStatistics: List<PlayerStatistics> = emptyList(),
    val bidStatistics: Map<String, List<BidStatistic>> = emptyMap(),
    
    // Loading and error states
    val isLoading: Boolean = false,
    val error: String? = null
)
