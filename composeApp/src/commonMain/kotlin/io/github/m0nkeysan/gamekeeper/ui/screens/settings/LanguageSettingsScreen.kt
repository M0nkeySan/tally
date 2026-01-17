package io.github.m0nkeysan.gamekeeper.ui.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.m0nkeysan.gamekeeper.GameIcons
import io.github.m0nkeysan.gamekeeper.core.domain.model.AppLocale
import io.github.m0nkeysan.gamekeeper.platform.PlatformRepositories
import io.github.m0nkeysan.gamekeeper.ui.strings.LocalStrings
import kotlinx.coroutines.launch

/**
 * Language settings screen for selecting app language (locale).
 *
 * Features:
 * - Select from: English, French, or System Default
 * - Changes apply immediately with UI recomposition
 * - Selection is persisted to user preferences
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageSettingsScreen(
    onBack: () -> Unit
) {
    val strings = LocalStrings.current
    val localeManager = remember { PlatformRepositories.getLocaleManager() }
    val scope = rememberCoroutineScope()
    
    // Observe active locale changes to update UI in real-time
    val activeLocale = localeManager.getActiveLocale().collectAsState(initial = AppLocale.ENGLISH)
    
    // Track selected locale (synced with active locale)
    val selectedLocale = remember(activeLocale.value) { mutableStateOf(activeLocale.value) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(strings.SETTINGS_LANGUAGE) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(GameIcons.ArrowBack, contentDescription = strings.ACTION_BACK)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            // Language options
            AppLocale.entries.forEach { locale ->
                LanguageOption(
                    locale = locale,
                    isSelected = selectedLocale.value == locale,
                    onClick = {
                        selectedLocale.value = locale
                        scope.launch {
                            localeManager.setLocale(locale)
                        }
                    }
                )
            }
        }
    }
}

/**
 * Single language option with radio button.
 */
@Composable
private fun LanguageOption(
    locale: AppLocale,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = isSelected,
            onClick = onClick,
            modifier = Modifier.padding(end = 16.dp)
        )
        Text(
            text = locale.displayName,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
    }
}
