package io.github.m0nkeysan.gamekeeper.ui.screens.yahtzee

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.m0nkeysan.gamekeeper.ui.screens.common.ResultsTemplate

@Composable
fun YahtzeeSummaryScreen(
    gameId: String,
    onHome: () -> Unit,
    viewModel: YahtzeeScoringViewModel = viewModel { YahtzeeScoringViewModel() }
) {
    val state by viewModel.state.collectAsState()
    
    LaunchedEffect(gameId) {
        viewModel.loadGame(gameId)
    }

    val results = remember(state.scores) {
        viewModel.getAllPlayerScores().sortedByDescending { it.second }
    }
    
    val winners = remember(results) {
        if (results.isEmpty()) emptyList() 
        else {
            val max = results.first().second
            results.filter { it.second == max }
        }
    }

    if (state.game != null) {
        ResultsTemplate(
            winners = winners,
            allResults = results,
            onHome = onHome
        )
    }
}
