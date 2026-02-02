package io.github.m0nkeysan.tally.core.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import io.github.m0nkeysan.tally.ui.screens.common.HomeNavigationTemplate
import io.github.m0nkeysan.tally.ui.screens.counter.CounterHistoryScreen
import io.github.m0nkeysan.tally.ui.screens.counter.CounterScreen
import io.github.m0nkeysan.tally.ui.screens.counter.CounterViewModel
import io.github.m0nkeysan.tally.ui.screens.counter.EditCounterScreen
import io.github.m0nkeysan.tally.ui.screens.dice.DiceRollerScreen
import io.github.m0nkeysan.tally.ui.screens.fingerselector.FingerSelectorScreen
import io.github.m0nkeysan.tally.ui.screens.gametracker.GameTrackerCreationScreen
import io.github.m0nkeysan.tally.ui.screens.gametracker.GameTrackerHistoryScreen
import io.github.m0nkeysan.tally.ui.screens.gametracker.GameTrackerRoundAdditionScreen
import io.github.m0nkeysan.tally.ui.screens.gametracker.GameTrackerScoringScreen
import io.github.m0nkeysan.tally.ui.screens.gametracker.GameTrackerSelectionScreen
import io.github.m0nkeysan.tally.ui.screens.gametracker.GameTrackerStatisticsScreen
import io.github.m0nkeysan.tally.ui.screens.gametracker.GameTrackerSummaryScreen
import io.github.m0nkeysan.tally.ui.screens.home.HomeCustomizationScreen
import io.github.m0nkeysan.tally.ui.screens.tarot.TarotGameCreationScreen
import io.github.m0nkeysan.tally.ui.screens.tarot.TarotGameSelectionScreen
import io.github.m0nkeysan.tally.ui.screens.tarot.TarotRoundAdditionScreen
import io.github.m0nkeysan.tally.ui.screens.tarot.TarotScoringScreen
import io.github.m0nkeysan.tally.ui.screens.tarot.TarotStatisticsScreen
import io.github.m0nkeysan.tally.ui.screens.yahtzee.YahtzeeGameCreationScreen
import io.github.m0nkeysan.tally.ui.screens.yahtzee.YahtzeeGameSelectionScreen
import io.github.m0nkeysan.tally.ui.screens.yahtzee.YahtzeeScoringScreen
import io.github.m0nkeysan.tally.ui.screens.yahtzee.YahtzeeStatisticsScreen
import io.github.m0nkeysan.tally.ui.screens.yahtzee.YahtzeeSummaryScreen

