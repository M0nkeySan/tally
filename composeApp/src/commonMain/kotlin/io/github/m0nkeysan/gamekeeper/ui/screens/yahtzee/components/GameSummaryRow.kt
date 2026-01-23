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
import io.github.m0nkeysan.gamekeeper.core.utils.format
import io.github.m0nkeysan.gamekeeper.ui.theme.GameColors
import org.jetbrains.compose.resources.stringResource
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
    // Simple date formatting: Convert timestamp to date string
    val date = formatGameDate(game.completedAt)
    
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
                 text = stringResource(Res.string.counter_format_player_count, game.playerCount),
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
                    else -> stringResource(Res.string.yahtzee_rank_format, game.rank)
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

/**
 * Formats a timestamp (milliseconds) to a readable date string.
 * Example: 1704067200000 -> "Jan 1, 2024"
 */
private fun formatGameDate(timestamp: Long): String {
    val seconds = timestamp / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    val days = hours / 24
    
    // Reference date: January 1, 1970 (Unix epoch)
    val daysSinceEpoch = days.toInt()
    
    // Simple algorithm to calculate year, month, day
    var year = 1970
    var remainingDays = daysSinceEpoch
    
    // Calculate year
    while (remainingDays >= daysInYear(year)) {
        remainingDays -= daysInYear(year)
        year++
    }
    
    // Calculate month
    var month = 1
    while (remainingDays >= daysInMonth(month, year)) {
        remainingDays -= daysInMonth(month, year)
        month++
    }
    
    val day = remainingDays + 1
    
    val monthName = when (month) {
        1 -> "Jan"; 2 -> "Feb"; 3 -> "Mar"; 4 -> "Apr"
        5 -> "May"; 6 -> "Jun"; 7 -> "Jul"; 8 -> "Aug"
        9 -> "Sep"; 10 -> "Oct"; 11 -> "Nov"; 12 -> "Dec"
        else -> "???"
    }
    
    return "$monthName $day, $year"
}

private fun isLeapYear(year: Int): Boolean {
    return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)
}

private fun daysInYear(year: Int): Int {
    return if (isLeapYear(year)) 366 else 365
}

private fun daysInMonth(month: Int, year: Int): Int {
    return when (month) {
        1, 3, 5, 7, 8, 10, 12 -> 31
        4, 6, 9, 11 -> 30
        2 -> if (isLeapYear(year)) 29 else 28
        else -> 0
    }
}
