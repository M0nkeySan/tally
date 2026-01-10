package io.github.m0nkeysan.gamekeeper

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

actual object GameIcons {
    actual val History: ImageVector = Icons.Default.History
    actual val TouchApp: ImageVector = Icons.Default.TouchApp
    actual val Casino: ImageVector = Icons.Default.Casino
    actual val GridView: ImageVector = Icons.Default.GridView
    actual val Add: ImageVector = Icons.Default.Add
    actual val Refresh: ImageVector = Icons.Default.Refresh
    actual val Remove: ImageVector = Icons.Default.Remove
    actual val ArrowBack: ImageVector = Icons.AutoMirrored.Filled.ArrowBack
    actual val KeyboardArrowRight: ImageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight
    actual val Settings: ImageVector = Icons.Default.Settings
    actual val Group: ImageVector = Icons.Default.Group
    actual val Palette: ImageVector = Icons.Default.Palette
    actual val Delete: ImageVector = Icons.Default.Delete
    actual val MoreVert: ImageVector = Icons.Default.MoreVert
    actual val Dice: ImageVector = Icons.Default.Casino
    actual val Cards: ImageVector = Icons.Default.Casino
    actual val Tarot: ImageVector = ImageVector.Builder(
        name = "Tarot",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        // 1. Left Card (Black)
        path(fill = SolidColor(Color.Black)) {
            moveTo(6.5f, 4.0f)
            curveTo(5.54f, 3.74f, 4.56f, 4.31f, 4.3f, 5.27f)
            lineTo(2.12f, 13.27f)
            curveTo(1.86f, 14.23f, 2.44f, 15.2f, 3.4f, 15.47f)
            lineTo(8.43f, 16.84f)
            curveTo(9.39f, 17.1f, 10.37f, 16.52f, 10.63f, 15.56f)
            lineTo(12.81f, 7.57f)
            curveTo(13.07f, 6.61f, 12.5f, 5.63f, 11.54f, 5.37f)
            lineTo(6.5f, 4.0f)
            close()
        }

        // 2. Right Card (Black)
        path(fill = SolidColor(Color.Black)) {
            moveTo(17.5f, 4.0f)
            curveTo(18.46f, 3.74f, 19.44f, 4.31f, 19.7f, 5.27f)
            lineTo(21.88f, 13.27f)
            curveTo(22.14f, 14.23f, 21.56f, 15.2f, 20.6f, 15.47f)
            lineTo(15.57f, 16.84f)
            curveTo(14.61f, 17.1f, 13.63f, 16.52f, 13.37f, 15.56f)
            lineTo(11.19f, 7.57f)
            curveTo(10.93f, 6.61f, 11.5f, 5.63f, 12.46f, 5.37f)
            lineTo(17.5f, 4.0f)
            close()
        }

        // 3. Center Card (Black with Stroke Cutout)
        path(
            fill = SolidColor(Color.Black),
            stroke = SolidColor(Color(0xFFEFEBF5)),
            strokeLineWidth = 1.5f
        ) {
            moveTo(8f, 6.5f)
            curveTo(8f, 5f, 8f, 5f, 9.5f, 5f)
            lineTo(14.5f, 5f)
            curveTo(16f, 5f, 16f, 5f, 16f, 6.5f)
            lineTo(16f, 17.5f)
            curveTo(16f, 19f, 16f, 19f, 14.5f, 19f)
            lineTo(9.5f, 19f)
            curveTo(8f, 19f, 8f, 19f, 8f, 17.5f)
            close()
        }
    }.build()
}
