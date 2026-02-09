package io.github.m0nkeysan.tally.ui.screens.gametracker

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.m0nkeysan.tally.GameIcons
import io.github.m0nkeysan.tally.core.domain.model.DurationMode
import io.github.m0nkeysan.tally.core.domain.model.ScoringLogic
import io.github.m0nkeysan.tally.core.model.GameTrackerGame
import io.github.m0nkeysan.tally.core.model.GameTrackerRound
import io.github.m0nkeysan.tally.core.model.Player
import io.github.m0nkeysan.tally.generated.resources.Res
import io.github.m0nkeysan.tally.generated.resources.action_back
import io.github.m0nkeysan.tally.generated.resources.action_cancel
import io.github.m0nkeysan.tally.generated.resources.action_delete
import io.github.m0nkeysan.tally.generated.resources.game_tracker_round_delete_dialog_message
import io.github.m0nkeysan.tally.generated.resources.game_tracker_round_delete_dialog_title
import io.github.m0nkeysan.tally.generated.resources.game_tracker_scoring_cd_add_round
import io.github.m0nkeysan.tally.generated.resources.game_tracker_scoring_cd_finish_game
import io.github.m0nkeysan.tally.generated.resources.game_tracker_scoring_cd_game_stats
import io.github.m0nkeysan.tally.generated.resources.game_tracker_scoring_cd_history
import io.github.m0nkeysan.tally.generated.resources.game_tracker_scoring_empty_rounds
import io.github.m0nkeysan.tally.generated.resources.game_tracker_scoring_others
import io.github.m0nkeysan.tally.generated.resources.game_tracker_scoring_round_title
import io.github.m0nkeysan.tally.generated.resources.game_tracker_scoring_section_leaderboard
import io.github.m0nkeysan.tally.generated.resources.game_tracker_scoring_section_rounds
import io.github.m0nkeysan.tally.generated.resources.game_tracker_scoring_should_finish
import io.github.m0nkeysan.tally.generated.resources.game_tracker_scoring_target_reached
import io.github.m0nkeysan.tally.generated.resources.game_tracker_scoring_title
import io.github.m0nkeysan.tally.generated.resources.game_tracker_scoring_winner
import io.github.m0nkeysan.tally.ui.components.AppSnackbarHost
import io.github.m0nkeysan.tally.ui.components.showErrorSnackbar
import io.github.m0nkeysan.tally.ui.utils.parseColor
import org.jetbrains.compose.resources.stringResource

