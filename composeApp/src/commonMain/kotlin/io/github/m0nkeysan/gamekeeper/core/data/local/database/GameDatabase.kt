package io.github.m0nkeysan.gamekeeper.core.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase

import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.execSQL

@Database(
    entities = [
        PlayerEntity::class,
        UserPreferencesEntity::class,
        PersistentCounterEntity::class,
        TarotGameEntity::class,
        TarotRoundEntity::class,
        YahtzeeGameEntity::class,
        YahtzeeScoreEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class GameDatabase : RoomDatabase() {
    abstract fun playerDao(): PlayerDao
    abstract fun userPreferencesDao(): UserPreferencesDao

        companion object {
            val MIGRATION_1_2 = object : Migration(1, 2) {
                override fun migrate(db: SupportSQLiteDatabase) {
                    // Create temporary table with new schema
                    db.execSQL("""
                        CREATE TABLE tarot_rounds_new (
                            id TEXT PRIMARY KEY NOT NULL,
                            gameId TEXT NOT NULL,
                            roundNumber INTEGER NOT NULL,
                            takerPlayerId TEXT NOT NULL,
                            bid TEXT NOT NULL,
                            bouts INTEGER NOT NULL,
                            pointsScored INTEGER NOT NULL,
                            hasPetitAuBout INTEGER NOT NULL,
                            hasPoignee INTEGER NOT NULL,
                            poigneeLevel TEXT,
                            chelem TEXT NOT NULL,
                            calledPlayerId TEXT,
                            score INTEGER NOT NULL
                        )
                    """.trimIndent())

                    db.execSQL("CREATE INDEX index_tarot_rounds_new_gameId ON tarot_rounds_new(gameId)")

                    // Migrate data: convert indices to player IDs
                    db.execSQL("""
                        INSERT INTO tarot_rounds_new (
                            id, gameId, roundNumber, takerPlayerId, bid, bouts, 
                            pointsScored, hasPetitAuBout, hasPoignee, poigneeLevel, 
                            chelem, calledPlayerId, score
                        )
                        SELECT 
                            r.id,
                            r.gameId,
                            r.roundNumber,
                            (SELECT CASE 
                                WHEN r.takerPlayerIndex = 0 THEN substr(g.playerIds, 1, instr(g.playerIds || ',', ',') - 1)
                                WHEN r.takerPlayerIndex = 1 THEN substr(substr(g.playerIds, instr(g.playerIds, ',') + 1), 1, instr(substr(g.playerIds, instr(g.playerIds, ',') + 1) || ',', ',') - 1)
                                WHEN r.takerPlayerIndex = 2 THEN substr(substr(g.playerIds, instr(g.playerIds, ',', instr(g.playerIds, ',') + 1) + 1), 1, instr(substr(g.playerIds, instr(g.playerIds, ',', instr(g.playerIds, ',') + 1) + 1) || ',', ',') - 1)
                                WHEN r.takerPlayerIndex = 3 THEN substr(substr(g.playerIds, instr(g.playerIds, ',', instr(g.playerIds, ',', instr(g.playerIds, ',') + 1) + 1) + 1), 1, instr(substr(g.playerIds, instr(g.playerIds, ',', instr(g.playerIds, ',', instr(g.playerIds, ',') + 1) + 1) + 1) || ',', ',') - 1)
                                WHEN r.takerPlayerIndex = 4 THEN substr(g.playerIds, instr(g.playerIds, ',', instr(g.playerIds, ',', instr(g.playerIds, ',', instr(g.playerIds, ',') + 1) + 1) + 1) + 1)
                                ELSE 'unknown'
                            END FROM tarot_games g WHERE g.id = r.gameId) as takerPlayerId,
                            r.bid,
                            r.bouts,
                            r.pointsScored,
                            r.hasPetitAuBout,
                            r.hasPoignee,
                            r.poigneeLevel,
                            r.chelem,
                            CASE 
                                WHEN r.calledPlayerIndex IS NULL THEN NULL
                                WHEN r.calledPlayerIndex = 0 THEN (SELECT substr(g.playerIds, 1, instr(g.playerIds || ',', ',') - 1) FROM tarot_games g WHERE g.id = r.gameId)
                                WHEN r.calledPlayerIndex = 1 THEN (SELECT substr(substr(g.playerIds, instr(g.playerIds, ',') + 1), 1, instr(substr(g.playerIds, instr(g.playerIds, ',') + 1) || ',', ',') - 1) FROM tarot_games g WHERE g.id = r.gameId)
                                WHEN r.calledPlayerIndex = 2 THEN (SELECT substr(substr(g.playerIds, instr(g.playerIds, ',', instr(g.playerIds, ',') + 1) + 1), 1, instr(substr(g.playerIds, instr(g.playerIds, ',', instr(g.playerIds, ',') + 1) + 1) || ',', ',') - 1) FROM tarot_games g WHERE g.id = r.gameId)
                                WHEN r.calledPlayerIndex = 3 THEN (SELECT substr(substr(g.playerIds, instr(g.playerIds, ',', instr(g.playerIds, ',', instr(g.playerIds, ',') + 1) + 1) + 1), 1, instr(substr(g.playerIds, instr(g.playerIds, ',', instr(g.playerIds, ',', instr(g.playerIds, ',') + 1) + 1) + 1) || ',', ',') - 1) FROM tarot_games g WHERE g.id = r.gameId)
                                WHEN r.calledPlayerIndex = 4 THEN (SELECT substr(g.playerIds, instr(g.playerIds, ',', instr(g.playerIds, ',', instr(g.playerIds, ',', instr(g.playerIds, ',') + 1) + 1) + 1) + 1) FROM tarot_games g WHERE g.id = r.gameId)
                                ELSE NULL
                            END as calledPlayerId,
                            r.score
                        FROM tarot_rounds r
                    """.trimIndent())

                    // Drop old table and rename new table
                    db.execSQL("DROP TABLE tarot_rounds")
                    db.execSQL("ALTER TABLE tarot_rounds_new RENAME TO tarot_rounds")
                }
            }
        }
    abstract fun persistentCounterDao(): PersistentCounterDao
    abstract fun tarotDao(): TarotDao
    abstract fun yahtzeeDao(): YahtzeeDao
}
