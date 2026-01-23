package io.github.m0nkeysan.gamekeeper.ui.strings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidedValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.InternalComposeUiApi
import platform.Foundation.NSLocale
import platform.Foundation.NSLocaleLanguageCode
import platform.Foundation.NSUserDefaults
import platform.Foundation.currentLocale

/**
 * iOS implementation of LocalAppLocale.
 * 
 * This implementation updates NSUserDefaults with the "AppleLanguages" key,
 * which is the official iOS way to change the app's preferred language at runtime.
 * 
 * When "AppleLanguages" is set in NSUserDefaults, iOS's resource loading system
 * (which Compose Multiplatform's stringResource() uses) will prioritize the
 * specified language for resource resolution.
 * 
 * This is the KEY mechanism that makes dynamic language switching work on iOS.
 * 
 * Reference: https://www.jetbrains.com/help/kotlin-multiplatform-dev/compose-resource-environment.html#locale
 */
@OptIn(InternalComposeUiApi::class)
actual object LocalAppLocale {
    /**
     * NSUserDefaults key for preferred languages.
     * This is the standard iOS key for app-specific language preferences.
     */
    private const val LANG_KEY = "AppleLanguages"
    
    /**
     * The system's default preferred language.
     * Retrieved from NSLocale.currentLocale at app startup.
     */
    private val default: String = run {
        val locale = NSLocale.currentLocale
        locale.objectForKey(NSLocaleLanguageCode) as? String ?: "en"
    }
    
    /**
     * CompositionLocal for providing the current locale to the composition tree.
     */
    private val LocalAppLocaleCompositionLocal = staticCompositionLocalOf { default }
    
    /**
     * Gets the current locale code from the composition.
     */
    actual val current: String
        @Composable get() = LocalAppLocaleCompositionLocal.current
    
    /**
     * Updates the iOS preferred language and provides it to the composition tree.
     * 
     * This method does TWO critical things:
     * 1. Updates NSUserDefaults.standardUserDefaults with "AppleLanguages" key
     *    → This tells iOS to load resources for the specified language
     * 2. Provides the locale to Compose via CompositionLocalProvider
     *    → This makes the value available to composables
     * 
     * @param value The language code (e.g., "en", "fr") or null to reset to system default
     * @return ProvidedValue for use with CompositionLocalProvider
     */
    @Composable
    actual infix fun provides(value: String?): ProvidedValue<*> {
        val new = value ?: default
        
        println("LocalAppLocale.iOS: Setting iOS preferred language to '$new'")
        
        if (value == null) {
            // Reset to system default by removing custom language preference
            NSUserDefaults.standardUserDefaults.removeObjectForKey(LANG_KEY)
            println("LocalAppLocale.iOS: Removed custom language, using system default")
        } else {
            // Set custom language preference
            // CRITICAL: This must be a List/Array with the language code
            // iOS will use this to determine which .lproj bundle to load resources from
            NSUserDefaults.standardUserDefaults.setObject(listOf(new), LANG_KEY)
            println("LocalAppLocale.iOS: Set AppleLanguages to [$new]")
        }
        
        // Provide the locale to the Compose composition tree
        return LocalAppLocaleCompositionLocal.provides(new)
    }
}
