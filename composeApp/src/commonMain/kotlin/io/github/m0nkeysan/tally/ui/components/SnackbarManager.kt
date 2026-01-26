package io.github.m0nkeysan.tally.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.runtime.Composable

/**
 * Standard Snackbar host configuration for the app.
 * - Duration: 5 seconds
 * - Position: Bottom center
 * - Action: Dismiss only (no retry)
 */
@Composable
fun AppSnackbarHost(
    hostState: SnackbarHostState,
) {
    SnackbarHost(hostState) { data ->
        val customVisuals = data.visuals as? AppSnackbarVisuals
        val isError = customVisuals?.type == SnackbarType.Error

        val bgColor =
            if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary

        Snackbar(
            snackbarData = data,
            containerColor = bgColor,
            contentColor = MaterialTheme.colorScheme.surface
        )
    }
}

enum class SnackbarType { Error, Success }

class AppSnackbarVisuals(
    override val message: String,
    val type: SnackbarType,
    override val actionLabel: String? = null,
    override val withDismissAction: Boolean = false,
    override val duration: SnackbarDuration = SnackbarDuration.Short
) : SnackbarVisuals

/**
 * Helper function to show error message in Snackbar.
 * Auto-dismisses after 5 seconds.
 */
suspend fun showErrorSnackbar(
    hostState: SnackbarHostState,
    message: String
) {
    hostState.showSnackbar(
        AppSnackbarVisuals(
            message = message,
            type = SnackbarType.Error,
            withDismissAction = true,
            duration = SnackbarDuration.Long
        )
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
        AppSnackbarVisuals(
            message = message,
            type = SnackbarType.Success,
            withDismissAction = false,
            duration = SnackbarDuration.Short
        )
    )
}
