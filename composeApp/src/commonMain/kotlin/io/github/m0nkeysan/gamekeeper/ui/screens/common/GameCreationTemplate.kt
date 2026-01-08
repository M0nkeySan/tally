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
 * Provides consistent layout with scrollable content and action buttons.
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
                            disabledContainerColor = GameColors.Surface2
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
