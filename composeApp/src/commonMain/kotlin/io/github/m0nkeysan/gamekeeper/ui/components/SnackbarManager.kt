package io.github.m0nkeysan.gamekeeper.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.m0nkeysan.gamekeeper.ui.theme.GameColors

/**
 * Standard Snackbar host configuration for GameKeeper.
 * - Duration: 5 seconds
 * - Position: Bottom center
 * - Action: Dismiss only (no retry)
 */
@Composable
fun GameKeeperSnackbarHost(
    hostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    SnackbarHost(
        hostState = hostState,
        modifier = modifier
    ) { data ->
        Snackbar(
            snackbarData = data,
            containerColor = GameColors.Error,
            contentColor = GameColors.Surface0,
            actionColor = GameColors.Surface0
        )
    }
}

/**
 * Helper function to show error message in Snackbar.
 * Auto-dismisses after 5 seconds.
 */
suspend fun showErrorSnackbar(
    hostState: SnackbarHostState,
    message: String
) {
    hostState.showSnackbar(
        message = message,
        duration = SnackbarDuration.Long, // 5 seconds
        withDismissAction = true
    )
}

/**
 * Helper function to show success message in Snackbar.
 * Auto-dismisses after 3 seconds.
 */
suspend fun showSuccessSnackbar(
    hostState: SnackbarHostState,
    message: String
) {
    hostState.showSnackbar(
        message = message,
        duration = SnackbarDuration.Short, // 3 seconds
        withDismissAction = false
    )
}
