package io.github.m0nkeysan.gamekeeper.ui.screens.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.m0nkeysan.gamekeeper.generated.resources.Res
import io.github.m0nkeysan.gamekeeper.generated.resources.results_action_back
import io.github.m0nkeysan.gamekeeper.generated.resources.results_section_scores
import io.github.m0nkeysan.gamekeeper.generated.resources.results_title_tie
import io.github.m0nkeysan.gamekeeper.generated.resources.results_title_winner
import io.github.m0nkeysan.gamekeeper.ui.components.ResultsCard
import io.github.m0nkeysan.gamekeeper.ui.theme.GameColors
import org.jetbrains.compose.resources.stringResource

/**
 * Reusable template for game results/summary screens.
 * Eliminates code duplication between different game result displays.
 *
 * Features:
 * - Trophy icon and winner announcement
 * - Tie detection and display ("IT'S A TIE!")
 * - Ranked results display using ResultsCard components
 * - Bottom sticky home button for navigation
 * - Consistent flat design with GameColors theme
 *
 * @param winners List of winner names with their scores
 * @param allResults List of all players with scores, sorted by rank
 * @param onHome Callback when home button is clicked
 * @param modifier Optional layout modifier
 *
 * Example usage:
 * ```
 * val winners = listOf("Alice" to 150, "Bob" to 150)  // Tie
 * val allResults = listOf(
 *     "Alice" to 150,
 *     "Bob" to 150,
 *     "Charlie" to 120
 * )
 * ResultsTemplate(
 *     winners = winners,
 *     allResults = allResults,
 *     onHome = { navController.popBackStack() }
 * )
 * ```
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultsTemplate(
    winners: List<Pair<String, Int>>,
    allResults: List<Pair<String, Int>>,
    onHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Game Over",
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        },
        bottomBar = {
            Button(
                onClick = onHome,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = GameColors.Primary
                )
            ) {
                Text(stringResource(Res.string.results_action_back), fontWeight = FontWeight.Bold)
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Trophy icon
            Icon(
                imageVector = Icons.Default.EmojiEvents,
                contentDescription = null,
                modifier = Modifier.size(100.dp),
                tint = GameColors.TrophyGold
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Winner announcement
            Text(
                text = if (winners.size > 1) stringResource(Res.string.results_title_tie) else stringResource(
                    Res.string.results_title_winner
                ),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Black,
                color = GameColors.Primary
            )

            Text(
                text = winners.joinToString(" & ") { it.first }.uppercase(),
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Final scores header
            Text(
                text = stringResource(Res.string.results_section_scores),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = GameColors.TextSecondary,
                letterSpacing = 1.5.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Results list
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                itemsIndexed(allResults) { index, (name, score) ->
                    val isWinner = winners.any { it.first == name }
                    ResultsCard(
                        rank = index + 1,
                        playerName = name,
                        score = score,
                        isWinner = isWinner
                    )
                }
            }
        }
    }
}
