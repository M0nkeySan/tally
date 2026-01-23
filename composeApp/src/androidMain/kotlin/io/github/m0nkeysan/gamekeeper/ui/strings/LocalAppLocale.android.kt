package io.github.m0nkeysan.gamekeeper.ui.strings

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidedValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import java.util.Locale

/**
 * Android implementation of LocalAppLocale.
 * 
 * This implementation updates Android's Configuration to change the app's language.
 * It modifies both Locale.setDefault() and resources.updateConfiguration() to ensure
 * the new locale is applied at both the JVM level and Android resource loading level.
 * 
 * This ensures that stringResource() calls resolve to the correct language on Android.
 * 
 * Reference: https://www.jetbrains.com/help/kotlin-multiplatform-dev/compose-resource-environment.html#locale
 */
actual object LocalAppLocale {
    /**
     * Stores the original system default locale to allow reset.
     */
    private var default: Locale? = null
    
    /**
     * Gets the current locale from the JVM default.
     * This is read from Locale.getDefault() which we update when locale changes.
     */
    actual val current: String
        @Composable get() = Locale.getDefault().toString()
    
    /**
     * Updates the Android locale and provides it to the composition tree.
     * 
     * This method does THREE critical things for Android:
     * 1. Updates Locale.setDefault() → JVM-level locale
     * 2. Updates Configuration.setLocale() → Android configuration
     * 3. Calls resources.updateConfiguration() → Forces resource reload
     * 
     * @param value The locale code (e.g., "en", "fr") or null to reset to system default
     * @return ProvidedValue for use with CompositionLocalProvider
     */
    @Composable
    actual infix fun provides(value: String?): ProvidedValue<*> {
        val configuration = LocalConfiguration.current
        
        // Capture the original default locale on first call
        if (default == null) {
            default = Locale.getDefault()
        }
        
        // Parse the locale code and create a Locale object
        val new = when (value) {
            null -> default!! // Reset to original system default
            else -> {
                // Parse language code (e.g., "en", "fr", "en-US")
                val parts = value.split("_", "-")
                val builder = Locale.Builder().setLanguage(parts[0])
                
                // Add region if provided (e.g., "en-US" -> language=en, region=US)
                if (parts.size > 1) {
                    builder.setRegion(parts[1])
                }
                
                builder.build()
            }
        }
        
        println("LocalAppLocale.Android: Setting locale to '$new'")
        
        // Update JVM default locale
        Locale.setDefault(new)
        
        // Update Android configuration
        configuration.setLocale(new)
        configuration.setLayoutDirection(new) // Also update layout direction for RTL languages
        
        // Force Android resources to reload with new configuration
        val resources = LocalContext.current.resources
        @Suppress("DEPRECATION") // This is the correct way for in-app locale changes
        resources.updateConfiguration(configuration, resources.displayMetrics)
        
        // Provide the updated configuration to the composition tree
        return LocalConfiguration.provides(configuration)
    }
}
