package io.github.m0nkeysan.gamekeeper.ui.components

import androidx.compose.animation.core.animateDpAsState
import android.graphics.Color as AndroidColor
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import kotlin.math.*

val DEFAULT_COLOR_PRESETS = listOf(
    "#F44336", // Red
    "#E91E63", // Pink
    "#9C27B0", // Purple
    "#673AB7", // Deep Purple
    "#3F51B5", // Indigo
    "#2196F3", // Blue
    "#00BCD4", // Cyan
    "#4CAF50", // Green
    "#FFC107", // Amber
    "#FF9800", // Orange
)

val DIALOG_COLOR_PRESETS = listOf(
    "#F44336", // Red
    "#E91E63", // Pink
    "#9C27B0", // Purple
    "#2196F3", // Blue
    "#4CAF50", // Green
    "#FFC107", // Amber
    "#FF9800", // Orange
)

@Composable
fun ColorSelectorRow(
    selectedColorHex: String,
    onColorSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    presets: List<String> = DEFAULT_COLOR_PRESETS
) {
    var showCustomPicker by remember { mutableStateOf(false) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 1. Color Swatches
        presets.forEach { colorHex ->
            val isSelected = selectedColorHex.equals(colorHex, ignoreCase = true)

            ColorSwatchItem(
                colorHex = colorHex,
                isSelected = isSelected,
                onClick = { onColorSelected(colorHex) }
            )
        }

        // 2. Spacer & Custom Picker Button
        Spacer(modifier = Modifier.width(12.dp)) // Slightly reduced spacer to fit more

        val isCustomSelected = presets.none { it.equals(selectedColorHex, ignoreCase = true) }
        val customColor = if (isCustomSelected) parseColor(selectedColorHex) else Color(0xFF4CAF50)

        CustomPickerSwatch(
            color = customColor,
            isSelected = isCustomSelected,
            onClick = { showCustomPicker = true }
        )
    }

    if (showCustomPicker) {
        ColorPickerDialog(
            initialColor = selectedColorHex,
            onDismiss = { showCustomPicker = false },
            onColorSelected = {
                onColorSelected(it)
                showCustomPicker = false
            }
        )
    }
}

@Composable
fun ColorSwatchItem(
    colorHex: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val color = remember(colorHex) { parseColor(colorHex) }

    // Adjusted widths to fit more items in a single row without scrolling
    val width by animateDpAsState(if (isSelected) 40.dp else 24.dp, label = "width")
    val height by animateDpAsState(if (isSelected) 64.dp else 40.dp, label = "height")
    val elevation by animateDpAsState(if (isSelected) 8.dp else 0.dp, label = "elevation")

    Surface(
        modifier = Modifier
            .size(width = width, height = height)
            .clickable(onClick = onClick)
            .zIndex(if (isSelected) 1f else 0f),
        shape = androidx.compose.ui.graphics.RectangleShape,
        color = color,
        shadowElevation = elevation
    ) {
    }
}

@Composable
fun CustomPickerSwatch(
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    // Fixed width and height as requested, matching the height of selected presets
    val size = 48.dp
    val elevation by animateDpAsState(if (isSelected) 8.dp else 4.dp, label = "elevation")

    Surface(
        modifier = Modifier
            .size(size)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp), // Rounded corners for the button
        color = color,
        shadowElevation = elevation,
        border = if (isSelected) BorderStroke(3.dp, MaterialTheme.colorScheme.primary) else null
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = Icons.Default.Palette,
                contentDescription = "Custom Color",
                tint = if (color.luminance() > 0.5f) Color.Black else Color.White
            )
        }
    }
}

