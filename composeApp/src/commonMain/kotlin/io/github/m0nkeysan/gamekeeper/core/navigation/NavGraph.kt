package io.github.m0nkeysan.gamekeeper.core.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import io.github.m0nkeysan.gamekeeper.ui.screens.common.HomeNavigationTemplate
import io.github.m0nkeysan.gamekeeper.ui.screens.counter.CounterHistoryScreen
import io.github.m0nkeysan.gamekeeper.ui.screens.counter.CounterScreen
import io.github.m0nkeysan.gamekeeper.ui.screens.counter.CounterViewModel
import io.github.m0nkeysan.gamekeeper.ui.screens.counter.EditCounterScreen
import io.github.m0nkeysan.gamekeeper.ui.screens.dice.DiceRollerScreen
import io.github.m0nkeysan.gamekeeper.ui.screens.fingerselector.FingerSelectorScreen
import io.github.m0nkeysan.gamekeeper.ui.screens.player.PlayerSelectionScreen
import io.github.m0nkeysan.gamekeeper.ui.screens.settings.SettingsScreen
import io.github.m0nkeysan.gamekeeper.ui.screens.settings.SettingsViewModel
import io.github.m0nkeysan.gamekeeper.ui.screens.tarot.TarotGameCreationScreen
import io.github.m0nkeysan.gamekeeper.ui.screens.tarot.TarotGameSelectionScreen
import io.github.m0nkeysan.gamekeeper.ui.screens.tarot.TarotRoundAdditionScreen
import io.github.m0nkeysan.gamekeeper.ui.screens.tarot.TarotScoringScreen
import io.github.m0nkeysan.gamekeeper.ui.screens.tarot.TarotStatisticsScreen
import io.github.m0nkeysan.gamekeeper.ui.screens.yahtzee.YahtzeeGameCreationScreen
import io.github.m0nkeysan.gamekeeper.ui.screens.yahtzee.YahtzeeGameSelectionScreen
import io.github.m0nkeysan.gamekeeper.ui.screens.yahtzee.YahtzeeScoringScreen
import io.github.m0nkeysan.gamekeeper.ui.screens.yahtzee.YahtzeeStatisticsScreen
import io.github.m0nkeysan.gamekeeper.ui.screens.yahtzee.YahtzeeSummaryScreen

