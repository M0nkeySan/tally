package io.github.m0nkeysan.tally.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import platform.UIKit.UIImpactFeedbackGenerator
import platform.UIKit.UIImpactFeedbackStyle
import platform.UIKit.UINotificationFeedbackGenerator
import platform.UIKit.UINotificationFeedbackType
import platform.UIKit.UISelectionFeedbackGenerator

class IOSHapticFeedbackController : HapticFeedbackController {
    
    private val lightGenerator = UIImpactFeedbackGenerator(UIImpactFeedbackStyle.UIImpactFeedbackStyleLight)
    private val mediumGenerator = UIImpactFeedbackGenerator(UIImpactFeedbackStyle.UIImpactFeedbackStyleMedium)
    private val heavyGenerator = UIImpactFeedbackGenerator(UIImpactFeedbackStyle.UIImpactFeedbackStyleHeavy)
    private val selectionGenerator = UISelectionFeedbackGenerator()
    private val notificationGenerator = UINotificationFeedbackGenerator()
    
    override fun performHapticFeedback(type: HapticType) {
        when (type) {
            HapticType.LIGHT -> {
                lightGenerator.prepare()
                lightGenerator.impactOccurred()
            }
            HapticType.MEDIUM -> {
                mediumGenerator.prepare()
                mediumGenerator.impactOccurred()
            }
            HapticType.HEAVY -> {
                heavyGenerator.prepare()
                heavyGenerator.impactOccurred()
            }
            HapticType.SUCCESS -> {
                notificationGenerator.prepare()
                notificationGenerator.notificationOccurred(UINotificationFeedbackType.UINotificationFeedbackTypeSuccess)
            }
            HapticType.ERROR -> {
                notificationGenerator.prepare()
                notificationGenerator.notificationOccurred(UINotificationFeedbackType.UINotificationFeedbackTypeError)
            }
            HapticType.SELECTION -> {
                selectionGenerator.prepare()
                selectionGenerator.selectionChanged()
            }
        }
    }
}

@Composable
actual fun rememberHapticFeedbackController(): HapticFeedbackController {
    return remember {
        IOSHapticFeedbackController()
    }
}
