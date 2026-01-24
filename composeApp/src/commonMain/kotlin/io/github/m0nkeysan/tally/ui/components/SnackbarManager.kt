package io.github.m0nkeysan.tally.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Standard Snackbar host configuration for the app.
 * - Duration: 5 seconds
 * - Position: Bottom center
 * - Action: Dismiss only (no retry)
 */
@Composable
fun AppSnackbarHost(
    hostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    SnackbarHost(
        hostState = hostState,
        modifier = modifier
    ) { data ->
        Snackbar(
            snackbarData = data,
            containerColor = MaterialTheme.colorScheme.error,
            contentColor = MaterialTheme.colorScheme.surface,
            actionColor = MaterialTheme.colorScheme.surface
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
