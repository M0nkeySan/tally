package io.github.m0nkeysan.gamekeeper.core.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import io.github.m0nkeysan.gamekeeper.ui.screens.home.HomeScreen
import io.github.m0nkeysan.gamekeeper.ui.screens.fingerselector.FingerSelectorScreen
import io.github.m0nkeysan.gamekeeper.ui.screens.tarot.TarotScoringScreen
import io.github.m0nkeysan.gamekeeper.ui.screens.tarot.TarotGameSelectionScreen
import io.github.m0nkeysan.gamekeeper.ui.screens.tarot.TarotGameCreationScreen
import io.github.m0nkeysan.gamekeeper.ui.screens.tarot.TarotRoundAdditionScreen
import io.github.m0nkeysan.gamekeeper.ui.screens.yahtzee.YahtzeeScoringScreen
import io.github.m0nkeysan.gamekeeper.ui.screens.yahtzee.YahtzeeSummaryScreen
import io.github.m0nkeysan.gamekeeper.ui.screens.yahtzee.YahtzeeGameSelectionScreen
import io.github.m0nkeysan.gamekeeper.ui.screens.yahtzee.YahtzeeGameCreationScreen
import io.github.m0nkeysan.gamekeeper.ui.screens.counter.CounterScreen
import io.github.m0nkeysan.gamekeeper.ui.screens.counter.EditCounterScreen
import io.github.m0nkeysan.gamekeeper.ui.screens.addplayer.AddPlayerScreen
import io.github.m0nkeysan.gamekeeper.ui.viewmodel.CounterViewModel

@Composable
fun GameNavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(route = Screen.Home.route) {
            HomeScreen(onNavigateTo = { route -> navController.navigate(route) })
        }

        composable(route = Screen.FingerSelector.route) {
            FingerSelectorScreen(onBack = { navController.popBackStack() })
        }

        // Tarot
        composable(route = Screen.Tarot.route) {
            TarotGameSelectionScreen(
                onBack = { navController.popBackStack() },
                onCreateNewGame = { navController.navigate(Screen.TarotCreation.route) },
                onSelectGame = { gameId -> navController.navigate(Screen.TarotScoring.createRoute(gameId)) }
            )
        }

        composable(route = Screen.TarotCreation.route) {
            TarotGameCreationScreen(
                onBack = { navController.popBackStack() },
                onGameCreated = { gameId -> 
                    navController.navigate(Screen.TarotScoring.createRoute(gameId)) {
                        popUpTo(Screen.Tarot.route)
                    }
                }
            )
        }

        composable(
            route = Screen.TarotScoring.route,
            arguments = listOf(navArgument("gameId") { type = NavType.StringType })
        ) { entry ->
            val gameId = entry.arguments?.getString("gameId") ?: ""
            TarotScoringScreen(
                gameId = gameId,
                onBack = { navController.popBackStack() },
                onAddNewRound = { roundId -> 
                    navController.navigate(Screen.TarotRoundAddition.createRoute(gameId, roundId)) 
                }
            )
        }

        composable(
            route = Screen.TarotRoundAddition.route,
            arguments = listOf(
                navArgument("gameId") { type = NavType.StringType },
                navArgument("roundId") { 
                    type = NavType.StringType
                    nullable = true
                }
            )
        ) { entry ->
            val gameId = entry.arguments?.getString("gameId") ?: ""
            val roundIdStr = entry.arguments?.getString("roundId")
            val roundId = roundIdStr?.toLongOrNull()
            
            TarotRoundAdditionScreen(
                gameId = gameId,
                roundId = if (roundId != null && roundId > 0) roundId else null,
                onBack = { navController.popBackStack() },
                onRoundAdded = { navController.popBackStack() }
            )
        }

        // Yahtzee
        composable(route = Screen.Yahtzee.route) {
            YahtzeeGameSelectionScreen(
                onBack = { navController.popBackStack() },
                onCreateNewGame = { navController.navigate(Screen.YahtzeeCreation.route) },
                onSelectGame = { gameId -> navController.navigate(Screen.YahtzeeScoring.createRoute(gameId)) }
            )
        }

        composable(route = Screen.YahtzeeCreation.route) {
            YahtzeeGameCreationScreen(
                onBack = { navController.popBackStack() },
                onGameCreated = { gameId ->
                    navController.navigate(Screen.YahtzeeScoring.createRoute(gameId)) {
                        popUpTo(Screen.Yahtzee.route)
                    }
                }
            )
        }

        composable(
            route = Screen.YahtzeeScoring.route,
            arguments = listOf(navArgument("gameId") { type = NavType.StringType })
        ) { entry ->
            val gameId = entry.arguments?.getString("gameId") ?: ""
            YahtzeeScoringScreen(
                gameId = gameId,
                onBack = { navController.popBackStack() },
                onGameFinished = { navController.navigate(Screen.YahtzeeSummary.createRoute(gameId)) {
                    popUpTo(Screen.Yahtzee.route)
                } }
            )
        }

        composable(
            route = Screen.YahtzeeSummary.route,
            arguments = listOf(navArgument("gameId") { type = NavType.StringType })
        ) { entry ->
            val gameId = entry.arguments?.getString("gameId") ?: ""
            YahtzeeSummaryScreen(
                gameId = gameId,
                onHome = { navController.popBackStack(Screen.Yahtzee.route, inclusive = false) }
            )
        }

        // Counter
        composable(route = Screen.Counter.route) { entry ->
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
                    navController.navigate(Screen.EditCounter.createRoute(id, name, count, color))
                },
                viewModel = viewModel
            )
        }

        composable(
            route = Screen.EditCounter.route,
            arguments = listOf(
                navArgument("id") { type = NavType.StringType },
                navArgument("name") { type = NavType.StringType },
                navArgument("count") { type = NavType.IntType },
                navArgument("color") { type = NavType.LongType }
            )
        ) { entry ->
            val id = entry.arguments?.getString("id") ?: ""
            val name = entry.arguments?.getString("name") ?: ""
            val count = entry.arguments?.getInt("count") ?: 0
            val color = entry.arguments?.getLong("color") ?: 0L
            
            EditCounterScreen(
                id = id,
                initialName = name,
                initialCount = count,
                initialColor = color,
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

        composable(route = Screen.AddPlayer.route) {
            AddPlayerScreen(onBack = { navController.popBackStack() })
        }
    }
}
