package io.github.m0nkeysan.gamekeeper.ui.strings

import androidx.compose.runtime.compositionLocalOf

/**
 * CompositionLocal for accessing localized strings throughout the Compose UI tree.
 *
 * Usage:
 * ```kotlin
 * val strings = LocalStrings.current
 * Text(strings.HOME_TITLE)
 * ```
 *
 * Default value is English strings for safety, but the App composable should
 * provide the appropriate language-specific StringProvider.
 */
val LocalStrings = compositionLocalOf<StringProvider> { AppStringsEn }
