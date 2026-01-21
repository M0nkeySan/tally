package io.github.m0nkeysan.gamekeeper

import android.content.Context
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import io.github.m0nkeysan.gamekeeper.platform.PlatformRepositories
import io.github.m0nkeysan.gamekeeper.ui.strings.LocaleManager
import kotlinx.coroutines.launch
import java.util.Locale

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(android.graphics.Color.TRANSPARENT, android.graphics.Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.auto(android.graphics.Color.TRANSPARENT, android.graphics.Color.TRANSPARENT)
        )

        PlatformRepositories.init(applicationContext)

        lifecycleScope.launch {
            LocaleManager.instance.currentLocale.collect { languageCode ->
                val currentConfig = resources.configuration
                val currentLanguage = currentConfig.locales[0].language

                if (currentLanguage != languageCode) {
                    updateAndroidLocale(this@MainActivity, languageCode)
                }
            }
        }

        setContent {
            App()
        }
    }

    private fun updateAndroidLocale(context: Context, languageCode: String) {
        val parts = languageCode.split("_", "-")
        val localeBuilder = Locale.Builder().setLanguage(parts[0])
        
        if (parts.size >= 2 && parts[1].isNotEmpty()) {
            localeBuilder.setRegion(parts[1])
        }
        if (parts.size >= 3 && parts[2].isNotEmpty()) {
            localeBuilder.setVariant(parts[2])
        }
        
        val locale = localeBuilder.build()
        Locale.setDefault(locale)

        val resources = context.resources
        val configuration = resources.configuration
        configuration.setLocale(locale)
        configuration.setLayoutDirection(locale)

        @Suppress("DEPRECATION")
        resources.updateConfiguration(configuration, resources.displayMetrics)
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}