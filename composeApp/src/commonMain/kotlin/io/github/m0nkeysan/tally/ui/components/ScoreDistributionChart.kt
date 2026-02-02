package io.github.m0nkeysan.tally.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.m0nkeysan.tally.core.model.DistributionCategory
import io.github.m0nkeysan.tally.core.model.ScoreDistribution
import io.github.m0nkeysan.tally.generated.resources.Res
import io.github.m0nkeysan.tally.generated.resources.game_tracker_game_stats_dist_high
import io.github.m0nkeysan.tally.generated.resources.game_tracker_game_stats_dist_low
import io.github.m0nkeysan.tally.generated.resources.game_tracker_game_stats_dist_medium
import io.github.m0nkeysan.tally.generated.resources.game_tracker_game_stats_dist_negative
import io.github.m0nkeysan.tally.generated.resources.game_tracker_game_stats_dist_zero
import io.github.m0nkeysan.tally.ui.theme.LocalCustomColors
import org.jetbrains.compose.resources.stringResource

/**
 * Horizontal bar chart showing score distribution across categories
 * 
 * @param distribution The score distribution data
 * @param modifier Optional modifier
 */
@Composable
fun ScoreDistributionChart(
    distribution: ScoreDistribution,
    modifier: Modifier = Modifier
) {
    val total = distribution.total()
    
    if (total == 0) {
        Text(
            text = "No data",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = modifier
        )
        return
    }
    
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // Negative scores
        if (distribution.negative > 0) {
            DistributionBar(
                label = stringResource(Res.string.game_tracker_game_stats_dist_negative),
                count = distribution.negative,
                percentage = distribution.getPercentage(DistributionCategory.NEGATIVE),
                color = MaterialTheme.colorScheme.error
            )
        }
        
        // Zero scores
        if (distribution.zero > 0) {
            DistributionBar(
                label = stringResource(Res.string.game_tracker_game_stats_dist_zero),
                count = distribution.zero,
                percentage = distribution.getPercentage(DistributionCategory.ZERO),
                color = MaterialTheme.colorScheme.surfaceVariant
            )
        }
        
        // Low scores (1-25)
        if (distribution.low > 0) {
            DistributionBar(
                label = stringResource(Res.string.game_tracker_game_stats_dist_low),
                count = distribution.low,
                percentage = distribution.getPercentage(DistributionCategory.LOW),
                color = LocalCustomColors.current.warning
            )
        }
        
        // Medium scores (26-50)
        if (distribution.medium > 0) {
            DistributionBar(
                label = stringResource(Res.string.game_tracker_game_stats_dist_medium),
                count = distribution.medium,
                percentage = distribution.getPercentage(DistributionCategory.MEDIUM),
                color = MaterialTheme.colorScheme.primary
            )
        }
        
        // High scores (51+)
        if (distribution.high > 0) {
            DistributionBar(
                label = stringResource(Res.string.game_tracker_game_stats_dist_high),
                count = distribution.high,
                percentage = distribution.getPercentage(DistributionCategory.HIGH),
                color = LocalCustomColors.current.success
            )
        }
    }
}

@Composable
private fun DistributionBar(
    label: String,
    count: Int,
    percentage: Float,
    color: androidx.compose.ui.graphics.Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Label
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.width(80.dp)
        )
        
        Spacer(modifier = Modifier.width(8.dp))
        
        // Bar
        Box(
            modifier = Modifier
                .weight(1f)
                .height(16.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(percentage / 100f)
                    .height(16.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(color)
            )
        }
        
        Spacer(modifier = Modifier.width(8.dp))
        
        // Count
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(24.dp)
        )
    }
}
