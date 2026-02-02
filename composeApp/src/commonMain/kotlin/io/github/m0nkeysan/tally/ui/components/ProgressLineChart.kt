package io.github.m0nkeysan.tally.ui.components

import androidx.compose.foundation.Canvas
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.m0nkeysan.tally.core.model.Player
import io.github.m0nkeysan.tally.core.model.RoundProgressData
import io.github.m0nkeysan.tally.ui.utils.parseColor
import kotlin.math.max

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
    if (progressData.isEmpty()) return

    val textMeasurer = rememberTextMeasurer()

    val chartData = remember(progressData, players) {
        val allScores = progressData.flatMap { it.cumulativeScores.values }
        val maxScore = allScores.maxOrNull() ?: 100
        val minScore = allScores.minOrNull() ?: 0
        val scorePadding = max(10, (maxScore - minScore) / 10)

        val yMax = (maxScore + scorePadding).toFloat()
        val yMin = (minScore - scorePadding).toFloat()
        val yRange = if (yMax == yMin) 1f else yMax - yMin

        object {
            val yMin = yMin
            val yRange = yRange
            val gridLineCount = 5
        }
    }

    val labelColor = MaterialTheme.colorScheme.onSurfaceVariant

    Column(modifier = modifier) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .padding(start = 48.dp, end = 16.dp, top = 16.dp, bottom = 32.dp)
        ) {
            val chartWidth = size.width
            val chartHeight = size.height
            val xStep = if (progressData.size > 1) chartWidth / (progressData.size - 1) else 0f

            val yStepValue = chartData.yRange / chartData.gridLineCount
            for (i in 0..chartData.gridLineCount) {
                val scoreValue = chartData.yMin + (yStepValue * i)
                val y =
                    chartHeight - ((scoreValue - chartData.yMin) / chartData.yRange * chartHeight)

                drawLine(
                    color = Color.LightGray,
                    start = Offset(0f, y),
                    end = Offset(chartWidth, y),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f))
                )

                drawText(
                    textMeasurer = textMeasurer,
                    text = scoreValue.toInt().toString(),
                    style = TextStyle(fontSize = 10.sp, color = labelColor),
                    topLeft = Offset(-35.dp.toPx(), y - 10.sp.toPx()),
                )
            }

            // 3. Draw Player Lines
            players.forEach { player ->
                val playerColor = parseColor(player.avatarColor)
                val path = Path()

                progressData.forEachIndexed { index, round ->
                    val score = (round.cumulativeScores[player.id] ?: 0).toFloat()
                    val x = index * xStep
                    val y =
                        chartHeight - ((score - chartData.yMin) / chartData.yRange * chartHeight)

                    if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)

                    drawCircle(playerColor, 4.dp.toPx(), Offset(x, y))
                }

                drawPath(path, playerColor, style = Stroke(width = 3.dp.toPx()))
            }
        }

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
