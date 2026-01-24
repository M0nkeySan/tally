package io.github.m0nkeysan.tally.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.m0nkeysan.tally.core.utils.getCurrentTimeMillis
import io.github.m0nkeysan.tally.generated.resources.Res
import io.github.m0nkeysan.tally.generated.resources.cd_finished_game
import io.github.m0nkeysan.tally.generated.resources.player_count_display
import io.github.m0nkeysan.tally.ui.theme.LocalCustomColors
import org.jetbrains.compose.resources.stringResource

/**
 * Data class representing a game for display in selection screens.
 */
data class GameDisplay(
    val id: String,
    val name: String,
    val playerCount: Int,
    val playerNames: String,
    val isFinished: Boolean,
    val createdAt: Long,
    val updatedAt: Long
)

/**
 * Unified game selection card for all game types.
 * Uses flat design with minimal elevation.
 */
@Composable
fun GameSelectionCard(
    game: GameDisplay,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (game.isFinished) 
                MaterialTheme.colorScheme.surfaceContainer 
            else 
                MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = game.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = if (game.isFinished)
                        MaterialTheme.colorScheme.onSurfaceVariant
                    else 
                        MaterialTheme.colorScheme.onSurface
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = stringResource(Res.string.player_count_display, game.playerCount, if (game.playerCount > 1) "s" else ""),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = game.playerNames,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            if (game.isFinished) {
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = stringResource(Res.string.cd_finished_game),
                    tint = LocalCustomColors.current.trophyGold,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun GameSelectionCardPreview_Active() {
    GameSelectionCard(
        game = GameDisplay(
            id = "123",
            name = "Tarot Game 1",
            playerCount = 4,
            playerNames = "Alice, Bob, Charlie, Diana",
            isFinished = false,
            createdAt = getCurrentTimeMillis(),
            updatedAt = getCurrentTimeMillis()
        ),
        onClick = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun GameSelectionCardPreview_Finished() {
    GameSelectionCard(
        game = GameDisplay(
            id = "456",
            name = "Tarot Game 2",
            playerCount = 3,
            playerNames = "Eve, Frank, Grace",
            isFinished = true,
            createdAt = getCurrentTimeMillis(),
            updatedAt = getCurrentTimeMillis()
        ),
        onClick = {}
    )
}
