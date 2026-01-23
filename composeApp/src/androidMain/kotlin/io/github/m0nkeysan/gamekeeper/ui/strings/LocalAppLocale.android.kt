package io.github.m0nkeysan.gamekeeper.ui.strings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidedValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalResources
import java.util.Locale

actual object LocalAppLocale {
    private var default: Locale? = null

    actual val current: String
        @Composable get() = Locale.getDefault().toString()

    @Composable
    actual infix fun provides(value: String?): ProvidedValue<*> {
        val configuration = LocalConfiguration.current

        if (default == null) {
            default = Locale.getDefault()
        }

        val new = when (value) {
            null -> default!!
            else -> {
                val parts = value.split("_", "-")
                val builder = Locale.Builder().setLanguage(parts[0])

                if (parts.size > 1) {
                    builder.setRegion(parts[1])
                }
                
                builder.build()
            }
        }

        Locale.setDefault(new)

        configuration.setLocale(new)
        configuration.setLayoutDirection(new)

        val resources = LocalResources.current
        resources.updateConfiguration(configuration, resources.displayMetrics)

        return LocalConfiguration.provides(configuration)
    }
}