@Composable
fun GameNavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = HomeRoute
    ) {
        composable<HomeRoute> { backStackEntry ->
            HomeNavigationTemplate(
                onNavigateTo = { route -> navController.navigateSafe(route) },
                navBackStackEntry = backStackEntry
            )
        }

        composable<FingerSelectorRoute> {
            FingerSelectorScreen(onBack = {
                navController.popSafe()
            })
        }

        // --- Tarot Section ---
        composable<TarotRoute> {
            TarotGameSelectionScreen(
                onBack = {
                    navController.popSafe()
                },
                onCreateNewGame = { navController.navigateSafe(TarotCreationRoute) },
                onSelectGame = { gameId ->
                    navController.navigateSafe(TarotScoringRoute(gameId))
                }
            )
        }

        composable<TarotCreationRoute> {
            TarotGameCreationScreen(
                onBack = {
                    navController.popSafe()
                },
                onGameCreated = { gameId ->
                    navController.navigateSafe(TarotScoringRoute(gameId)) {
                        popUpTo(TarotRoute)
                    }
                }
            )
        }

        composable<TarotScoringRoute> { entry ->
            val route = entry.toRoute<TarotScoringRoute>()

            TarotScoringScreen(
                gameId = route.gameId,
                onBack = {
                    navController.popSafe()
                },
                onAddNewRound = { roundId ->
                    navController.navigateSafe(TarotRoundAdditionRoute(route.gameId, roundId))
                },
                onNavigateToStatistics = { statsGameId ->
                    navController.navigateSafe(TarotStatisticsRoute(statsGameId))
                }
            )
        }

        composable<TarotRoundAdditionRoute> { entry ->
            val route = entry.toRoute<TarotRoundAdditionRoute>()

            TarotRoundAdditionScreen(
                gameId = route.gameId,
                roundId = route.roundId,
                onBack = {
                    navController.popSafe()
                },
                onRoundAdded = {
                    navController.popSafe()
                }
            )
        }

        composable<TarotStatisticsRoute> { entry ->
            val route = entry.toRoute<TarotStatisticsRoute>()
            TarotStatisticsScreen(
                gameId = route.gameId,
                onBack = {
                    navController.popSafe()
                }
            )
        }

        // --- Yahtzee Section ---
        composable<YahtzeeRoute> {
            YahtzeeGameSelectionScreen(
                onBack = { navController.navigateSafe(HomeRoute) },
                onCreateNewGame = { navController.navigateSafe(YahtzeeCreationRoute) },
                onSelectGame = { gameId ->
                    navController.navigateSafe(YahtzeeScoringRoute(gameId))
                },
                onNavigateToStatistics = {
                    navController.navigateSafe(YahtzeeStatisticsRoute)
                }
            )
        }

        composable<YahtzeeCreationRoute> {
            YahtzeeGameCreationScreen(
                onBack = {
                    navController.popSafe()
                },
                onGameCreated = { gameId ->
                    navController.navigateSafe(YahtzeeScoringRoute(gameId)) {
                        popUpTo(YahtzeeRoute)
                    }
                }
            )
        }

        composable<YahtzeeScoringRoute> { entry ->
            val route = entry.toRoute<YahtzeeScoringRoute>()
            YahtzeeScoringScreen(
                gameId = route.gameId,
                onBack = {
                    navController.navigateSafe(YahtzeeRoute)
                },
                onGameFinished = {
                    navController.navigateSafe(YahtzeeSummaryRoute(route.gameId)) {
                        popUpTo(YahtzeeRoute)
                    }
                }
            )
        }

        composable<YahtzeeSummaryRoute> { entry ->
            val route = entry.toRoute<YahtzeeSummaryRoute>()
            YahtzeeSummaryScreen(
                gameId = route.gameId,
                onHome = { navController.popBackStackSafe(YahtzeeRoute, inclusive = false) }
            )
        }

        composable<YahtzeeStatisticsRoute> {
            YahtzeeStatisticsScreen(
                onBack = {
                    navController.popSafe()
                }
            )
        }

        // --- Game Tracker Section ---
        composable<GameTrackerRoute> {
            GameTrackerSelectionScreen(
                onBack = {
                    navController.popSafe()
                },
                onCreateNewGame = { navController.navigateSafe(GameTrackerCreationRoute) },
                onSelectGame = { gameId ->
                    navController.navigateSafe(GameTrackerScoringRoute(gameId))
                },
                onNavigateToStatistics = {
                    navController.navigateSafe(GameTrackerStatisticsRoute)
                }
            )
        }

        composable<GameTrackerCreationRoute> {
            GameTrackerCreationScreen(
                onBack = {
                    navController.popSafe()
                },
                onGameCreated = { gameId ->
                    navController.navigateSafe(GameTrackerScoringRoute(gameId)) {
                        popUpTo(GameTrackerRoute)
                    }
                }
            )
        }

        composable<GameTrackerScoringRoute> { entry ->
            val route = entry.toRoute<GameTrackerScoringRoute>()
            
            GameTrackerScoringScreen(
                gameId = route.gameId,
                onBack = {
                    navController.popSafe()
                },
                onAddNewRound = { roundNumber ->
                    navController.navigateSafe(GameTrackerRoundAdditionRoute(route.gameId, roundNumber, null))
                },
                onEditRound = { roundNumber, roundId ->
                    navController.navigateSafe(GameTrackerRoundAdditionRoute(route.gameId, roundNumber, roundId))
                },
                onFinishGame = {
                    navController.navigateSafe(GameTrackerSummaryRoute(route.gameId)) {
                        popUpTo(GameTrackerRoute)
                    }
                },
                onNavigateToHistory = {
                    navController.navigateSafe(GameTrackerHistoryRoute)
                }
            )
        }

        composable<GameTrackerRoundAdditionRoute> { entry ->
            val route = entry.toRoute<GameTrackerRoundAdditionRoute>()
            
            GameTrackerRoundAdditionScreen(
                gameId = route.gameId,
                roundNumber = route.roundNumber,
                roundId = route.roundId,
                onBack = {
                    navController.popSafe()
                },
                onRoundSaved = {
                    navController.popSafe()
                }
            )
        }

        composable<GameTrackerSummaryRoute> { entry ->
            val route = entry.toRoute<GameTrackerSummaryRoute>()
            GameTrackerSummaryScreen(
                gameId = route.gameId,
                onRematch = { newGameId ->
                    navController.navigateSafe(GameTrackerScoringRoute(newGameId)) {
                        popUpTo(GameTrackerRoute)
                    }
                },
                onFinish = {
                    navController.popBackStackSafe(GameTrackerRoute, inclusive = false)
                }
            )
        }

        composable<GameTrackerHistoryRoute> {
            GameTrackerHistoryScreen(
                onBack = {
                    navController.popSafe()
                }
            )
        }

        composable<GameTrackerStatisticsRoute> {
            GameTrackerStatisticsScreen(
                onBack = {
                    navController.popSafe()
                }
            )
        }

        // --- Counter Section ---
        composable<CounterRoute> { entry ->
            val viewModel: CounterViewModel = viewModel { CounterViewModel() }
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
                onBack = {
                    navController.popSafe()
                },
                onEditCounter = { id, name, count, color ->
                    navController.navigateSafe(EditCounterRoute(id, name, count, color))
                },
                onNavigateToHistory = { navController.navigateSafe(HistoryRoute) },
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
                onBack = {
                    navController.popSafe()
                },
                onSave = { updatedId, updatedName, updatedCount, updatedColor ->
                    navController.previousBackStackEntry?.savedStateHandle?.apply {
                        set("result_type", "update")
                        set("id", updatedId)
                        set("name", updatedName)
                        set("count", updatedCount)
                        set("color", updatedColor)
                    }
                    navController.popSafe()
                },
                onDelete = { deletedId ->
                    navController.previousBackStackEntry?.savedStateHandle?.apply {
                        set("result_type", "delete")
                        set("id", deletedId)
                    }
                    navController.popSafe()
                }
            )
        }

        composable<HistoryRoute> {
            CounterHistoryScreen(
                onBack = {
                    navController.popSafe()
                }
            )
        }

        composable<DiceRollerRoute> {
            DiceRollerScreen(
                onBack = {
                    navController.popSafe()
                }
            )
        }

        composable<HomeCustomizationRoute> {
            HomeCustomizationScreen(
                onNavigateBack = {
                    navController.popSafe()
                },
                onSaveSuccess = {
                    navController.popBackStack(HomeRoute, inclusive = false)
                    navController.currentBackStackEntry?.savedStateHandle?.set("home_customization_success", true)
                }
            )
        }
    }
}

fun <T : Any> NavHostController.navigateSafe(route: T, builder: NavOptionsBuilder.() -> Unit = {}) {
    val currentState = this.currentBackStackEntry?.lifecycle?.currentState
    if (currentState == Lifecycle.State.RESUMED || currentState == Lifecycle.State.STARTED) {
        this.navigate(route, builder)
    }
}

fun NavHostController.popSafe() {
    val currentState = this.currentBackStackEntry?.lifecycle?.currentState
    if (currentState == Lifecycle.State.RESUMED || currentState == Lifecycle.State.STARTED) {
        this.popBackStack()
    }
}

fun <T : Any> NavHostController.popBackStackSafe(
    route: T,
    inclusive: Boolean,
    saveState: Boolean = false
) {
    val currentState = this.currentBackStackEntry?.lifecycle?.currentState
    if (currentState == Lifecycle.State.RESUMED || currentState == Lifecycle.State.STARTED) {
        popBackStack(route, inclusive, saveState)
    }
}
