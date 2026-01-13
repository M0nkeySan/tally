package io.github.m0nkeysan.gamekeeper.ui.utils

import androidx.compose.ui.graphics.Color
import kotlin.math.*
import kotlin.random.Random

/**
 * Platform-agnostic color conversion utilities for HSV color space manipulation.
 * Works on all platforms (Android, iOS, Desktop, Web) without platform-specific dependencies.
 */

/**
 * Convert a Color to HSV (Hue, Saturation, Value) components.
 * 
 * @param color The Color to convert
 * @return A FloatArray with [Hue (0-360), Saturation (0-1), Value (0-1)]
 */
fun colorToHSV(color: Color): FloatArray {
    val r = color.red
    val g = color.green
    val b = color.blue
    
    val max = maxOf(r, g, b)
    val min = minOf(r, g, b)
    val delta = max - min
    
    // Calculate Hue (0-360)
    val hue = when {
        delta == 0f -> 0f
        max == r -> (60 * (((g - b) / delta) % 6) + 360) % 360
        max == g -> 60 * (((b - r) / delta) + 2)
        else -> 60 * (((r - g) / delta) + 4)
    }
    
    // Calculate Saturation (0-1)
    val saturation = if (max == 0f) 0f else delta / max
    
    // Value is just the maximum component
    val value = max
    
    return floatArrayOf(hue, saturation, value)
}

/**
 * Convert HSV (Hue, Saturation, Value) components to a Color.
 * 
 * @param hsv A FloatArray with [Hue (0-360), Saturation (0-1), Value (0-1)]
 * @return The resulting Color
 */
fun hsvToColor(hsv: FloatArray): Color {
    require(hsv.size >= 3) { "HSV array must have at least 3 components" }
    
    val hue = hsv[0]
    val saturation = hsv[1]
    val value = hsv[2]
    
    val c = value * saturation
    val hPrime = hue / 60f
    val x = c * (1 - abs(hPrime % 2 - 1))
    
    val (r1, g1, b1) = when {
        hPrime < 1 -> Triple(c, x, 0f)
        hPrime < 2 -> Triple(x, c, 0f)
        hPrime < 3 -> Triple(0f, c, x)
        hPrime < 4 -> Triple(0f, x, c)
        hPrime < 5 -> Triple(x, 0f, c)
        else -> Triple(c, 0f, x)
    }
    
     val m = value - c
     return Color(r1 + m, g1 + m, b1 + m)
}

/**
 * Generate a random hex color string (e.g., "#FF5733")
 */
fun generateRandomHexColor(): String {
    val color = Random.nextInt(0xFFFFFF)
    return "#${color.toString(16).padStart(6, '0')}"
}
