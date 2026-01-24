package io.github.m0nkeysan.tally.platform

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import kotlin.math.sqrt

/**
 * Android implementation of shake detection using accelerometer
 */
actual class ShakeDetector

/**
 * Android shake detection using SensorManager and accelerometer
 * 
 * Shake detection parameters:
 * - SHAKE_THRESHOLD: 12f - Reasonable default, prevents false positives
 * - SHAKE_COOLDOWN: 500ms - Prevents multiple roll triggers from single shake
 * - Sensor: TYPE_ACCELEROMETER - Standard accelerometer
 * - Frequency: SENSOR_DELAY_NORMAL - Balanced between responsiveness and battery
 */
@Composable
actual fun rememberShakeDetector(
    onShake: () -> Unit,
    enabled: Boolean
): ShakeDetector {
    val context = LocalContext.current
    val sensorManager = remember { 
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager 
    }
    val accelerometer = remember { 
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) 
    }
    
    DisposableEffect(enabled, onShake) {
        if (enabled && accelerometer != null) {
            val listener = object : SensorEventListener {
                private var lastShakeTime = 0L
                private val SHAKE_THRESHOLD = 12f // Acceleration threshold in m/s²
                private val SHAKE_COOLDOWN = 500L // Milliseconds between shake events
                
                override fun onSensorChanged(event: SensorEvent) {
                    val x = event.values[0]
                    val y = event.values[1]
                    val z = event.values[2]
                    
                    // Calculate acceleration magnitude minus gravity
                    val acceleration = sqrt(x * x + y * y + z * z) - SensorManager.GRAVITY_EARTH
                    val now = System.currentTimeMillis()
                    
                    // Trigger callback only if threshold exceeded and cooldown elapsed
                    if (acceleration > SHAKE_THRESHOLD && 
                        (now - lastShakeTime) > SHAKE_COOLDOWN) {
                        lastShakeTime = now
                        onShake()
                    }
                }
                
                override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
            }
            
            // Register accelerometer listener
            sensorManager.registerListener(
                listener, 
                accelerometer, 
                SensorManager.SENSOR_DELAY_NORMAL
            )
            
            // Unregister listener when composable leaves composition
            onDispose { 
                sensorManager.unregisterListener(listener)
            }
        } else {
            onDispose { }
        }
    }
    
    return ShakeDetector()
}
