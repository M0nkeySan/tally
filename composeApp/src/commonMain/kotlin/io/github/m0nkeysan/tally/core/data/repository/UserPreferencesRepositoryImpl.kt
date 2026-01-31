package io.github.m0nkeysan.tally.core.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOneOrNull
import io.github.m0nkeysan.tally.core.domain.model.AppLocale
import io.github.m0nkeysan.tally.core.domain.model.AppTheme
import io.github.m0nkeysan.tally.core.domain.model.HomeFeatureState
import io.github.m0nkeysan.tally.core.domain.model.defaultFeatureStates
import io.github.m0nkeysan.tally.core.domain.repository.UserPreferencesRepository
import io.github.m0nkeysan.tally.core.model.DiceConfiguration
import io.github.m0nkeysan.tally.core.model.DiceType
import io.github.m0nkeysan.tally.database.PreferencesQueries
import io.github.m0nkeysan.tally.platform.getSystemLocaleCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreferencesRepositoryImpl(
    private val preferencesQueries: PreferencesQueries
) : UserPreferencesRepository {

    companion object {
        private const val KEY_HOME_FEATURES = "home_features_state"
        private const val KEY_DICE_CONFIG = "dice_configuration"
        private const val KEY_LOCALE = "app_locale"
        private const val KEY_THEME = "app_theme"
    }

    override fun getHomeFeatureStates(): Flow<List<HomeFeatureState>> {
        return preferencesQueries.selectPreference(KEY_HOME_FEATURES)
            .asFlow()
            .mapToOneOrNull(Dispatchers.Default)
            .map { json ->
                json?.let { parseHomeFeatureStates(it) } ?: defaultFeatureStates
            }
    }

    override suspend fun saveHomeFeatureStates(states: List<HomeFeatureState>) {
        preferencesQueries.insertPreference(
            key = KEY_HOME_FEATURES,
            prefValue = serializeHomeFeatureStates(states)
        )
    }

    private fun serializeHomeFeatureStates(states: List<HomeFeatureState>): String {
        return states.joinToString(",") { "${it.featureId}:${it.enabled}:${it.order}" }
    }

    private fun parseHomeFeatureStates(json: String): List<HomeFeatureState> {
        return try {
            json.split(",").mapNotNull { entry ->
                val parts = entry.split(":")
                if (parts.size == 3) {
                    HomeFeatureState(
                        featureId = parts[0],
                        enabled = parts[1].toBoolean(),
                        order = parts[2].toInt()
                    )
                } else null
            }.sortedBy { it.order }
        } catch (_: Exception) {
            defaultFeatureStates
        }
    }

    override fun getString(key: String, defaultValue: String): Flow<String> {
        return preferencesQueries.selectPreference(key)
            .asFlow()
            .mapToOneOrNull(Dispatchers.Default)
            .map { it ?: defaultValue }
    }

    override suspend fun saveString(key: String, value: String) {
        preferencesQueries.insertPreference(key, value)
    }

    override fun getDiceConfiguration(): Flow<DiceConfiguration> {
        return preferencesQueries.selectPreference(KEY_DICE_CONFIG)
            .asFlow()
            .mapToOneOrNull(Dispatchers.Default)
            .map { json ->
                if (json.isNullOrBlank()) {
                    DiceConfiguration()
                } else {
                    parseDiceConfiguration(json)
                }
            }
    }

    override suspend fun saveDiceConfiguration(config: DiceConfiguration) {
        preferencesQueries.insertPreference(
            key = KEY_DICE_CONFIG,
            prefValue = serializeDiceConfiguration(config)
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

                else -> DiceType.fromSides(
                    when (parts["diceType"]) {
                        "D4" -> 4
                        "D6" -> 6
                        "D8" -> 8
                        "D10" -> 10
                        "D12" -> 12
                        "D20" -> 20
                        else -> 6
                    }
                )
            }
            val animationEnabled = parts["animation"]?.toBoolean() ?: true
            val shakeEnabled = parts["shake"]?.toBoolean() ?: false

            DiceConfiguration(
                numberOfDice = numberOfDice,
                diceType = diceType,
                animationEnabled = animationEnabled,
                shakeEnabled = shakeEnabled
            )
        } catch (_: Exception) {
            DiceConfiguration()
        }
    }

    override fun getLocale(): Flow<AppLocale> =
        getString(KEY_LOCALE, getDefaultLocaleCode())
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

    private fun getDefaultLocaleCode(): String {
        val supportedLanguages = listOf("en", "fr")
        val systemLang = getSystemLocaleCode()
        return if (systemLang in supportedLanguages) systemLang else "en"
    }
}
