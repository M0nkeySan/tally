package io.github.m0nkeysan.gamekeeper.core.model

data class MergedCounterChange(
    val counterId: String,
    val counterName: String,
    val counterColor: Long,
    val totalDelta: Int,              // Sum of all consecutive deltas
    val count: Int,                   // How many changes were merged
    val firstTimestamp: Long,         // Earliest change in group
    val lastTimestamp: Long,          // Latest change in group
    val isDeleted: Boolean = false,   // True if this is a deletion entry
    val changes: List<CounterChange>  // Original changes for reference
)
