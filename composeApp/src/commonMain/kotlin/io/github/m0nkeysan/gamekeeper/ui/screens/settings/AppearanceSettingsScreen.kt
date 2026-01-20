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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.m0nkeysan.gamekeeper.GameIcons
import io.github.m0nkeysan.gamekeeper.core.domain.model.AppTheme
import io.github.m0nkeysan.gamekeeper.platform.PlatformRepositories
import org.jetbrains.compose.resources.stringResource
import kotlinx.coroutines.launch
import io.github.m0nkeysan.gamekeeper.generated.resources.action_back
import io.github.m0nkeysan.gamekeeper.generated.resources.Res

/**
 * Appearance settings screen for selecting app theme.
 *
 * Features:
 * - Select from: Light, Dark, or System Default
 * - Changes apply immediately with UI recomposition
 * - Selection is persisted to user preferences
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppearanceSettingsScreen(
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    
    // Track selected theme
    val selectedTheme = remember { mutableStateOf(AppTheme.SYSTEM_DEFAULT) }
    
    // Load current theme preference on screen enter
    LaunchedEffect(Unit) {
        PlatformRepositories.getUserPreferencesRepository().getTheme().collect { theme ->
            selectedTheme.value = theme
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Theme") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(GameIcons.ArrowBack, contentDescription = stringResource(Res.string.action_back))
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
            // Theme options
            AppTheme.entries.forEach { theme ->
                ThemeOption(
                    theme = theme,
                    isSelected = selectedTheme.value == theme,
                    onClick = {
                        selectedTheme.value = theme
                        scope.launch {
                            PlatformRepositories.getUserPreferencesRepository().saveTheme(theme)
                        }
                    }
                )
            }
        }
    }
}

/**
 * Single theme option with radio button.
 */
@Composable
private fun ThemeOption(
    theme: AppTheme,
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
            text = theme.displayName,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
    }
}
