package io.github.m0nkeysan.tally.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.m0nkeysan.tally.ui.utils.parseColor

/**
 * A circular player avatar with their initial letter.
 *
 * @param name The player's name
 * @param avatarColorHex Hex string of the avatar color (e.g., "#FF0000")
 * @param modifier Optional modifier
 * @param size The diameter of the avatar circle
 */
@Composable
fun PlayerAvatar(
    name: String,
    avatarColorHex: String,
    modifier: Modifier = Modifier,
    size: Dp = 32.dp
) {
    val avatarColor = parseColor(avatarColorHex)
    val contentColor = if (avatarColor.luminance() > 0.5f) Color.Black.copy(alpha = 0.8f) else Color.White

    Box(
        modifier = modifier
            .size(size)
            .background(avatarColor, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = name.take(1).uppercase(),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = contentColor
        )
    }
}
