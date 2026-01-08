package io.github.m0nkeysan.gamekeeper.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.m0nkeysan.gamekeeper.ui.theme.GameColors

/**
 * Card displaying a player's rank and score in game results.
 * Highlights winners with indigo background.
 */
@Composable
fun ResultsCard(
    rank: Int,
    playerName: String,
    score: Int,
    isWinner: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isWinner) 
                GameColors.PrimaryLight 
            else 
                GameColors.Surface1
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isWinner) 2.dp else 0.dp
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Rank badge
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(
                        if (isWinner) GameColors.Primary else GameColors.Surface2
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = rank.toString(),
                    color = if (isWinner) Color.White else GameColors.TextPrimary,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            // Player name
            Text(
                text = playerName,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (isWinner) FontWeight.Bold else FontWeight.Normal,
                modifier = Modifier.weight(1f),
                color = GameColors.TextPrimary
            )
            
            // Score
            Text(
                text = score.toString(),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Black,
                color = if (isWinner) GameColors.Primary else GameColors.TextPrimary
            )
        }
    }
}
