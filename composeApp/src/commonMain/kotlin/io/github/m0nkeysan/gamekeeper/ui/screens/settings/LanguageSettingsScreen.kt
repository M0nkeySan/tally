package io.github.m0nkeysan.gamekeeper.ui.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import io.github.m0nkeysan.gamekeeper.core.domain.model.AppLocale
import io.github.m0nkeysan.gamekeeper.ui.strings.LocaleManager
import org.jetbrains.compose.resources.stringResource
import io.github.m0nkeysan.gamekeeper.generated.resources.settings_language
import io.github.m0nkeysan.gamekeeper.generated.resources.Res

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageSettingsScreen(
    onBack: () -> Unit
) {
    val localeManager = remember { LocaleManager.instance }
    val currentLocale by localeManager.currentLocale.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.settings_language)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            items(AppLocale.entries.size) { index ->
                val language = AppLocale.entries[index]
                LanguageItem(
                    language = language,
                    isSelected = currentLocale == language.code,
                    onClick = {
                        localeManager.setLocale(language.code)
                    }
                )
            }
        }
    }
}

@Composable
private fun LanguageItem(
    language: AppLocale,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    ListItem(
        headlineContent = { Text(language.displayName) },
        trailingContent = {
            if (isSelected) {
                Icon(Icons.Default.Check, "Selected")
            }
        },
        modifier = Modifier.clickable(onClick = onClick)
    )
    HorizontalDivider()
}