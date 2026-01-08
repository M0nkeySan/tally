package io.github.m0nkeysan.gamekeeper.ui.screens.common

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.m0nkeysan.gamekeeper.ui.components.ResultsCard
import io.github.m0nkeysan.gamekeeper.ui.theme.GameColors

/**
 * Reusable template for game results/summary screens.
 * Displays winners and ranked results consistently.
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
            TopAppBar(
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
                Text("BACK TO HOME", fontWeight = FontWeight.Bold)
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
                text = if (winners.size > 1) "IT'S A TIE!" else "WE HAVE A WINNER!",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Black,
                color = GameColors.Primary
            )
            
            Text(
                text = winners.joinToString(" & ") { it.first }.uppercase(),
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center,
                color = GameColors.TextPrimary
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            // Final scores header
            Text(
                text = "FINAL SCORES",
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
