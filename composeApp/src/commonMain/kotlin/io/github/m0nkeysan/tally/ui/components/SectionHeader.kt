package io.github.m0nkeysan.tally.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.m0nkeysan.tally.GameIcons
import io.github.m0nkeysan.tally.generated.resources.Res
import io.github.m0nkeysan.tally.generated.resources.cd_toggle_collapse
import io.github.m0nkeysan.tally.generated.resources.cd_toggle_expand
import org.jetbrains.compose.resources.stringResource

/**
 * A section header with the Tarot-style banner background.
 * Can be static (centered) or collapsible (chevron on the left).
 *
 * @param title The header text
 * @param modifier Optional modifier
 * @param isExpanded If null, the header is static. If provided, it shows a chevron and handles clicks.
 * @param onToggle Callback for collapsible headers
 */
@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    isExpanded: Boolean? = null,
    onToggle: (() -> Unit)? = null
) {
    val backgroundColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
    val shape = MaterialTheme.shapes.small

    if (isExpanded != null && onToggle != null) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .background(backgroundColor, shape)
                .clickable(onClick = onToggle)
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = if (isExpanded) GameIcons.ExpandLess else GameIcons.ExpandMore,
                contentDescription = stringResource(
                    if (isExpanded) Res.string.cd_toggle_collapse
                    else Res.string.cd_toggle_expand
                ),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    } else {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = modifier
                .fillMaxWidth()
                .background(backgroundColor, shape)
                .padding(8.dp),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
