package io.github.m0nkeysan.tally.ui.screens.yahtzee

import io.github.m0nkeysan.tally.core.model.YahtzeeGlobalStatistics
import io.github.m0nkeysan.tally.core.model.YahtzeePlayerStatistics
import io.github.m0nkeysan.tally.core.utils.getCurrentTimeMillis
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * In-memory cache for Yahtzee statistics with 5-minute TTL (Time To Live).
 * Reduces database load by caching computation-heavy statistics results.
 * Thread-safe using Mutex for concurrent access.
 */
class YahtzeeStatisticsCache(
    private val cacheDurationMs: Long = 5 * 60 * 1000 // 5 minutes
) {
    private data class CachedItem<T>(
        val data: T,
        val timestamp: Long
    )
    
    private val mutex = Mutex()
    private var globalStats: CachedItem<YahtzeeGlobalStatistics>? = null
    private val playerStatsCache = mutableMapOf<String, CachedItem<YahtzeePlayerStatistics>>()
    
    /**
     * Get global statistics from cache if valid, null if expired or not cached
     */
    suspend fun getGlobalStatistics(): YahtzeeGlobalStatistics? = mutex.withLock {
        globalStats?.let { cached ->
            if (isValid(cached.timestamp)) {
                cached.data
            } else {
                globalStats = null
                null
            }
        }
    }
    
    /**
     * Cache global statistics with current timestamp
     */
    suspend fun putGlobalStatistics(stats: YahtzeeGlobalStatistics) = mutex.withLock {
        globalStats = CachedItem(stats, getCurrentTimeMillis())
    }
    
    /**
     * Get player statistics from cache if valid, null if expired or not cached
     */
    suspend fun getPlayerStatistics(playerId: String): YahtzeePlayerStatistics? = mutex.withLock {
        playerStatsCache[playerId]?.let { cached ->
            if (isValid(cached.timestamp)) {
                cached.data
            } else {
                playerStatsCache.remove(playerId)
                null
            }
        }
    }
    
    /**
     * Cache player statistics with current timestamp
     */
    suspend fun putPlayerStatistics(playerId: String, stats: YahtzeePlayerStatistics) = mutex.withLock {
        playerStatsCache[playerId] = CachedItem(stats, getCurrentTimeMillis())
    }
    
    /**
     * Invalidate all cached data.
     * Call when a game is finished or scores are updated.
     */
    suspend fun invalidateAll() = mutex.withLock {
        globalStats = null
        playerStatsCache.clear()
    }
    
    private fun isValid(timestamp: Long): Boolean {
        return (getCurrentTimeMillis() - timestamp) < cacheDurationMs
    }
}