@Composable
fun GameTrackerScoringScreen(
    gameId: String,
    onBack: () -> Unit,
    onAddNewRound: (Int) -> Unit,
    onEditRound: (Int, String) -> Unit,
    onFinishGame: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToGameStats: () -> Unit,
    viewModel: GameTrackerScoringViewModel = viewModel { GameTrackerScoringViewModel() }
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var roundToDelete by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(gameId) {
        viewModel.loadGame(gameId)
    }

    // Show error in Snackbar
    LaunchedEffect(state.error) {
        state.error?.let { errorMessage ->
            showErrorSnackbar(snackbarHostState, errorMessage)
            viewModel.onErrorConsumed()
        }
    }

    // Delete round confirmation dialog
    roundToDelete?.let { roundNumber ->
        AlertDialog(
            onDismissRequest = { roundToDelete = null },
            title = { Text(stringResource(Res.string.game_tracker_round_delete_dialog_title)) },
            text = { Text(stringResource(Res.string.game_tracker_round_delete_dialog_message, roundNumber)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteRound(roundNumber)
                        roundToDelete = null
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(stringResource(Res.string.action_delete))
                }
            },
            dismissButton = {
                TextButton(onClick = { roundToDelete = null }) {
                    Text(stringResource(Res.string.action_cancel))
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        state.game?.name ?: stringResource(Res.string.game_tracker_scoring_title),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            GameIcons.ArrowBack,
                            contentDescription = stringResource(Res.string.action_back)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToGameStats) {
                        Icon(
                            GameIcons.BarChart,
                            contentDescription = stringResource(Res.string.game_tracker_scoring_cd_game_stats)
                        )
                    }
                    IconButton(onClick = onNavigateToHistory) {
                        Icon(
                            GameIcons.History,
                            contentDescription = stringResource(Res.string.game_tracker_scoring_cd_history)
                        )
                    }
                    if (state.game?.isFinished == false) {
                        IconButton(
                            onClick = {
                                viewModel.finishGame(
                                    onFinished = onFinishGame,
                                    onError = { /* Error already shown via state */ }
                                )
                            }
                        ) {
                            Icon(
                                GameIcons.Check,
                                contentDescription = stringResource(Res.string.game_tracker_scoring_cd_finish_game)
                            )
                        }
                    }
                },
                modifier = Modifier.shadow(elevation = 2.dp)
            )
        },
        floatingActionButton = {
            if (state.game?.isFinished == false) {
                FloatingActionButton(
                    onClick = {
                        val nextRound = state.rounds.maxOfOrNull { it.roundNumber }?.plus(1) ?: 1
                        onAddNewRound(nextRound)
                    },
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(
                        GameIcons.Add,
                        contentDescription = stringResource(Res.string.game_tracker_scoring_cd_add_round)
                    )
                }
            }
        },
        snackbarHost = {
            AppSnackbarHost(hostState = snackbarHostState)
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
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Leaderboard Section
                    item {
                        LeaderboardSection(
                            game = state.game!!,
                            players = state.players,
                            totalScores = state.totalScores
                        )
                    }

                    // Round History Section
                    item {
                        Text(
                            text = stringResource(Res.string.game_tracker_scoring_section_rounds),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (state.rounds.isEmpty()) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                                ),
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(32.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = stringResource(Res.string.game_tracker_scoring_empty_rounds),
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    } else {
                        // Group rounds by round number
                        val roundsByNumber = state.rounds.groupBy { it.roundNumber }
                        
                        items(roundsByNumber.keys.sortedDescending()) { roundNumber ->
                            RoundCard(
                                roundNumber = roundNumber,
                                rounds = roundsByNumber[roundNumber] ?: emptyList(),
                                players = state.players,
                                onEditClick = { round ->
                                    onEditRound(roundNumber, round.id)
                                },
                                onDeleteClick = {
                                    roundToDelete = roundNumber
                                },
                                isFinished = state.game?.isFinished == true
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LeaderboardSection(
    game: GameTrackerGame,
    players: List<Player>,
    totalScores: Map<String, Int>
) {
    Column {
        Text(
            text = stringResource(Res.string.game_tracker_scoring_section_leaderboard),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(8.dp))

        val sortedPlayers = when (game.scoringLogic) {
            ScoringLogic.HIGH_SCORE_WINS -> players.sortedByDescending { totalScores[it.id] ?: 0 }
            ScoringLogic.LOW_SCORE_WINS -> players.sortedBy { totalScores[it.id] ?: 0 }
        }

        sortedPlayers.forEachIndexed { index, player ->
            val score = totalScores[player.id] ?: 0
            val hasReachedTarget = game.targetScore != null && 
                ((game.scoringLogic == ScoringLogic.HIGH_SCORE_WINS && score >= game.targetScore) ||
                 (game.scoringLogic == ScoringLogic.LOW_SCORE_WINS && score <= game.targetScore))
            
            PlayerScoreCard(
                player = player,
                score = score,
                hasReachedTarget = hasReachedTarget,
                isGameFinished = game.isFinished,
                isWinner = game.isFinished && game.winnerPlayerId == player.id
            )
            
            if (index < sortedPlayers.size - 1) {
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        // Show win status indicators
        if (!game.isFinished) {
            if (game.durationMode == DurationMode.FIXED_ROUNDS && 
                game.fixedRoundCount != null && 
                game.currentRound >= game.fixedRoundCount) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(Res.string.game_tracker_scoring_should_finish),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun PlayerScoreCard(
    player: Player,
    score: Int,
    hasReachedTarget: Boolean,
    isGameFinished: Boolean,
    isWinner: Boolean
) {
    val avatarColor = parseColor(player.avatarColor)
    val contentColor = if (avatarColor.luminance() > 0.5f) Color.Black.copy(alpha = 0.8f) else Color.White

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = avatarColor,
            contentColor = contentColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation =  1.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Avatar
                Surface(
                    shape = CircleShape,
                    modifier = Modifier.size(32.dp),
                    color = contentColor.copy(alpha = 0.2f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = player.name.take(1),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = contentColor
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = player.name,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = contentColor
                    )
                    
                    if (hasReachedTarget && !isGameFinished) {
                        Text(
                            text = stringResource(Res.string.game_tracker_scoring_target_reached),
                            style = MaterialTheme.typography.bodySmall,
                            color = contentColor.copy(alpha = 0.9f),
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    if (isWinner) {
                        Text(
                            text = stringResource(Res.string.game_tracker_scoring_winner),
                            style = MaterialTheme.typography.bodySmall,
                            color = contentColor.copy(alpha = 0.9f),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Text(
                text = score.toString(),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = contentColor
            )
        }
    }
}

@Composable
private fun RoundCard(
    roundNumber: Int,
    rounds: List<GameTrackerRound>,
    players: List<Player>,
    onEditClick: (GameTrackerRound) -> Unit,
    onDeleteClick: () -> Unit,
    isFinished: Boolean
) {
    val dismissState = rememberSwipeToDismissBoxState()
    LaunchedEffect(dismissState.currentValue) {
        if (dismissState.currentValue == SwipeToDismissBoxValue.EndToStart) {
            onDeleteClick()
            dismissState.snapTo(SwipeToDismissBoxValue.Settled)
        }
    }
    
    // Calculate highlights
    val highestScore = rounds.maxOfOrNull { it.score }
    val lowestScore = rounds.minOfOrNull { it.score }
    
    val highestScorer = rounds.find { it.score == highestScore }
    val lowestScorer = rounds.find { it.score == lowestScore }
    
    val highestPlayer = highestScorer?.let { round ->
        players.find { it.id == round.playerId }
    }
    val lowestPlayer = lowestScorer?.let { round ->
        players.find { it.id == round.playerId }
    }
    
    // Count "others" (players who are neither highest nor lowest)
    val otherCount = if (rounds.size > 2) rounds.size - 2 else 0

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = false,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    imageVector = GameIcons.Delete,
                    contentDescription = "Delete round",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled = !isFinished) { 
                    rounds.firstOrNull()?.let(onEditClick) 
                },
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            border = BorderStroke(
                1.dp,
                MaterialTheme.colorScheme.outlineVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // Round number
                Text(
                    text = stringResource(Res.string.game_tracker_scoring_round_title, roundNumber),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                // Score highlights
                if (rounds.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Highest scorer
                        highestPlayer?.let { player ->
                            val scoreText = if (highestScore != null && highestScore >= 0) {
                                "+$highestScore"
                            } else {
                                "$highestScore"
                            }
                            Text(
                                text = "📈 ${player.name} ($scoreText)",
                                style = MaterialTheme.typography.bodyMedium,
                                color = io.github.m0nkeysan.tally.ui.theme.LocalCustomColors.current.success
                            )
                        }
                        
                        // Separator
                        if (highestPlayer != null && (lowestPlayer != null || otherCount > 0)) {
                            Text(
                                text = " | ",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        
                        // Lowest scorer (only if different from highest)
                        if (lowestPlayer != null && lowestPlayer.id != highestPlayer?.id) {
                            val scoreText = if (lowestScore != null && lowestScore >= 0) {
                                "+$lowestScore"
                            } else {
                                "$lowestScore"
                            }
                            Text(
                                text = "📉 ${lowestPlayer.name} ($scoreText)",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                        
                        // Others count
                        if (otherCount > 0) {
                            // Add separator if we showed lowest
                            if (lowestPlayer != null && lowestPlayer.id != highestPlayer?.id) {
                                Text(
                                    text = " | ",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(
                                text = "+$otherCount ${stringResource(Res.string.game_tracker_scoring_others)}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}
