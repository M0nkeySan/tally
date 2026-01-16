package io.github.m0nkeysan.gamekeeper.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import io.github.m0nkeysan.gamekeeper.core.model.getCurrentTimeMillis
import io.github.m0nkeysan.gamekeeper.ui.theme.GameColors
import io.github.m0nkeysan.gamekeeper.ui.strings.AppStrings

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
                GameColors.Surface2 
            else 
                GameColors.Surface1
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
                        GameColors.TextSecondary 
                    else 
                        GameColors.TextPrimary
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = AppStrings.PLAYER_COUNT_DISPLAY.format(game.playerCount, if (game.playerCount > 1) "s" else ""),
                    style = MaterialTheme.typography.bodyMedium,
                    color = GameColors.TextSecondary
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = game.playerNames,
                    style = MaterialTheme.typography.bodySmall,
                    color = GameColors.TextSecondary
                )
            }
            
            if (game.isFinished) {
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = AppStrings.CD_FINISHED_GAME,
                    tint = GameColors.TrophyGold,
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
