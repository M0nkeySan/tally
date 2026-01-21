package io.github.m0nkeysan.gamekeeper.ui.screens.tarot

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.m0nkeysan.gamekeeper.GameIcons
import io.github.m0nkeysan.gamekeeper.core.model.ChelemType
import io.github.m0nkeysan.gamekeeper.core.model.Player
import io.github.m0nkeysan.gamekeeper.core.model.TarotRound
import io.github.m0nkeysan.gamekeeper.generated.resources.Res
import io.github.m0nkeysan.gamekeeper.generated.resources.action_back
import io.github.m0nkeysan.gamekeeper.generated.resources.cd_settings
import io.github.m0nkeysan.gamekeeper.generated.resources.tarot_scoring_announce_chelem
import io.github.m0nkeysan.gamekeeper.generated.resources.tarot_scoring_announce_petit_au_bout
import io.github.m0nkeysan.gamekeeper.generated.resources.tarot_scoring_announce_poignee
import io.github.m0nkeysan.gamekeeper.generated.resources.tarot_scoring_cd_add_round
import io.github.m0nkeysan.gamekeeper.generated.resources.tarot_scoring_empty_rounds
import io.github.m0nkeysan.gamekeeper.generated.resources.tarot_scoring_round_details
import io.github.m0nkeysan.gamekeeper.generated.resources.tarot_scoring_round_title
import io.github.m0nkeysan.gamekeeper.generated.resources.tarot_scoring_screen_title
import io.github.m0nkeysan.gamekeeper.generated.resources.tarot_scoring_section_history
import io.github.m0nkeysan.gamekeeper.generated.resources.tarot_scoring_section_scores
import io.github.m0nkeysan.gamekeeper.ui.components.GameKeeperSnackbarHost
import io.github.m0nkeysan.gamekeeper.ui.components.showErrorSnackbar
import io.github.m0nkeysan.gamekeeper.ui.theme.GameColors
import io.github.m0nkeysan.gamekeeper.ui.utils.parseColor
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun TarotScoringScreen(
    gameId: String,
    onBack: () -> Unit,
    onAddNewRound: (String?) -> Unit,
    onNavigateToStatistics: (String) -> Unit = {},
    viewModel: TarotScoringViewModel = viewModel { TarotScoringViewModel() }
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Handle system back gesture
    BackHandler {
        onBack()
    }

    LaunchedEffect(gameId) {
        viewModel.loadGame(gameId)
    }

    // Show error in Snackbar
    LaunchedEffect(state.error) {
        state.error?.let { errorMessage ->
            showErrorSnackbar(snackbarHostState, errorMessage)
        }
    }

    Scaffold(
         topBar = {
             TopAppBar(
                 title = { 
                     Text(
                         stringResource(Res.string.tarot_scoring_screen_title),
                         style = MaterialTheme.typography.headlineSmall,
                         fontWeight = FontWeight.Bold,
                         modifier = Modifier.fillMaxWidth(),
                         textAlign = TextAlign.Center
                     ) 
                 },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(GameIcons.ArrowBack, contentDescription = stringResource(Res.string.action_back))
                    }
                },
                actions = {
                    IconButton(onClick = { onNavigateToStatistics(gameId) }) {
                        Icon(GameIcons.BarChart, contentDescription = stringResource(Res.string.cd_settings))
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onAddNewRound(null) },
                containerColor = GameColors.Primary
            ) {
                Icon(GameIcons.Add, contentDescription = stringResource(Res.string.tarot_scoring_cd_add_round))
            }
        },
        snackbarHost = {
            GameKeeperSnackbarHost(hostState = snackbarHostState)
        }
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            color = MaterialTheme.colorScheme.background
        ) {
            if (state.game == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                MainScoringView(
                    state = state,
                    viewModel = viewModel,
                    onEditRound = onAddNewRound
                )
            }
        }
    }
}

