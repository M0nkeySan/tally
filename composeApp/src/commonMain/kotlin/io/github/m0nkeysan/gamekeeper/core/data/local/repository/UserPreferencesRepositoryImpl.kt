package io.github.m0nkeysan.gamekeeper.core.data.local.repository

import io.github.m0nkeysan.gamekeeper.core.data.local.database.UserPreferencesDao
import io.github.m0nkeysan.gamekeeper.core.data.local.database.UserPreferencesEntity
import io.github.m0nkeysan.gamekeeper.core.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreferencesRepositoryImpl(
    private val dao: UserPreferencesDao
) : UserPreferencesRepository {

    companion object {
        private const val KEY_CARD_ORDER = "home_card_order"
    }

    override fun getCardOrder(): Flow<List<String>?> {
        return dao.getValue(KEY_CARD_ORDER).map { value ->
            value?.split(",")?.filter { it.isNotBlank() }
        }
    }

    override suspend fun saveCardOrder(order: List<String>) {
        dao.setValue(
            UserPreferencesEntity(
                key = KEY_CARD_ORDER,
                value = order.joinToString(",")
            )
        )
    }

    override fun getString(key: String, defaultValue: String): Flow<String> {
        return dao.getValue(key).map { it ?: defaultValue }
    }

    override suspend fun saveString(key: String, value: String) {
        dao.setValue(UserPreferencesEntity(key, value))
    }
}
