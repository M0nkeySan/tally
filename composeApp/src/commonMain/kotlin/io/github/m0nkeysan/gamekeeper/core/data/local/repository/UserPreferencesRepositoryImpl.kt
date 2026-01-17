package io.github.m0nkeysan.gamekeeper.core.data.local.repository

import io.github.m0nkeysan.gamekeeper.core.data.local.database.UserPreferencesDao
import io.github.m0nkeysan.gamekeeper.core.data.local.database.UserPreferencesEntity
import io.github.m0nkeysan.gamekeeper.core.domain.model.AppLocale
import io.github.m0nkeysan.gamekeeper.core.domain.model.AppTheme
import io.github.m0nkeysan.gamekeeper.core.domain.repository.UserPreferencesRepository
import io.github.m0nkeysan.gamekeeper.core.model.DiceConfiguration
import io.github.m0nkeysan.gamekeeper.core.model.DiceType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreferencesRepositoryImpl(
    private val dao: UserPreferencesDao
) : UserPreferencesRepository {

    companion object {
        private const val KEY_CARD_ORDER = "home_card_order"
        private const val KEY_DICE_CONFIG = "dice_configuration"
        private const val KEY_LOCALE = "app_locale"
        private const val KEY_THEME = "app_theme"
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
    
    override fun getDiceConfiguration(): Flow<DiceConfiguration> {
        return dao.getValue(KEY_DICE_CONFIG).map { json ->
            if (json.isNullOrBlank()) {
                DiceConfiguration()
            } else {
                parseDiceConfiguration(json)
            }
        }
    }
    
    override suspend fun saveDiceConfiguration(config: DiceConfiguration) {
        dao.setValue(
            UserPreferencesEntity(
                key = KEY_DICE_CONFIG,
                value = serializeDiceConfiguration(config)
            )
        )
    }
    
    private fun serializeDiceConfiguration(config: DiceConfiguration): String {
        val diceTypeStr = when (config.diceType) {
            DiceType.D4 -> "D4"
            DiceType.D6 -> "D6"
            DiceType.D8 -> "D8"
            DiceType.D10 -> "D10"
            DiceType.D12 -> "D12"
            DiceType.D20 -> "D20"
            is DiceType.Custom -> "CUSTOM:${config.diceType.customSides}"
        }
        return "{numberOfDice:${config.numberOfDice},diceType:$diceTypeStr,animation:${config.animationEnabled},shake:${config.shakeEnabled}}"
    }
    
     private fun parseDiceConfiguration(json: String): DiceConfiguration {
          return try {
              val cleaned = json.removeSurrounding("{", "}")
              val parts = cleaned.split(",").associate { part ->
                  val colonIndex = part.indexOf(":")
                  if (colonIndex == -1) {
                      Pair(part, "")
                  } else {
                      Pair(part.take(colonIndex), part.substring(colonIndex + 1))
                  }
              }
             
             val numberOfDice = parts["numberOfDice"]?.toIntOrNull() ?: 1
             val diceType = when {
                 parts["diceType"]?.startsWith("CUSTOM:") == true -> {
                     val sides = parts["diceType"]?.removePrefix("CUSTOM:")?.toIntOrNull() ?: 6
                     DiceType.Custom(sides)
                 }
                 else -> DiceType.fromSides(when (parts["diceType"]) {
                     "D4" -> 4
                     "D6" -> 6
                     "D8" -> 8
                     "D10" -> 10
                     "D12" -> 12
                     "D20" -> 20
                     else -> 6
                 })
             }
             val animationEnabled = parts["animation"]?.toBoolean() ?: true
             val shakeEnabled = parts["shake"]?.toBoolean() ?: false
             
             DiceConfiguration(
                 numberOfDice = numberOfDice,
                 diceType = diceType,
                 animationEnabled = animationEnabled,
                 shakeEnabled = shakeEnabled
             )
         } catch (e: Exception) {
             DiceConfiguration()
         }
     }

    override fun getLocale(): Flow<AppLocale> =
        getString(KEY_LOCALE, AppLocale.SYSTEM_DEFAULT.code)
            .map { AppLocale.fromCode(it) }

    override suspend fun saveLocale(locale: AppLocale) {
        saveString(KEY_LOCALE, locale.code)
    }

    override fun getTheme(): Flow<AppTheme> =
        getString(KEY_THEME, AppTheme.SYSTEM_DEFAULT.code)
            .map { AppTheme.fromCode(it) }

    override suspend fun saveTheme(theme: AppTheme) {
        saveString(KEY_THEME, theme.code)
    }
}
