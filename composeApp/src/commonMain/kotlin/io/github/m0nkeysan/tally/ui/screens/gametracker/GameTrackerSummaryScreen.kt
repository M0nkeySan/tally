package io.github.m0nkeysan.tally.ui.screens.gametracker

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.m0nkeysan.tally.core.domain.model.ScoringLogic
import io.github.m0nkeysan.tally.generated.resources.Res
import io.github.m0nkeysan.tally.generated.resources.game_tracker_scoring_total_score
import io.github.m0nkeysan.tally.generated.resources.game_tracker_summary_action_finish
import io.github.m0nkeysan.tally.generated.resources.game_tracker_summary_action_rematch
import io.github.m0nkeysan.tally.generated.resources.game_tracker_summary_draw_title
import io.github.m0nkeysan.tally.generated.resources.game_tracker_summary_final_scores
import io.github.m0nkeysan.tally.generated.resources.game_tracker_summary_winner_title
import io.github.m0nkeysan.tally.ui.utils.parseColor
import org.jetbrains.compose.resources.stringResource

@Composable
fun GameTrackerSummaryScreen(
    gameId: String,
    onRematch: (String) -> Unit,
    onFinish: () -> Unit,
    viewModel: GameTrackerSummaryViewModel = viewModel { GameTrackerSummaryViewModel() }
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(gameId) {
        viewModel.loadGame(gameId)
    }

    Scaffold(
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedButton(
                        onClick = { viewModel.rematch(onRematch) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(stringResource(Res.string.game_tracker_summary_action_rematch))
                    }
                    Button(
                        onClick = onFinish,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(stringResource(Res.string.game_tracker_summary_action_finish))
                    }
                }
            }
        }
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            color = MaterialTheme.colorScheme.background
        ) {
            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (state.game != null) {
                val game = state.game!!
                val players = state.players
                val scores = state.totalScores

                // Sort players for leaderboard
                val sortedPlayers = when (game.scoringLogic) {
                    ScoringLogic.HIGH_SCORE_WINS -> players.sortedByDescending { scores[it.id] ?: 0 }
                    ScoringLogic.LOW_SCORE_WINS -> players.sortedBy { scores[it.id] ?: 0 }
                }

                val winner = sortedPlayers.firstOrNull()
                val isDraw = sortedPlayers.size > 1 && scores[sortedPlayers[0].id] == scores[sortedPlayers[1].id]

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Winner Header
                    Text(
                        text = if (isDraw) stringResource(Res.string.game_tracker_summary_draw_title) 
                               else stringResource(Res.string.game_tracker_summary_winner_title),
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Winner Avatar
                    winner?.let { w ->
                        val avatarColor = parseColor(w.avatarColor)
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .background(avatarColor, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = w.name.take(1).uppercase(),
                                color = if (avatarColor.luminance() > 0.5) Color.Black else Color.White,
                                style = MaterialTheme.typography.displayLarge.copy(fontSize = 60.sp),
                                fontWeight = FontWeight.Bold
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = w.name,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Text(
                            text = (scores[w.id] ?: 0).toString(),
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Final Standings
                    Text(
                        text = stringResource(Res.string.game_tracker_summary_final_scores),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Start
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    sortedPlayers.forEachIndexed { index, player ->
                        StandingRow(
                            player = player,
                            score = scores[player.id] ?: 0,
                            position = index + 1
                        )
                        if (index < sortedPlayers.size - 1) {
                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StandingRow(
    player: io.github.m0nkeysan.tally.core.model.Player,
    score: Int,
    position: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "$position.",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.width(32.dp)
            )
            
            val avatarColor = parseColor(player.avatarColor)
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(avatarColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = player.name.take(1).uppercase(),
                    color = if (avatarColor.luminance() > 0.5) Color.Black else Color.White,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Text(
                text = player.name,
                style = MaterialTheme.typography.bodyLarge
            )
        }
        
        Text(
            text = score.toString(),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}
