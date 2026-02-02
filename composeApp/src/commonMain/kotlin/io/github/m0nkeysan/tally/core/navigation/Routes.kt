package io.github.m0nkeysan.tally.core.navigation

import kotlinx.serialization.Serializable

sealed interface Route

@Serializable object HomeRoute : Route
@Serializable object FingerSelectorRoute : Route
@Serializable object TarotRoute : Route
@Serializable object TarotCreationRoute : Route
@Serializable data class TarotScoringRoute(val gameId: String): Route
@Serializable data class TarotRoundAdditionRoute(val gameId: String, val roundId: String? = null): Route
@Serializable data class TarotStatisticsRoute(val gameId: String): Route
@Serializable object YahtzeeRoute : Route
@Serializable object YahtzeeCreationRoute : Route
@Serializable data class YahtzeeScoringRoute(val gameId: String): Route
@Serializable data class YahtzeeSummaryRoute(val gameId: String): Route
@Serializable object YahtzeeStatisticsRoute : Route
@Serializable object CounterRoute : Route
@Serializable data class EditCounterRoute(val id: String, val name: String, val count: Int, val color: Long): Route
@Serializable object HistoryRoute : Route
@Serializable object DiceRollerRoute : Route
@Serializable object GameTrackerRoute : Route
@Serializable object GameTrackerCreationRoute : Route
@Serializable data class GameTrackerScoringRoute(val gameId: String): Route
@Serializable data class GameTrackerRoundAdditionRoute(val gameId: String, val roundNumber: Int, val roundId: String? = null): Route
@Serializable data class GameTrackerSummaryRoute(val gameId: String): Route
@Serializable object GameTrackerHistoryRoute : Route
@Serializable object GameTrackerStatisticsRoute : Route
@Serializable data class GameTrackerGameStatisticsRoute(val gameId: String): Route
@Serializable object HomeCustomizationRoute : Route
