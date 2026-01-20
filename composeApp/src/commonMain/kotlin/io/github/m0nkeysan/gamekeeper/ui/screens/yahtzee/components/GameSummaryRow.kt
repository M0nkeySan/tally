package io.github.m0nkeysan.gamekeeper.ui.screens.yahtzee.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.m0nkeysan.gamekeeper.core.model.GameSummary
import io.github.m0nkeysan.gamekeeper.ui.theme.GameColors
import org.jetbrains.compose.resources.stringResource
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import io.github.m0nkeysan.gamekeeper.generated.resources.counter_format_player_count
import io.github.m0nkeysan.gamekeeper.generated.resources.yahtzee_rank_first_place
import io.github.m0nkeysan.gamekeeper.generated.resources.yahtzee_rank_format
import io.github.m0nkeysan.gamekeeper.generated.resources.yahtzee_rank_second_place
import io.github.m0nkeysan.gamekeeper.generated.resources.yahtzee_rank_third_place
import io.github.m0nkeysan.gamekeeper.generated.resources.Res

/**
 * Row displaying a summary of a single completed game
 */
@Composable
fun GameSummaryRow(
    game: GameSummary,
    modifier: Modifier = Modifier
) {
    val dateFormat = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
    val date = dateFormat.format(Date(game.completedAt))
    
    val cardColor = if (game.isWinner) {
        GameColors.Secondary.copy(alpha = 0.1f)
    } else {
        MaterialTheme.colorScheme.surface
    }
    
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Game info
            androidx.compose.foundation.layout.Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = game.gameName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = date,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // Player count
             Text(
                 text = String.format(stringResource(Res.string.counter_format_player_count), game.playerCount),
                 style = MaterialTheme.typography.bodySmall,
                 color = MaterialTheme.colorScheme.onSurfaceVariant,
                 modifier = Modifier.padding(horizontal = 8.dp)
             )
            
            // Rank and score
            androidx.compose.foundation.layout.Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val rankColor = when (game.rank) {
                    1 -> GameColors.Success
                    2 -> GameColors.Warning
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                }
                
                val rankText = when (game.rank) {
                    1 -> stringResource(Res.string.yahtzee_rank_first_place)
                    2 -> stringResource(Res.string.yahtzee_rank_second_place)
                    3 -> stringResource(Res.string.yahtzee_rank_third_place)
                    else -> String.format(stringResource(Res.string.yahtzee_rank_format), game.rank)
                }
                
                Text(
                    text = rankText,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = rankColor
                )
                
                Text(
                    text = game.totalScore.toString(),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
