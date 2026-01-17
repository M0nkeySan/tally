package io.github.m0nkeysan.gamekeeper.ui.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.m0nkeysan.gamekeeper.GameIcons
import io.github.m0nkeysan.gamekeeper.ui.strings.LocalStrings

/**
 * Main settings screen containing all app settings.
 *
 * Sections:
 * - Appearance (Theme: Light, Dark, System Default)
 * - Language (Language: English, French, System Default)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit
) {
    val strings = LocalStrings.current
    val showAppearanceSettings = remember { mutableStateOf(false) }
    val showLanguageSettings = remember { mutableStateOf(false) }

    if (showAppearanceSettings.value) {
        AppearanceSettingsScreen(
            onBack = { showAppearanceSettings.value = false }
        )
    } else if (showLanguageSettings.value) {
        LanguageSettingsScreen(
            onBack = { showLanguageSettings.value = false }
        )
    } else {
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
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                // Appearance Section
                item {
                    SettingsSectionHeader("Appearance")
                }
                item {
                    SettingsOption(
                        title = "Theme",
                        onClick = { showAppearanceSettings.value = true }
                    )
                }
                item {
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                }

                // Language Section
                item {
                    SettingsSectionHeader("Language")
                }
                item {
                    SettingsOption(
                        title = strings.SETTINGS_LANGUAGE,
                        onClick = { showLanguageSettings.value = true }
                    )
                }
            }
        }
    }
}

/**
 * Settings section header.
 */
@Composable
private fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    )
}

/**
 * Reusable settings option item.
 */
@Composable
private fun SettingsOption(
    title: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium
        )
    }
}
