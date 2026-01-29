package io.github.m0nkeysan.tally

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalFontFamilyResolver
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.window.ComposeViewport
import io.github.m0nkeysan.tally.generated.resources.Res
import io.github.m0nkeysan.tally.generated.resources.noto_emoji
import io.github.m0nkeysan.tally.platform.PlatformRepositories
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.preloadFont

@OptIn(ExperimentalComposeUiApi::class, ExperimentalResourceApi::class)
fun main() {
    ComposeViewport("composeApplication") {
        // 1. Preload the font bytes
        val emojiFontState = preloadFont(Res.font.noto_emoji)
        val fontFamilyResolver = LocalFontFamilyResolver.current
        var fontsReady by remember { mutableStateOf(false) }

        // 2. IMPORTANT: Register the font as a fallback
        LaunchedEffect(Unit) {
            // Initialize platform repositories (Database, etc.)
            PlatformRepositories.init()

            val font = emojiFontState.value
            if (font != null) {
                // This adds the font to the global cache.
                // Skia will now automatically use it when system fonts fail.
                fontFamilyResolver.preload(FontFamily(font))
            }
            fontsReady = true
        }

        if (fontsReady) {
            App() // Your text will now be visible AND emojis will work!
        } else {
            // Loading screen...
        }
    }
}