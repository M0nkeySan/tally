package io.github.m0nkeysan.tally.ui.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.m0nkeysan.tally.GameIcons
import io.github.m0nkeysan.tally.core.domain.model.AppLocale
import io.github.m0nkeysan.tally.core.domain.model.AppTheme
import io.github.m0nkeysan.tally.generated.resources.Res
import io.github.m0nkeysan.tally.generated.resources.action_cancel
import io.github.m0nkeysan.tally.generated.resources.cancel
import io.github.m0nkeysan.tally.generated.resources.export_error
import io.github.m0nkeysan.tally.generated.resources.export_success
import io.github.m0nkeysan.tally.generated.resources.import_confirm
import io.github.m0nkeysan.tally.generated.resources.import_error
import io.github.m0nkeysan.tally.generated.resources.import_success
import io.github.m0nkeysan.tally.generated.resources.import_warning_message
import io.github.m0nkeysan.tally.generated.resources.import_warning_title
import io.github.m0nkeysan.tally.generated.resources.settings_export_database
import io.github.m0nkeysan.tally.generated.resources.settings_export_subtitle
import io.github.m0nkeysan.tally.generated.resources.settings_import_database
import io.github.m0nkeysan.tally.generated.resources.settings_import_subtitle
import io.github.m0nkeysan.tally.generated.resources.settings_language
import io.github.m0nkeysan.tally.generated.resources.settings_section_data
import io.github.m0nkeysan.tally.generated.resources.settings_theme
import io.github.m0nkeysan.tally.generated.resources.settings_theme_dark
import io.github.m0nkeysan.tally.generated.resources.settings_theme_light
import io.github.m0nkeysan.tally.generated.resources.settings_theme_system
import io.github.m0nkeysan.tally.generated.resources.settings_title
import org.jetbrains.compose.resources.stringResource


@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = viewModel { SettingsViewModel() }
) {
    val uiState by viewModel.uiState.collectAsState()

    var showThemeDialog by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showImportWarningDialog by remember { mutableStateOf(false) }

    key(uiState.currentLocaleCode) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = stringResource(Res.string.settings_title),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    },
                    modifier = Modifier.shadow(elevation = 2.dp)
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

                Spacer(modifier = Modifier.height(24.dp))

                // Data Management Section
                Text(
                    text = stringResource(Res.string.settings_section_data),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )

                // Export Database Card
                SettingsCard(
                    icon = GameIcons.Upload,
                    title = stringResource(Res.string.settings_export_database),
                    subtitle = stringResource(Res.string.settings_export_subtitle),
                    onClick = { viewModel.exportDatabase() }
                )

                // Import Database Card
                SettingsCard(
                    icon = GameIcons.Download,
                    title = stringResource(Res.string.settings_import_database),
                    subtitle = stringResource(Res.string.settings_import_subtitle),
                    onClick = { showImportWarningDialog = true }
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

    if (showImportWarningDialog) {
        AlertDialog(
            onDismissRequest = { showImportWarningDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            },
            title = { Text(stringResource(Res.string.import_warning_title)) },
            text = { Text(stringResource(Res.string.import_warning_message)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.importDatabase()
                        showImportWarningDialog = false
                    }
                ) {
                    Text(stringResource(Res.string.import_confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { showImportWarningDialog = false }) {
                    Text(stringResource(Res.string.cancel))
                }
            }
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
