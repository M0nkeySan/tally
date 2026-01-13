package io.github.m0nkeysan.gamekeeper.platform

import androidx.compose.runtime.Composable

/**
 * Platform-specific shake detection implementation
 * Detects device shake via accelerometer on supported platforms
 */
expect class ShakeDetector

/**
 * Composable to detect device shake and trigger a callback
 * 
 * @param onShake Callback triggered when shake is detected
 * @param enabled Whether shake detection is active
 */
@Composable
expect fun rememberShakeDetector(
    onShake: () -> Unit,
    enabled: Boolean
): ShakeDetector
