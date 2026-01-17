package io.github.m0nkeysan.gamekeeper.core.domain.repository

import io.github.m0nkeysan.gamekeeper.core.domain.model.AppLocale
import io.github.m0nkeysan.gamekeeper.core.domain.model.AppTheme
import io.github.m0nkeysan.gamekeeper.core.model.DiceConfiguration
import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    fun getCardOrder(): Flow<List<String>?>
    suspend fun saveCardOrder(order: List<String>)

    fun getString(key: String, defaultValue: String): Flow<String>
    suspend fun saveString(key: String, value: String)
    
    fun getDiceConfiguration(): Flow<DiceConfiguration>
    suspend fun saveDiceConfiguration(config: DiceConfiguration)

    fun getLocale(): Flow<AppLocale>
    suspend fun saveLocale(locale: AppLocale)

    fun getTheme(): Flow<AppTheme>
    suspend fun saveTheme(theme: AppTheme)
}
