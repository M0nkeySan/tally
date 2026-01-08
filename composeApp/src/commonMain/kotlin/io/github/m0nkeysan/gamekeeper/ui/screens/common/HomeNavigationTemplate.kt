package io.github.m0nkeysan.gamekeeper.ui.screens.common

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.m0nkeysan.gamekeeper.GameIcons
import io.github.m0nkeysan.gamekeeper.ui.screens.home.HomeScreen
import io.github.m0nkeysan.gamekeeper.ui.screens.home.HomeViewModel
import io.github.m0nkeysan.gamekeeper.ui.screens.player.PlayerSelectionScreen
import io.github.m0nkeysan.gamekeeper.ui.screens.player.PlayerSelectionViewModel
import io.github.m0nkeysan.gamekeeper.ui.theme.GameColors

/**
 * Navigation template for the home screen with bottom bar for switching between Games and Players tabs.
 * Only appears on the home screen to manage navigation between game selection and player management.
 *
 * Features:
 * - Bottom navigation bar with Games and Players tabs (icons only, no labels)
 * - Conditional rendering of HomeScreen (Games tab) and PlayerSelectionScreen (Players tab)
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
    homeViewModel: HomeViewModel = viewModel { HomeViewModel() },
    playerViewModel: PlayerSelectionViewModel = viewModel { PlayerSelectionViewModel() },
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) }

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
                            contentDescription = "Games"
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
                            contentDescription = "Players"
                        )
                    },
                    alwaysShowLabel = false
                )
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
                    showBackButton = false
                )
            }
        }
    }
}
