package io.github.m0nkeysan.tally.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.m0nkeysan.tally.core.model.Player
import io.github.m0nkeysan.tally.core.model.RoundProgressData
import io.github.m0nkeysan.tally.ui.utils.parseColor
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * Line chart showing cumulative score progression for each player across rounds
 * 
 * @param progressData List of cumulative scores per round
 * @param players List of players to display
 * @param modifier Optional modifier
 */
@Composable
fun ProgressLineChart(
    progressData: List<RoundProgressData>,
    players: List<Player>,
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()
    val gridColor = MaterialTheme.colorScheme.outlineVariant
    val textColor = MaterialTheme.colorScheme.onSurface
    
    Column(modifier = modifier) {
        // Chart
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .padding(start = 40.dp, end = 16.dp, top = 16.dp, bottom = 32.dp)
        ) {
            if (progressData.isEmpty()) return@Canvas
            
            val chartWidth = size.width
            val chartHeight = size.height
            
            // Find min and max scores for Y-axis scaling
            val allScores = progressData.flatMap { it.cumulativeScores.values }
            val maxScore = allScores.maxOrNull() ?: 100
            val minScore = allScores.minOrNull() ?: 0
            
            // Add padding to min/max for better visualization
            val scorePadding = max(10, (maxScore - minScore) / 10)
            val yMax = maxScore + scorePadding
            val yMin = minScore - scorePadding
            val yRange = yMax - yMin
            
            // Calculate step size for grid lines
            val gridLineCount = 5
            val yStep = yRange / gridLineCount
            
            // Draw horizontal grid lines and Y-axis labels
            for (i in 0..gridLineCount) {
                val yValue = yMin + (yStep * i)
                val y = chartHeight - (((yValue - yMin) / yRange) * chartHeight)
                
                // Grid line
                drawLine(
                    color = gridColor,
                    start = Offset(0f, y),
                    end = Offset(chartWidth, y),
                    strokeWidth = 1.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f))
                )
                
                // Y-axis label
                val label = yValue.toInt().toString()
                val textLayoutResult = textMeasurer.measure(
                    text = label,
                    style = TextStyle(
                        color = textColor,
                        fontSize = 10.sp
                    )
                )
                drawText(
                    textLayoutResult = textLayoutResult,
                    topLeft = Offset(-textLayoutResult.size.width - 8.dp.toPx(), y - textLayoutResult.size.height / 2)
                )
            }
            
            // Draw X-axis labels (round numbers)
            val xStep = chartWidth / max(1, progressData.size - 1)
            progressData.forEachIndexed { index, round ->
                val x = index * xStep
                val label = round.roundNumber.toString()
                val textLayoutResult = textMeasurer.measure(
                    text = label,
                    style = TextStyle(
                        color = textColor,
                        fontSize = 10.sp
                    )
                )
                drawText(
                    textLayoutResult = textLayoutResult,
                    topLeft = Offset(x - textLayoutResult.size.width / 2, chartHeight + 8.dp.toPx())
                )
            }
            
            // Draw lines for each player
            players.forEach { player ->
                val playerColor = parseColor(player.avatarColor)
                val points = mutableListOf<Offset>()
                
                // Calculate points for this player
                progressData.forEachIndexed { index, round ->
                    val score = round.cumulativeScores[player.id] ?: 0
                    val x = index * xStep
                    val y = chartHeight - (((score - yMin) / yRange) * chartHeight)
                    points.add(Offset(x, y.toFloat()))
                }
                
                // Draw line connecting points
                if (points.size > 1) {
                    val path = Path()
                    path.moveTo(points[0].x, points[0].y)
                    for (i in 1 until points.size) {
                        path.lineTo(points[i].x, points[i].y)
                    }
                    
                    drawPath(
                        path = path,
                        color = playerColor,
                        style = Stroke(
                            width = 3.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                    )
                }
                
                // Draw points (circles) at each data point
                points.forEach { point ->
                    drawCircle(
                        color = playerColor,
                        radius = 4.dp.toPx(),
                        center = point
                    )
                    // White center for better visibility
                    drawCircle(
                        color = Color.White,
                        radius = 2.dp.toPx(),
                        center = point
                    )
                }
            }
        }
        
        // Legend
        PlayerLegend(players = players)
    }
}

@Composable
private fun PlayerLegend(players: List<Player>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        players.forEach { player ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(end = 16.dp)
            ) {
                Surface(
                    modifier = Modifier.size(12.dp),
                    shape = CircleShape,
                    color = parseColor(player.avatarColor)
                ) {}
                
                Spacer(modifier = Modifier.width(4.dp))
                
                Text(
                    text = player.name,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
