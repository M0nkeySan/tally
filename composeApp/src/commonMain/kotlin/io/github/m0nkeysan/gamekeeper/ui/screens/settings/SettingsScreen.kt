package io.github.m0nkeysan.gamekeeper.ui.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.m0nkeysan.gamekeeper.GameIcons
import io.github.m0nkeysan.gamekeeper.core.domain.model.AppLocale
import io.github.m0nkeysan.gamekeeper.core.domain.model.AppTheme
import io.github.m0nkeysan.gamekeeper.generated.resources.Res
import io.github.m0nkeysan.gamekeeper.generated.resources.action_cancel
import io.github.m0nkeysan.gamekeeper.generated.resources.settings_language
import io.github.m0nkeysan.gamekeeper.generated.resources.settings_theme
import io.github.m0nkeysan.gamekeeper.generated.resources.settings_theme_dark
import io.github.m0nkeysan.gamekeeper.generated.resources.settings_theme_light
import io.github.m0nkeysan.gamekeeper.generated.resources.settings_theme_system
import io.github.m0nkeysan.gamekeeper.generated.resources.settings_title
import org.jetbrains.compose.resources.stringResource


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    var showThemeDialog by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }

    key(uiState.currentLocaleCode) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text(stringResource(Res.string.settings_title)) }
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(top = 16.dp)
            ) {
                // Theme Card
                SettingsCard(
                    icon = GameIcons.Brightness4,
                    title = stringResource(Res.string.settings_theme),
                    subtitle = getThemeDisplayName(uiState.currentTheme),
                    onClick = { showThemeDialog = true }
                )

                // Language Card
                SettingsCard(
                    icon = GameIcons.Translate,
                    title = stringResource(Res.string.settings_language),
                    subtitle = AppLocale.fromCode(uiState.currentLocaleCode).displayName,
                    onClick = { showLanguageDialog = true }
                )
            }
        }
    }

    if (showThemeDialog) {
        SelectionDialog(
            title = stringResource(Res.string.settings_theme),
            options = AppTheme.entries,
            currentSelection = uiState.currentTheme,
            onDismiss = { showThemeDialog = false },
            onOptionSelected = {
                viewModel.setTheme(it)
                showThemeDialog = false
            },
            labelProvider = { getThemeDisplayName(it) }
        )
    }

    if (showLanguageDialog) {
        SelectionDialog(
            title = stringResource(Res.string.settings_language),
            options = AppLocale.entries,
            currentSelection = AppLocale.fromCode(uiState.currentLocaleCode),
            onDismiss = { showLanguageDialog = false },
            onOptionSelected = {
                viewModel.setLocale(it.code)
                showLanguageDialog = false
            },
            labelProvider = { it.displayName }
        )
    }
}

// Helper to map enum to string resource
@Composable
private fun getThemeDisplayName(theme: AppTheme): String {
    return when (theme) {
        AppTheme.LIGHT -> stringResource(Res.string.settings_theme_light)
        AppTheme.DARK -> stringResource(Res.string.settings_theme_dark)
        AppTheme.SYSTEM_DEFAULT -> stringResource(Res.string.settings_theme_system)
    }
}

/**
 * Generic Selection Dialog to replace duplicate code for Theme/Language
 */
@Composable
private fun <T> SelectionDialog(
    title: String,
    options: List<T>,
    currentSelection: T,
    onDismiss: () -> Unit,
    onOptionSelected: (T) -> Unit,
    labelProvider: @Composable (T) -> String
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column {
                options.forEach { option ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOptionSelected(option) }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = option == currentSelection,
                            onClick = { onOptionSelected(option) }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = labelProvider(option),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(Res.string.action_cancel))
            }
        }
    )
}

@Composable
private fun SettingsCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp), // Standard icon size
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}