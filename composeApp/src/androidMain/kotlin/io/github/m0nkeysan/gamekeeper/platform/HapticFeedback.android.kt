package io.github.m0nkeysan.gamekeeper.platform

import android.Manifest
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.annotation.RequiresPermission
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

class AndroidHapticFeedbackController(
    context: Context
) : HapticFeedbackController {

    private val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        vibratorManager.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    @RequiresPermission(Manifest.permission.VIBRATE)
    override fun performHapticFeedback(type: HapticType) {
        if (!vibrator.hasVibrator()) return

        val effect = when (type) {
            HapticType.LIGHT -> VibrationEffect.createOneShot(10, VibrationEffect.DEFAULT_AMPLITUDE)
            HapticType.MEDIUM -> VibrationEffect.createOneShot(25, VibrationEffect.DEFAULT_AMPLITUDE)
            HapticType.HEAVY -> VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE)
            HapticType.SUCCESS -> VibrationEffect.createWaveform(longArrayOf(0, 50, 100, 50), -1)
            HapticType.ERROR -> VibrationEffect.createWaveform(longArrayOf(0, 100, 50, 100), -1)
            HapticType.SELECTION -> VibrationEffect.createOneShot(15, VibrationEffect.DEFAULT_AMPLITUDE)
        }
        vibrator.vibrate(effect)
    }
}

@Composable
actual fun rememberHapticFeedbackController(): HapticFeedbackController {
    val context = LocalContext.current
    return remember(context) {
        AndroidHapticFeedbackController(context)
    }
}