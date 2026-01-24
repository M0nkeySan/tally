package io.github.m0nkeysan.tally.platform

import androidx.compose.runtime.Composable

enum class HapticType {
    LIGHT, MEDIUM, HEAVY, SUCCESS, ERROR, SELECTION
}

interface HapticFeedbackController {
    fun performHapticFeedback(type: HapticType)
}

@Composable
expect fun rememberHapticFeedbackController(): HapticFeedbackController
