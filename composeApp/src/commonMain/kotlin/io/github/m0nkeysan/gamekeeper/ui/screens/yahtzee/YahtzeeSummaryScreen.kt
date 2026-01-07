package io.github.m0nkeysan.gamekeeper.ui.screens.yahtzee

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.m0nkeysan.gamekeeper.GameIcons

@OptIn(ExperimentalMaterial3Api::class)
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Game Over", fontWeight = FontWeight.Bold) }
            )
        },
        bottomBar = {
            Button(
                onClick = onHome,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(56.dp)
            ) {
                Text("BACK TO HOME", fontWeight = FontWeight.Bold)
            }
        }
    ) { paddingValues ->
        if (state.game == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(24.dp))
                
                // Winner Icon
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = null,
                    modifier = Modifier.size(100.dp),
                    tint = Color(0xFFFFD700) // Gold
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Winner Announcement
                Text(
                    text = if (winners.size > 1) "IT'S A TIE!" else "WE HAVE A WINNER!",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Text(
                    text = winners.joinToString(" & ") { it.first }.uppercase(),
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(48.dp))
                
                Text(
                    text = "FINAL SCORES",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    letterSpacing = 1.5.sp
                )
                
                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    itemsIndexed(results) { index, (name, score) ->
                        val isWinner = winners.any { it.first == name }
                        ScoreRankItem(
                            rank = index + 1,
                            name = name,
                            score = score,
                            isWinner = isWinner
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ScoreRankItem(
    rank: Int,
    name: String,
    score: Int,
    isWinner: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isWinner) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        elevation = if (isWinner) CardDefaults.cardElevation(defaultElevation = 2.dp) else CardDefaults.cardElevation()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(if (isWinner) MaterialTheme.colorScheme.primary else Color.Gray.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = rank.toString(),
                    color = if (isWinner) Color.White else Color.Black,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Text(
                text = name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = if (isWinner) FontWeight.Bold else FontWeight.Normal,
                modifier = Modifier.weight(1f)
            )
            
            Text(
                text = score.toString(),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Black,
                color = if (isWinner) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
