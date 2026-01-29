package io.github.m0nkeysan.tally.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

/**
 * Web implementation of HapticFeedbackController.
 * Haptic feedback is not supported in standard web browsers.
 */
class WebHapticFeedbackController : HapticFeedbackController {
    override fun performHapticFeedback(type: HapticType) {
        // Haptic feedback not supported on web platform
        // Silently fail - no user-facing error message
    }
}

@Composable
actual fun rememberHapticFeedbackController(): HapticFeedbackController {
    return remember {
        WebHapticFeedbackController()
    }
}
