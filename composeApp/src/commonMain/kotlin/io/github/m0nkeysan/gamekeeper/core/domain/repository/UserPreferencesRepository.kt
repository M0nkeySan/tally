package io.github.m0nkeysan.gamekeeper.core.domain.repository

import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    fun getCardOrder(): Flow<List<String>?>
    suspend fun saveCardOrder(order: List<String>)
}
