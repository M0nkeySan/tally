package io.github.m0nkeysan.tally.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import io.github.m0nkeysan.tally.core.utils.getCurrentTimeMillis
import kotlinx.cinterop.useContents
import platform.CoreMotion.CMAccelerometerData
import platform.CoreMotion.CMMotionManager
import platform.Foundation.NSError
import platform.Foundation.NSOperationQueue
import kotlin.math.sqrt

actual class ShakeDetector

@Composable
actual fun rememberShakeDetector(
    onShake: () -> Unit,
    enabled: Boolean
): ShakeDetector {
    val motionManager = remember { CMMotionManager() }

    DisposableEffect(enabled, onShake) {
        var lastShakeTime = 0L
        val SHAKE_THRESHOLD = 2.5
        val SHAKE_COOLDOWN = 500L

        if (enabled && motionManager.accelerometerAvailable) {
            motionManager.accelerometerUpdateInterval = 0.1

            motionManager.startAccelerometerUpdatesToQueue(
                queue = NSOperationQueue.mainQueue,
                withHandler = { data: CMAccelerometerData?, error: NSError? ->
                    if (error == null) {
                        data?.acceleration?.useContents {
                            val x = this.x
                            val y = this.y
                            val z = this.z

                            val totalAcceleration = sqrt(x * x + y * y + z * z)
                            val now = getCurrentTimeMillis()

                            if (totalAcceleration > SHAKE_THRESHOLD &&
                                (now - lastShakeTime) > SHAKE_COOLDOWN
                            ) {
                                lastShakeTime = now
                                onShake()
                            }
                        }
                    }
                }
            )
        }

        onDispose {
            motionManager.stopAccelerometerUpdates()
        }
    }

    return ShakeDetector()
}