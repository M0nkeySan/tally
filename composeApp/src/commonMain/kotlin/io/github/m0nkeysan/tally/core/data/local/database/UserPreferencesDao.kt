package io.github.m0nkeysan.tally.core.data.local.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserPreferencesDao {
    @Query("SELECT value FROM user_preferences WHERE `key` = :key")
    fun getValue(key: String): Flow<String?>

    @Query("SELECT value FROM user_preferences WHERE `key` = :key")
    suspend fun getValueOnce(key: String): String?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setValue(preference: UserPreferencesEntity)

    @Query("DELETE FROM user_preferences WHERE `key` = :key")
    suspend fun deleteValue(key: String)
}
