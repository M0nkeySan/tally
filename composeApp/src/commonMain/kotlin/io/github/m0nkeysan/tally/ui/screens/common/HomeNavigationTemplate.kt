package io.github.m0nkeysan.tally.ui.screens.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.m0nkeysan.tally.GameIcons
import io.github.m0nkeysan.tally.core.navigation.Route
import io.github.m0nkeysan.tally.generated.resources.Res
import io.github.m0nkeysan.tally.generated.resources.cd_add_player
import io.github.m0nkeysan.tally.generated.resources.cd_settings
import io.github.m0nkeysan.tally.generated.resources.home_cd_games
import io.github.m0nkeysan.tally.generated.resources.home_cd_players
import io.github.m0nkeysan.tally.ui.components.AppSnackbarHost
import io.github.m0nkeysan.tally.ui.components.showErrorSnackbar
import io.github.m0nkeysan.tally.ui.components.showSuccessSnackbar
import io.github.m0nkeysan.tally.ui.screens.home.HomeScreen
import io.github.m0nkeysan.tally.ui.screens.home.HomeViewModel
import io.github.m0nkeysan.tally.ui.screens.player.PlayerManagementScreen
import io.github.m0nkeysan.tally.ui.screens.player.PlayerSelectionViewModel
import io.github.m0nkeysan.tally.ui.screens.settings.SettingsScreen
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

/**
 * Navigation template for the home screen with bottom bar for switching between Games, Players, and Settings tabs.
 * Only appears on the home screen to manage navigation between game selection, player management, and settings.
 *
 * Features:
 * - Bottom navigation bar with Games, Players, and Settings tabs (icons only, no labels)
 * - Conditional rendering of HomeScreen (Games tab), PlayerSelectionScreen (Players tab), and SettingsScreen (Settings tab)
 * - Active tab highlighted in primary color
 * - Persists selected tab state during session
 *
 * @param onNavigateTo Callback for navigating to game screens from HomeScreen
 * @param homeViewModel ViewModel for home screen state
 * @param playerViewModel ViewModel for player selection state
 * @param modifier Optional layout modifier
 *
 */
@Composable
fun HomeNavigationTemplate(
    onNavigateTo: (Route) -> Unit,
    modifier: Modifier = Modifier,
    homeViewModel: HomeViewModel = viewModel { HomeViewModel() },
    playerViewModel: PlayerSelectionViewModel = viewModel { PlayerSelectionViewModel() },
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val onShowSnackbar: (String, Boolean) -> Unit = { message, isError ->
        scope.launch {
            if (isError) {
                showErrorSnackbar(snackbarHostState, message)
            } else {
                showSuccessSnackbar(snackbarHostState, message)
            }
        }
    }

    BackHandler(enabled = selectedTab == 1 || selectedTab == 2) {
        selectedTab = 0
    }

    var showAddPlayerDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier,
        snackbarHost = { AppSnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            Column {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.shadow(elevation = 2.dp)
                ) {
                    // Games Tab
                    NavigationBarItem(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        icon = {
                            Icon(
                                imageVector = GameIcons.GridView,
                                contentDescription = stringResource(Res.string.home_cd_games)
                            )
                        },
                        alwaysShowLabel = false
                    )

                    // Players Tab
                    NavigationBarItem(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        icon = {
                            Icon(
                                imageVector = GameIcons.Group,
                                contentDescription = stringResource(Res.string.home_cd_players)
                            )
                        },
                        alwaysShowLabel = false
                    )

                    // Settings Tab
                    NavigationBarItem(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        icon = {
                            Icon(
                                imageVector = GameIcons.Settings,
                                contentDescription = stringResource(Res.string.cd_settings)
                            )
                        },
                        alwaysShowLabel = false
                    )
                }
            }
        },
        floatingActionButton = {
            if (selectedTab == 1) {
                FloatingActionButton(
                    onClick = { showAddPlayerDialog = true },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = stringResource(Res.string.cd_add_player)
                    )
                }
            }
        }
    ) { paddingValues ->

        val childModifier = Modifier.padding(bottom = paddingValues.calculateBottomPadding())

        when (selectedTab) {
            0 -> {
                HomeScreen(
                    onNavigateTo = onNavigateTo,
                    viewModel = homeViewModel,
                    modifier = childModifier
                )
            }

            1 -> {
                PlayerManagementScreen(
                    viewModel = playerViewModel,
                    triggerAddDialog = showAddPlayerDialog,
                    onAddDialogHandled = { showAddPlayerDialog = false },
                    modifier = childModifier,
                    onShowSnackbar = onShowSnackbar
                )
            }

            2 -> {
                SettingsScreen()
            }
        }
    }
}