@Composable
fun ColorPickerDialog(
    initialColor: String,
    onDismiss: () -> Unit,
    onColorSelected: (String) -> Unit
) {
    val initialHsv = remember(initialColor) {
        val floatArray = FloatArray(3)
        AndroidColor.colorToHSV(parseColor(initialColor).toArgb(), floatArray)
        floatArray
    }

    var hue by remember { mutableFloatStateOf(initialHsv[0]) }
    var saturation by remember { mutableFloatStateOf(initialHsv[1]) }
    var value by remember { mutableFloatStateOf(initialHsv[2]) }

    val currentColor = remember(hue, saturation, value) {
        Color(AndroidColor.HSVToColor(floatArrayOf(hue, saturation, value)))
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Pick a Color") },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ColorWheel(
                    hue = hue,
                    saturation = saturation,
                    onChange = { newH, newS ->
                        hue = newH
                        saturation = newS
                    },
                    modifier = Modifier.size(250.dp)
                )

                BrightnessSlider(
                    hue = hue,
                    saturation = saturation,
                    value = value,
                    onValueChange = { value = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(30.dp)
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .background(currentColor, RoundedCornerShape(4.dp))
                        .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val argb = currentColor.toArgb()
                val hex = String.format("#%06X", (0xFFFFFF and argb))
                onColorSelected(hex)
            }) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun ColorWheel(
    hue: Float,
    saturation: Float,
    onChange: (Float, Float) -> Unit,
    modifier: Modifier = Modifier
) {
    var size by remember { mutableStateOf(IntSize.Zero) }

    Box(
        modifier = modifier
            .onGloballyPositioned { size = it.size }
            .pointerInput(Unit) {
                detectDragGestures { change, _ ->
                    val position = change.position
                    val centerX = size.width / 2f
                    val centerY = size.height / 2f
                    val dx = position.x - centerX
                    val dy = position.y - centerY

                    // Calculate Angle (Hue)
                    val angle = (Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())) + 360) % 360

                    // Calculate Distance (Saturation)
                    val maxRadius = size.width / 2f
                    val distance = sqrt(dx * dx + dy * dy)
                    val sat = (distance / maxRadius).coerceIn(0f, 1f)

                    onChange(angle.toFloat(), sat)
                }
            }
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    val centerX = size.width / 2f
                    val centerY = size.height / 2f
                    val dx = offset.x - centerX
                    val dy = offset.y - centerY
                    val angle = (Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())) + 360) % 360
                    val maxRadius = size.width / 2f
                    val distance = sqrt(dx * dx + dy * dy)
                    val sat = (distance / maxRadius).coerceIn(0f, 1f)
                    onChange(angle.toFloat(), sat)
                }
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val radius = size.width / 2f

            val sweepGradient = Brush.sweepGradient(
                colors = listOf(
                    Color.Red,
                    Color.Yellow,
                    Color.Green,
                    Color.Cyan,
                    Color.Blue,
                    Color.Magenta,
                    Color.Red
                ),
                center = center
            )
            drawCircle(brush = sweepGradient, radius = radius, center = center)

            // 2. Radial Gradient (Saturation: White center -> Transparent edge)
            val radialGradient = Brush.radialGradient(
                colors = listOf(Color.White, Color.White.copy(alpha = 0f)),
                center = center,
                radius = radius
            )
            drawCircle(brush = radialGradient, radius = radius, center = center)

            // 3. Selection Indicator
            val angleRad = Math.toRadians(hue.toDouble())
            val satRadius = saturation * radius
            val selectorX = center.x + satRadius * cos(angleRad).toFloat()
            val selectorY = center.y + satRadius * sin(angleRad).toFloat()

            drawCircle(
                color = Color.Black,
                radius = 8.dp.toPx(),
                center = Offset(selectorX, selectorY),
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.dp.toPx())
            )
            drawCircle(
                color = Color.White,
                radius = 6.dp.toPx(),
                center = Offset(selectorX, selectorY)
            )
        }
    }
}

@Composable
fun BrightnessSlider(
    hue: Float,
    saturation: Float,
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    // The color at standard brightness (Value = 1)
    val baseColor = remember(hue, saturation) {
        Color(AndroidColor.HSVToColor(floatArrayOf(hue, saturation, 1f)))
    }

    BoxWithConstraints(modifier = modifier) {
        val width = maxWidth

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectDragGestures { change, _ ->
                        val newVal = (change.position.x / size.width).coerceIn(0f, 1f)
                        onValueChange(newVal)
                    }
                }
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        val newVal = (offset.x / size.width).coerceIn(0f, 1f)
                        onValueChange(newVal)
                    }
                }
        ) {
            // Gradient from Black to BaseColor
            val brush = Brush.horizontalGradient(
                colors = listOf(Color.Black, baseColor)
            )
            drawRoundRect(
                brush = brush,
                size = size,
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(10f, 10f)
            )

            // Selector Circle
            val selectorX = value * size.width
            drawCircle(
                color = Color.White,
                radius = size.height / 1.5f,
                center = Offset(selectorX, size.height / 2)
            )
        }
    }
}

// Helper (Reused from your code)
fun parseColor(colorHex: String): Color {
    return try {
        Color(0xFF000000 or colorHex.removePrefix("#").toLong(16))
    } catch (e: Exception) {
        Color.Gray
    }
}