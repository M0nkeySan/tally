package io.github.m0nkeysan.tally.core.data.backup

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import io.github.m0nkeysan.tally.core.domain.backup.DatabaseBackup
import io.github.m0nkeysan.tally.core.domain.backup.DatabaseExporter
import io.github.m0nkeysan.tally.core.domain.backup.toData
import io.github.m0nkeysan.tally.core.domain.backup.toEntity
import io.github.m0nkeysan.tally.core.utils.getCurrentTimeMillis
import io.github.m0nkeysan.tally.database.TallyDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

/**
 * Implementation of DatabaseExporter
 * Handles exporting and importing all database data
 */
class DatabaseExporterImpl(
    private val database: TallyDatabase
) : DatabaseExporter {
    
    override suspend fun exportToBackup(): DatabaseBackup = withContext(Dispatchers.Default) {
        val playerQueries = database.playerQueries
        val tarotQueries = database.tarotQueries
        val yahtzeeQueries = database.yahtzeeQueries
        val counterQueries = database.counterQueries
        val preferencesQueries = database.preferencesQueries
        
        // Fetch all data from database
        val players = playerQueries.selectAllPlayers()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .first()
            .map { it.toData() }
        
        val tarotGames = tarotQueries.selectAllGames()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .first()
            .map { it.toData() }
        
        val tarotRounds = tarotQueries.selectAllRounds()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .first()
            .map { it.toData() }
        
        val yahtzeeGames = yahtzeeQueries.selectAllGames()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .first()
            .map { it.toData() }
        
        val yahtzeeScores = yahtzeeQueries.selectAllScores()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .first()
            .map { it.toData() }
        
        val counters = counterQueries.selectAllCounters()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .first()
            .map { it.toData() }
        
        val preferences = preferencesQueries.selectAllPreferences()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .first()
            .map { it.toData() }
        
        DatabaseBackup(
            version = 1,
            exportedAt = getCurrentTimeMillis(),
            players = players,
            tarotGames = tarotGames,
            tarotRounds = tarotRounds,
            yahtzeeGames = yahtzeeGames,
            yahtzeeScores = yahtzeeScores,
            counters = counters,
            preferences = preferences
        )
    }
    
    override suspend fun importFromBackup(backup: DatabaseBackup) = withContext(Dispatchers.Default) {
        database.transaction {
            // Clear all existing data
            database.playerQueries.deleteAllPlayers()
            database.tarotQueries.deleteAllGames()
            database.tarotQueries.deleteAllRounds()
            database.yahtzeeQueries.deleteAllGames()
            database.yahtzeeQueries.deleteAllScores()
            database.counterQueries.deleteAllCounters()
            database.preferencesQueries.deleteAllPreferences()
            
            // Import new data
            backup.players.forEach { player ->
                val entity = player.toEntity()
                database.playerQueries.insertPlayer(
                    id = entity.id,
                    name = entity.name,
                    avatarColor = entity.avatarColor,
                    createdAt = entity.createdAt,
                    isActive = entity.isActive,
                    deactivatedAt = entity.deactivatedAt
                )
            }
            
            backup.tarotGames.forEach { game ->
                database.tarotQueries.insertGame(game.toEntity())
            }
            
            backup.tarotRounds.forEach { round ->
                database.tarotQueries.insertRound(round.toEntity())
            }
            
            backup.yahtzeeGames.forEach { game ->
                database.yahtzeeQueries.insertGame(game.toEntity())
            }
            
            backup.yahtzeeScores.forEach { score ->
                database.yahtzeeQueries.insertScore(score.toEntity())
            }
            
            backup.counters.forEach { counter ->
                val entity = counter.toEntity()
                database.counterQueries.insertCounter(entity)
            }
            
            backup.preferences.forEach { pref ->
                val entity = pref.toEntity()
                database.preferencesQueries.insertPreference(
                    key = entity.key,
                    prefValue = entity.prefValue
                )
            }
        }
    }
}