@Composable
fun MainScoringView(
    state: TarotScoringState,
    viewModel: TarotScoringViewModel,
    onEditRound: (String?) -> Unit
) {
    val playerScores = remember(state.rounds) { viewModel.getCurrentTotalScores() }

    Column(modifier = Modifier.fillMaxSize()) {
        PlayerSummarySection(
            players = state.players,
            scores = playerScores
        )

        Spacer(modifier = Modifier.height(16.dp))

        Box(modifier = Modifier.weight(1f)) {
            RoundHistorySection(
                rounds = state.rounds,
                players = state.players,
                playerCount = state.game?.playerCount ?: state.players.size,
                onEditRound = { onEditRound(it) }
            )
        }
    }
}

@Composable
fun PlayerSummarySection(
    players: List<Player>,
    scores: Map<String, Int>
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
            text = stringResource(Res.string.tarot_scoring_section_scores),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            players.forEach { player ->
                val score = scores[player.id] ?: 0
                val playerColor = remember(player.avatarColor) { parseColor(player.avatarColor) }
                val contentColor =
                    if (playerColor.luminance() > 0.5f) Color.Black.copy(alpha = 0.8f) else Color.White

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = playerColor,
                        contentColor = contentColor
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                modifier = Modifier.size(32.dp),
                                color = contentColor.copy(alpha = 0.2f)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = player.name.firstOrNull()?.uppercase() ?: "?",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = contentColor
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.size(12.dp))

                            Text(
                                text = player.name,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                color = contentColor
                            )
                        }

                        Text(
                            text = if (score >= 0) "+$score" else "-${kotlin.math.abs(score)}",
                            style = MaterialTheme.typography.titleLarge,
                            color = contentColor,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RoundHistorySection(
    rounds: List<TarotRound>,
    players: List<Player>,
    playerCount: Int,
    onEditRound: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = stringResource(Res.string.tarot_scoring_section_history),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (rounds.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = stringResource(Res.string.tarot_scoring_empty_rounds),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(rounds.reversed()) { round ->
                    val taker = players.find { it.id == round.takerPlayerId }?.name ?: "Unknown"
                    RoundHistoryItem(
                        round = round,
                        takerName = taker,
                        playerCount = playerCount,
                        onClick = { onEditRound(round.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun RoundHistoryItem(
    round: TarotRound,
    takerName: String,
    playerCount: Int,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(
                alpha = 0.5f
            )
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                     text = stringResource(Res.string.tarot_scoring_round_title, round.roundNumber, takerName),
                     style = MaterialTheme.typography.bodyMedium,
                     fontWeight = FontWeight.Bold
                 )

                val displayScore = when (playerCount) {
                    5 -> {
                        val partnerId = round.calledPlayerId
                        if (partnerId == null || partnerId == round.takerPlayerId) round.score * 4 else round.score * 2
                    }

                    else -> round.score * (playerCount - 1)
                }
                val scoreColor =
                    if (displayScore >= 0) GameColors.Success else GameColors.Error
                Text(
                    text = if (displayScore >= 0) "+$displayScore" else "-${kotlin.math.abs(displayScore)}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = scoreColor,
                    fontWeight = FontWeight.Black
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                 text = stringResource(Res.string.tarot_scoring_round_details, round.bid.displayName, round.bouts, round.pointsScored),
                 style = MaterialTheme.typography.bodySmall,
                 color = MaterialTheme.colorScheme.onSurfaceVariant
             )

             val announces = mutableListOf<String>()
             if (round.hasPetitAuBout) announces.add(stringResource(Res.string.tarot_scoring_announce_petit_au_bout))
             if (round.hasPoignee) announces.add(stringResource(Res.string.tarot_scoring_announce_poignee, round.poigneeLevel?.displayName ?: ""))
             if (round.chelem != ChelemType.NONE) announces.add(stringResource(Res.string.tarot_scoring_announce_chelem, round.chelem.displayName))

            if (announces.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = announces.joinToString(", "),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
