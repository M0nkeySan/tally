package io.github.m0nkeysan.tally.ui.screens.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import io.github.m0nkeysan.tally.GameIcons
import io.github.m0nkeysan.tally.generated.resources.Res
import io.github.m0nkeysan.tally.generated.resources.action_back
import io.github.m0nkeysan.tally.generated.resources.action_cancel
import io.github.m0nkeysan.tally.generated.resources.game_creation_action_create
import io.github.m0nkeysan.tally.ui.components.AppSnackbarHost
import io.github.m0nkeysan.tally.ui.components.showErrorSnackbar
import org.jetbrains.compose.resources.stringResource

/**
 * Reusable template for game creation screens.
 * Eliminates code duplication between Tarot and Yahtzee game creation screens.
 *
 * Features:
 * - Scrollable content area for form inputs
 * - Bottom sticky action bar with Cancel and Create buttons
 * - Error feedback via Snackbar notifications
 * - Conditional enable/disable of Create button based on form validity
 * - Consistent flat design with GameColors theme
 *
 * @param title Title displayed in TopAppBar
 * @param onBack Callback for back navigation
 * @param onCreate Callback when Create button is clicked
 * @param canCreate Whether Create button should be enabled
 * @param error Error message to display, if any
 * @param content Lambda for form content (player selector, name field, etc.)
 * @param modifier Optional layout modifier
 *
 * Example usage:
 * ```
 * GameCreationTemplate(
 *     title = "New Tarot Game",
 *     onBack = {
                    if (navController.currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED) {
                        navController.popBackStack()
                    }
                },
 *     onCreate = { viewModel.createGame() },
 *     canCreate = gameName.isNotBlank() && players.size == playerCount,
 *     error = state.error,
 *     content = {
 *         OutlinedTextField(value = gameName, onValueChange = { gameName = it })
 *         FlexiblePlayerSelector(...)
 *     }
 * )
 * ```
 */
@Composable
fun GameCreationTemplate(
    title: String,
    onBack: () -> Unit,
    onCreate: () -> Unit,
    canCreate: Boolean,
    error: String?,
    content: @Composable ColumnScope.() -> Unit,
    modifier: Modifier = Modifier
) {
    val snackbarHostState = remember { SnackbarHostState() }

    // Show error in Snackbar
    LaunchedEffect(error) {
        if (error != null) {
            showErrorSnackbar(snackbarHostState, error)
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(title)
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            GameIcons.ArrowBack,
                            contentDescription = stringResource(Res.string.action_back)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier.shadow(elevation = 2.dp)
            )
        },
        snackbarHost = {
            AppSnackbarHost(hostState = snackbarHostState)
        },
        bottomBar = {
            Surface(
                tonalElevation = 3.dp,
                shadowElevation = 0.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onBack,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(stringResource(Res.string.action_cancel))
                    }

                    Button(
                        onClick = onCreate,
                        enabled = canCreate,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    ) {
                        Text(stringResource(Res.string.game_creation_action_create))
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))
                content()
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}
