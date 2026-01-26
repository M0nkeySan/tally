package io.github.m0nkeysan.tally.core.data.web.repository

import io.github.m0nkeysan.tally.core.data.web.WebStorageManager
import io.github.m0nkeysan.tally.core.domain.model.AppLocale
import io.github.m0nkeysan.tally.core.domain.model.AppTheme
import io.github.m0nkeysan.tally.core.domain.repository.UserPreferencesRepository
import io.github.m0nkeysan.tally.core.model.DiceConfiguration
import io.github.m0nkeysan.tally.core.model.DiceType
import io.github.m0nkeysan.tally.platform.getSystemLocaleCode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

/**
 * Web implementation of UserPreferencesRepository using localStorage.
 * Stores preferences as individual key-value pairs.
 */
class UserPreferencesRepositoryWebImpl(
    private val storageManager: WebStorageManager
) : UserPreferencesRepository {

    companion object {
        private const val KEY_CARD_ORDER = "tally_home_card_order"
        private const val KEY_DICE_CONFIG = "tally_dice_configuration"
        private const val KEY_LOCALE = "tally_app_locale"
        private const val KEY_THEME = "tally_app_theme"
    }

    // State flows for reactive updates
    private val cardOrderFlow = MutableStateFlow<List<String>?>(loadCardOrder())
    private val diceConfigFlow = MutableStateFlow(loadDiceConfiguration())
    private val localeFlow = MutableStateFlow(loadLocale())
    private val themeFlow = MutableStateFlow(loadTheme())

    // Generic string storage for custom keys
    private val stringFlows = mutableMapOf<String, MutableStateFlow<String>>()

    override fun getCardOrder(): Flow<List<String>?> = cardOrderFlow.asStateFlow()

    override suspend fun saveCardOrder(order: List<String>) {
        cardOrderFlow.value = order
        storageManager.save(KEY_CARD_ORDER, order.joinToString(","))
    }

    override fun getString(key: String, defaultValue: String): Flow<String> {
        val fullKey = "tally_$key"
        val flow = stringFlows.getOrPut(fullKey) {
            MutableStateFlow(storageManager.load<String>(fullKey) ?: defaultValue)
        }
        return flow.asStateFlow()
    }

    override suspend fun saveString(key: String, value: String) {
        val fullKey = "tally_$key"
        val flow = stringFlows.getOrPut(fullKey) { MutableStateFlow(value) }
        flow.value = value
        storageManager.save(fullKey, value)
    }

    override fun getDiceConfiguration(): Flow<DiceConfiguration> = diceConfigFlow.asStateFlow()

    override suspend fun saveDiceConfiguration(config: DiceConfiguration) {
        diceConfigFlow.value = config
        storageManager.save(KEY_DICE_CONFIG, serializeDiceConfiguration(config))
    }

    override fun getLocale(): Flow<AppLocale> = localeFlow.asStateFlow()

    override suspend fun saveLocale(locale: AppLocale) {
        localeFlow.value = locale
        storageManager.save(KEY_LOCALE, locale.code)
    }

    override fun getTheme(): Flow<AppTheme> = themeFlow.asStateFlow()

    override suspend fun saveTheme(theme: AppTheme) {
        themeFlow.value = theme
        storageManager.save(KEY_THEME, theme.code)
    }

    // Private helper functions

    private fun loadCardOrder(): List<String>? {
        val value = storageManager.load<String>(KEY_CARD_ORDER)
        return value?.split(",")?.filter { it.isNotBlank() }
    }

    private fun loadDiceConfiguration(): DiceConfiguration {
        val json = storageManager.load<String>(KEY_DICE_CONFIG)
        return if (json.isNullOrBlank()) {
            DiceConfiguration()
        } else {
            parseDiceConfiguration(json)
        }
    }

    private fun loadLocale(): AppLocale {
        val code = storageManager.load<String>(KEY_LOCALE) ?: getDefaultLocaleCode()
        return AppLocale.fromCode(code)
    }

    private fun loadTheme(): AppTheme {
        val code = storageManager.load<String>(KEY_THEME) ?: AppTheme.SYSTEM_DEFAULT.code
        return AppTheme.fromCode(code)
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

    private fun getDefaultLocaleCode(): String {
        val supportedLanguages = listOf("en", "fr")
        val systemLang = getSystemLocaleCode()
        return if (systemLang in supportedLanguages) systemLang else "en"
    }
}
