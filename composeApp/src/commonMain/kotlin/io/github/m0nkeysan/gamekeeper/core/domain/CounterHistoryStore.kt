package io.github.m0nkeysan.gamekeeper.core.domain

import io.github.m0nkeysan.gamekeeper.core.model.CounterChange
import io.github.m0nkeysan.gamekeeper.core.model.MergedCounterChange
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * In-memory session-based store for counter history.
 * History entries persist only for the duration of the app session.
 * All entries are cleared when the app is closed.
 */
class CounterHistoryStore {
    private val _history = MutableStateFlow<List<CounterChange>>(emptyList())
    val history: StateFlow<List<CounterChange>> = _history.asStateFlow()

    /**
     * Add a change to the history.
     * Changes are stored in memory only and will be lost on app close.
     *
     * @param change The counter change to add
     */
    fun addChange(change: CounterChange) {
        _history.value += change
    }

    /**
     * Delete all changes from history.
     */
    fun deleteAllChanges() {
        _history.value = emptyList()
    }

    /**
     * Get the current list of all changes.
     * @return Unmodifiable list of all changes in reverse chronological order
     */
    fun getHistory(): List<CounterChange> = _history.value

    /**
     * Get the merged and grouped history.
     * Consecutive changes for the same counter are merged into groups.
     *
     * @return List of merged counter changes
     */
    fun getMergedHistory(): List<MergedCounterChange> {
        return mergeConsecutiveChanges(getHistory())
    }

    /**
     * Merge consecutive changes for the same counter into groups.
     *
     * @param changes The list of changes to merge
     * @return List of merged changes, grouped by consecutive counters
     */
    private fun mergeConsecutiveChanges(changes: List<CounterChange>): List<MergedCounterChange> {
        if (changes.isEmpty()) return emptyList()

        val merged = mutableListOf<MergedCounterChange>()
        var currentGroup = mutableListOf(changes[0])

        for (i in 1 until changes.size) {
            val current = changes[i]
            val previous = changes[i - 1]

            if (current.counterId == previous.counterId) {
                // Same counter, add to current group
                currentGroup.add(current)
            } else {
                // Different counter, finalize current group and start new one
                merged.add(createMergedChange(currentGroup))
                currentGroup = mutableListOf(current)
            }
        }

        // Don't forget the last group
        merged.add(createMergedChange(currentGroup))

        return merged
    }

    /**
     * Create a merged change from a group of changes.
     *
     * @param changes The list of changes to merge
     * @return A merged counter change
     */
    private fun createMergedChange(changes: List<CounterChange>): MergedCounterChange {
        val totalDelta = changes.sumOf { it.changeDelta }
        val firstTimestamp = changes.minOf { it.timestamp }
        val lastTimestamp = changes.maxOf { it.timestamp }
        val first = changes.first()
        val isDeleted = changes.any { it.isDeleted }

        return MergedCounterChange(
            counterId = first.counterId,
            counterName = first.counterName,
            counterColor = first.counterColor,
            totalDelta = totalDelta,
            count = changes.size,
            firstTimestamp = firstTimestamp,
            lastTimestamp = lastTimestamp,
            isDeleted = isDeleted,
            changes = changes
        )
    }
}