@Composable
fun GameNavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = HomeRoute
    ) {
        composable<HomeRoute> {
            HomeNavigationTemplate(onNavigateTo = { route -> navController.navigate(route)  })
        }

        composable<FingerSelectorRoute> {
            FingerSelectorScreen(onBack = { navController.popBackStack() })
        }

        // --- Tarot Section ---
        composable<TarotRoute> {
            TarotGameSelectionScreen(
                onBack = { navController.popBackStack() },
                onCreateNewGame = { navController.navigate(TarotCreationRoute) },
                onSelectGame = { gameId ->
                    navController.navigate(TarotScoringRoute(gameId))
                }
            )
        }

        composable<TarotCreationRoute> {
            TarotGameCreationScreen(
                onBack = { navController.popBackStack() },
                onGameCreated = { gameId ->
                    navController.navigate(TarotScoringRoute(gameId)) {
                        popUpTo(TarotRoute)
                    }
                }
            )
        }

        composable<TarotScoringRoute> { entry ->
            val route = entry.toRoute<TarotScoringRoute>()

            TarotScoringScreen(
                gameId = route.gameId,
                onBack = { navController.popBackStack() },
                onAddNewRound = { roundId ->
                    navController.navigate(TarotRoundAdditionRoute(route.gameId, roundId))
                },
                onNavigateToStatistics = { statsGameId ->
                    navController.navigate(TarotStatisticsRoute(statsGameId))
                }
            )
        }

        composable<TarotRoundAdditionRoute> { entry ->
            val route = entry.toRoute<TarotRoundAdditionRoute>()

            TarotRoundAdditionScreen(
                gameId = route.gameId,
                roundId = route.roundId,
                onBack = { navController.popBackStack() },
                onRoundAdded = { navController.popBackStack() }
            )
        }

        composable<TarotStatisticsRoute> { entry ->
            val route = entry.toRoute<TarotStatisticsRoute>()
            TarotStatisticsScreen(
                gameId = route.gameId,
                onBack = { navController.popBackStack() }
            )
        }

        // --- Yahtzee Section ---
        composable<YahtzeeRoute> {
            YahtzeeGameSelectionScreen(
                onBack = { navController.popBackStack() },
                onCreateNewGame = { navController.navigate(YahtzeeCreationRoute) },
                onSelectGame = { gameId ->
                    navController.navigate(YahtzeeScoringRoute(gameId))
                },
                onNavigateToStatistics = {
                    navController.navigate(YahtzeeStatisticsRoute)
                }
            )
        }

        composable<YahtzeeCreationRoute> {
            YahtzeeGameCreationScreen(
                onBack = { navController.popBackStack() },
                onGameCreated = { gameId ->
                    navController.navigate(YahtzeeScoringRoute(gameId)) {
                        popUpTo(YahtzeeRoute)
                    }
                }
            )
        }

        composable<YahtzeeScoringRoute> { entry ->
            val route = entry.toRoute<YahtzeeScoringRoute>()
            YahtzeeScoringScreen(
                gameId = route.gameId,
                onBack = { navController.popBackStack() },
                onGameFinished = {
                    navController.navigate(YahtzeeSummaryRoute(route.gameId)) {
                        popUpTo(YahtzeeRoute)
                    }
                }
            )
        }

        composable<YahtzeeSummaryRoute> { entry ->
            val route = entry.toRoute<YahtzeeSummaryRoute>()
            YahtzeeSummaryScreen(
                gameId = route.gameId,
                onHome = { navController.popBackStack(YahtzeeRoute, inclusive = false) }
            )
        }

        composable<YahtzeeStatisticsRoute> {
            YahtzeeStatisticsScreen(
                onBack = { navController.popBackStack() }
            )
        }

        // --- Counter Section ---
        composable<CounterRoute> { entry ->
            val viewModel: CounterViewModel = viewModel()
            val savedState = entry.savedStateHandle

            val resultType = savedState.get<String>("result_type")

            if (resultType != null) {
                if (resultType == "update") {
                    val id = savedState.get<String>("id") ?: ""
                    val name = savedState.get<String>("name") ?: ""
                    val count = savedState.get<Int>("count") ?: 0
                    val color = savedState.get<Long>("color") ?: 0L
                    viewModel.updateCounter(id, name, count, color)
                } else if (resultType == "delete") {
                    val id = savedState.get<String>("id") ?: ""
                    viewModel.deleteCounter(id)
                }
                savedState.remove<String>("result_type")
            }

            CounterScreen(
                onBack = { navController.popBackStack() },
                onEditCounter = { id, name, count, color ->
                    navController.navigate(EditCounterRoute(id, name, count, color))
                },
                onNavigateToHistory = { navController.navigate(HistoryRoute) },
                viewModel = viewModel
            )
        }

        composable<EditCounterRoute> { entry ->
            val route = entry.toRoute<EditCounterRoute>()

            EditCounterScreen(
                id = route.id,
                initialName = route.name,
                initialCount = route.count,
                initialColor = route.color,
                onBack = { navController.popBackStack() },
                onSave = { updatedId, updatedName, updatedCount, updatedColor ->
                    navController.previousBackStackEntry?.savedStateHandle?.apply {
                        set("result_type", "update")
                        set("id", updatedId)
                        set("name", updatedName)
                        set("count", updatedCount)
                        set("color", updatedColor)
                    }
                    navController.popBackStack()
                },
                onDelete = { deletedId ->
                    navController.previousBackStackEntry?.savedStateHandle?.apply {
                        set("result_type", "delete")
                        set("id", deletedId)
                    }
                    navController.popBackStack()
                }
            )
        }

        composable<HistoryRoute> {
            CounterHistoryScreen(
                onBackPressed = { navController.popBackStack() }
            )
        }

        composable<PlayersRoute> {
            PlayerSelectionScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable<DiceRollerRoute> {
            DiceRollerScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable<SettingsRoute> {
            val viewModel: SettingsViewModel = viewModel { SettingsViewModel() }
            SettingsScreen(viewModel = viewModel)
        }
    }
}
