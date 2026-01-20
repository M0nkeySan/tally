package io.github.m0nkeysan.gamekeeper.ui.screens.common

import androidx.activity.compose.BackHandler
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.m0nkeysan.gamekeeper.GameIcons
import io.github.m0nkeysan.gamekeeper.ui.screens.home.HomeScreen
import io.github.m0nkeysan.gamekeeper.ui.screens.home.HomeViewModel
import io.github.m0nkeysan.gamekeeper.ui.screens.player.PlayerSelectionScreen
import io.github.m0nkeysan.gamekeeper.ui.screens.player.PlayerSelectionViewModel
import io.github.m0nkeysan.gamekeeper.ui.screens.settings.SettingsScreen
import org.jetbrains.compose.resources.stringResource
import io.github.m0nkeysan.gamekeeper.generated.resources.cd_add_player
import io.github.m0nkeysan.gamekeeper.generated.resources.cd_settings
import io.github.m0nkeysan.gamekeeper.generated.resources.home_cd_games
import io.github.m0nkeysan.gamekeeper.generated.resources.home_cd_players
import io.github.m0nkeysan.gamekeeper.generated.resources.Res

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
 * Example usage:
 * ```
 * HomeNavigationTemplate(
 *     onNavigateTo = { navController.navigate(it) },
 *     homeViewModel = viewModel(),
 *     playerViewModel = viewModel()
 * )
 * ```
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeNavigationTemplate(
    onNavigateTo: (String) -> Unit,
    modifier: Modifier = Modifier,
    homeViewModel: HomeViewModel = viewModel { HomeViewModel() },
    playerViewModel: PlayerSelectionViewModel = viewModel { PlayerSelectionViewModel() },
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    // Handle system back gesture - go to home screen when on players or settings tab
    BackHandler(enabled = selectedTab == 1 || selectedTab == 2) {
        selectedTab = 0
    }

    var showAddPlayerDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier,
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
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
        },
        floatingActionButton = {
            if (selectedTab == 1) {
                FloatingActionButton(
                    onClick = { showAddPlayerDialog = true },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(Icons.Default.Add, contentDescription = stringResource(Res.string.cd_add_player))
                }
            }
        }
    ) { paddingValues ->
        when (selectedTab) {
             0 -> {
                 HomeScreen(
                     onNavigateTo = onNavigateTo,
                     viewModel = homeViewModel,
                     modifier = Modifier
                 )
             }
             1 -> {
                 PlayerSelectionScreen(
                     onBack = { /* No-op: staying in tab */ },
                     viewModel = playerViewModel,
                     showBackButton = false,
                     triggerAddDialog = showAddPlayerDialog,
                     onAddDialogHandled = { showAddPlayerDialog = false }
                 )
             }
             2 -> {
                 SettingsScreen(
                     onBack = { /* No-op: staying in tab */ }
                 )
             }
         }
    }
}
