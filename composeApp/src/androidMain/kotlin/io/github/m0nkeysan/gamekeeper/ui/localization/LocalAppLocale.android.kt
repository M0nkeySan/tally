package io.github.m0nkeysan.gamekeeper.ui.localization

import android.content.res.Configuration
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import java.util.Locale

/**
 * Android-specific implementation of LocalAppLocale.
 * Uses Android's Configuration API to update the device locale.
 */
actual object LocalAppLocale {
    private var default: Locale? = null

    /**
     * Returns the current locale as a string (e.g., "en_US", "fr_FR")
     */
    actual val current: String
        @Composable get() = Locale.getDefault().toString()

    /**
     * Provides the given locale to the composition.
     * If value is null, reverts to the device's default locale.
     */
    @Composable
    actual infix fun provides(value: String?): ProvidedValue<*> {
        val configuration = LocalConfiguration.current

        if (default == null) {
            default = Locale.getDefault()
        }

        val new = when (value) {
            null -> default!!
            else -> {
                // Parse locale string: "en" or "en_US"
                val parts = value.split("_", "-")
                when (parts.size) {
                    1 -> Locale(parts[0])
                    2 -> Locale(parts[0], parts[1])
                    else -> Locale(parts[0], parts[1], parts[2])
                }
            }
        }

        // Set the JVM default locale
        Locale.setDefault(new)

        // Update Android Configuration
        configuration.setLocale(new)

        // Update resources configuration
        val resources = LocalContext.current.resources
        resources.updateConfiguration(configuration, resources.displayMetrics)

        return LocalConfiguration.provides(configuration)
    }
}
