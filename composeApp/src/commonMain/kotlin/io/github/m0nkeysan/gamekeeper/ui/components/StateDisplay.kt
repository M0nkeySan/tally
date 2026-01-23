package io.github.m0nkeysan.gamekeeper.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.m0nkeysan.gamekeeper.generated.resources.Res
import io.github.m0nkeysan.gamekeeper.generated.resources.action_retry
import io.github.m0nkeysan.gamekeeper.generated.resources.state_error_title
import io.github.m0nkeysan.gamekeeper.generated.resources.state_loading
import io.github.m0nkeysan.gamekeeper.generated.resources.state_loading_games
import org.jetbrains.compose.resources.stringResource

/**
 * Loading state display with centered spinner and message.
 * Used to show content is being loaded from the network or database.
 *
 * @param message Text message to display below the spinner (e.g., "Loading games...")
 * @param modifier Optional modifier for layout customization
 *
 * Example usage:
 * ```
 * if (isLoading) {
 *     LoadingState(message = "Loading games...")
 * }
 * ```
 */
@Composable
fun LoadingState(
    modifier: Modifier = Modifier,
    message: String = stringResource(Res.string.state_loading),
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Empty state display with icon, message, and optional action button.
 * Used when a list or collection is empty.
 *
 * @param icon Icon vector to display (default: Folder icon)
 * @param message Main message explaining the empty state
 * @param actionLabel Optional label for the action button (e.g., "Create Game")
 * @param onAction Optional callback when action button is clicked
 * @param modifier Optional modifier for layout customization
 *
 * Example usage:
 * ```
 * if (games.isEmpty()) {
 *     EmptyState(
 *         message = "No games yet. Create one!",
 *         actionLabel = "Create Game",
 *         onAction = { viewModel.createGame() }
 *     )
 * }
 * ```
 */
@Composable
fun EmptyState(
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Default.Folder,
    message: String,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            if (actionLabel != null && onAction != null) {
                Button(
                    onClick = onAction,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(actionLabel)
                }
            }
        }
    }
}

/**
 * Error state display with error icon, message, and optional retry button.
 * Used to display errors from failed network or database operations.
 *
 * @param message Error message to display to the user
 * @param onRetry Optional callback when retry button is clicked
 * @param modifier Optional modifier for layout customization
 *
 * Example usage:
 * ```
 * if (state.error != null) {
 *     ErrorState(
 *         message = state.error,
 *         onRetry = { viewModel.retry() }
 *     )
 * }
 * ```
 */
@Composable
fun ErrorState(
    message: String,
    modifier: Modifier = Modifier,
    onRetry: (() -> Unit)? = null
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 400.dp)
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Error,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.error
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = stringResource(Res.string.state_error_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )
            }

            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }

        if (onRetry != null) {
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text(stringResource(Res.string.action_retry))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LoadingStatePreview() {
    LoadingState(message = stringResource(Res.string.state_loading_games))
}

@Preview(showBackground = true)
@Composable
private fun EmptyStatePreview() {
    EmptyState(
        message = "No games yet",
        actionLabel = "Create Game",
        onAction = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun ErrorStatePreview() {
    ErrorState(
        message = "Failed to load games",
        onRetry = {}
    )
}
