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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.m0nkeysan.gamekeeper.GameIcons
import org.jetbrains.compose.resources.stringResource
import io.github.m0nkeysan.gamekeeper.generated.resources.action_back
import io.github.m0nkeysan.gamekeeper.generated.resources.action_retry
import io.github.m0nkeysan.gamekeeper.generated.resources.settings_language
import io.github.m0nkeysan.gamekeeper.generated.resources.Res

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
                     title = { Text(stringResource(Res.string.settings_language)) },
                     navigationIcon = {
                         IconButton(onClick = onBack) {
                             Icon(GameIcons.ArrowBack, contentDescription = stringResource(Res.string.action_back))
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
                     SettingsSectionHeader(stringResource(Res.string.action_retry))
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
                         title = stringResource(Res.string.settings_language),
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
