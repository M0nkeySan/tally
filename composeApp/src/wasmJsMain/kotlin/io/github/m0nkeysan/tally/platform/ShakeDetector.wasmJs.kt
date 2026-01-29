package io.github.m0nkeysan.tally.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

/**
 * Web implementation of ShakeDetector.
 * Shake detection is not supported in standard web browsers.
 */
actual class ShakeDetector {
    fun start() {
        // Shake detection not supported on web platform
    }
    
    fun stop() {
        // No-op
    }
}

@Composable
actual fun rememberShakeDetector(
    onShake: () -> Unit,
    enabled: Boolean
): ShakeDetector {
    return remember(enabled) {
        ShakeDetector().apply {
            if (enabled) {
                start()
            }
        }
    }
}
