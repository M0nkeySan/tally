package io.github.m0nkeysan.gamekeeper.ui.screens.common

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.m0nkeysan.gamekeeper.GameIcons
import io.github.m0nkeysan.gamekeeper.ui.components.GameKeeperSnackbarHost
import io.github.m0nkeysan.gamekeeper.ui.components.showErrorSnackbar
import io.github.m0nkeysan.gamekeeper.ui.theme.GameColors

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
 *     onBack = { navController.popBackStack() },
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
@OptIn(ExperimentalMaterial3Api::class)
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
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(GameIcons.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        snackbarHost = {
            GameKeeperSnackbarHost(hostState = snackbarHostState)
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
                        Text("Cancel")
                    }
                    
                    Button(
                        onClick = onCreate,
                        enabled = canCreate,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GameColors.Primary,
                            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    ) {
                        Text("Create Game")
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
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
