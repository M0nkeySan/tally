package io.github.m0nkeysan.tally.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.m0nkeysan.tally.core.model.Streak
import io.github.m0nkeysan.tally.core.model.StreakType
import io.github.m0nkeysan.tally.ui.theme.LocalCustomColors

/**
 * Badge showing a player's current winning or losing streak
 * 
 * @param streak The streak data
 * @param modifier Optional modifier
 */
@Composable
fun StreakBadge(
    streak: Streak,
    modifier: Modifier = Modifier
) {
    if (!streak.isActive()) {
        return // Don't show badge for streaks of 1 or less
    }
    
    val (icon, color) = when (streak.type) {
        StreakType.WINNING -> "🔥" to LocalCustomColors.current.success
        StreakType.LOSING -> "❄️" to MaterialTheme.colorScheme.error
        StreakType.NEUTRAL -> "➖" to MaterialTheme.colorScheme.surfaceVariant
    }
    
    Row(
        modifier = modifier
            .background(
                color = color.copy(alpha = 0.15f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = icon,
            style = MaterialTheme.typography.bodySmall
        )
        
        Spacer(modifier = Modifier.width(4.dp))
        
        Text(
            text = streak.length.toString(),
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}
