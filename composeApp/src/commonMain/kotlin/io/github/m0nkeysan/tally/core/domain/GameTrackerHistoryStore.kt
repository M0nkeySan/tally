package io.github.m0nkeysan.tally.core.domain

import io.github.m0nkeysan.tally.core.model.GameTrackerScoreChange
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * In-memory session-based store for GameTracker score history.
 * History entries persist only for the duration of the app session.
 * All entries are cleared when the app is closed.
 */
class GameTrackerHistoryStore {
    private val _history = MutableStateFlow<List<GameTrackerScoreChange>>(emptyList())
    val history: StateFlow<List<GameTrackerScoreChange>> = _history.asStateFlow()

    /**
     * Add a score change to the history.
     * Only adds if score is non-zero.
     *
     * @param change The score change to add
     */
    fun addChange(change: GameTrackerScoreChange) {
        if (change.score == 0) return // Don't track zero scores
        _history.value += change
    }

    /**
     * Add multiple score changes at once.
     * Filters out zero scores automatically.
     *
     * @param changes The list of score changes to add
     */
    fun addChanges(changes: List<GameTrackerScoreChange>) {
        val nonZeroChanges = changes.filter { it.score != 0 }
        _history.value += nonZeroChanges
    }

    /**
     * Remove score entries for a specific round.
     * Called when a round is deleted.
     *
     * @param gameId The game ID
     * @param roundNumber The round number to remove
     */
    fun removeRound(gameId: String, roundNumber: Int) {
        _history.value = _history.value.filter { 
            !(it.gameId == gameId && it.roundNumber == roundNumber)
        }
    }

    /**
     * Replace score entries for a specific round.
     * Called when a round is edited.
     * First removes old entries, then adds new ones.
     *
     * @param gameId The game ID
     * @param roundNumber The round number to replace
     * @param newChanges The new score changes for this round
     */
    fun replaceRound(gameId: String, roundNumber: Int, newChanges: List<GameTrackerScoreChange>) {
        // Remove old entries for this round
        removeRound(gameId, roundNumber)
        // Add new entries (filtering zeros)
        addChanges(newChanges)
    }

    /**
     * Delete all changes from history.
     */
    fun deleteAllChanges() {
        _history.value = emptyList()
    }

    /**
     * Get the current list of all changes.
     * @return Unmodifiable list of all changes in reverse chronological order (newest first)
     */
    fun getHistory(): List<GameTrackerScoreChange> = _history.value.reversed()
}
