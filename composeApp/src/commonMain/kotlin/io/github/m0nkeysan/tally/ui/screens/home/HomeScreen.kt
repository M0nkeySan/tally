package io.github.m0nkeysan.tally.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.m0nkeysan.tally.GameIcons
import io.github.m0nkeysan.tally.core.navigation.HomeCustomizationRoute
import io.github.m0nkeysan.tally.core.navigation.Route
import io.github.m0nkeysan.tally.generated.resources.Res
import io.github.m0nkeysan.tally.generated.resources.home_empty_action
import io.github.m0nkeysan.tally.generated.resources.home_empty_message
import io.github.m0nkeysan.tally.generated.resources.home_empty_title
import io.github.m0nkeysan.tally.generated.resources.home_title
import io.github.m0nkeysan.tally.ui.components.GameCard
import io.github.m0nkeysan.tally.ui.theme.resolveIsDarkTheme
import org.jetbrains.compose.resources.stringResource

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onNavigateTo: (Route) -> Unit,
    viewModel: HomeViewModel = viewModel { HomeViewModel() }
) {
    val visibleFeatures by viewModel.visibleFeatures.collectAsState()
    val themePreference by viewModel.themePreference.collectAsState()
    val isDarkTheme = resolveIsDarkTheme(themePreference)

    val gameFeatureMap = getGameFeatureMap()

    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        stringResource(Res.string.home_title),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier.shadow(elevation = 2.dp)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (visibleFeatures.isEmpty()) {
                // Empty state when all features are hidden
                EmptyHomeState(
                    onCustomize = { onNavigateTo(HomeCustomizationRoute) }
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                ) {
                    Spacer(modifier = Modifier.height(16.dp))

                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 140.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        items(
                            items = visibleFeatures,
                            key = { it.featureId }
                        ) { featureState ->
                            val feature = gameFeatureMap[featureState.featureId]
                            if (feature != null) {
                                GameCard(
                                    icon = feature.icon,
                                    title = feature.title,
                                    description = feature.description,
                                    onClick = { onNavigateTo(feature.route) },
                                    borderColor = if (isDarkTheme) {
                                        feature.colors.borderDark
                                    } else {
                                        feature.colors.borderLight
                                    },
                                    backgroundColor = if (isDarkTheme) {
                                        feature.colors.backgroundDark.copy(alpha = 0.3f)
                                    } else {
                                        feature.colors.backgroundLight.copy(alpha = 0.2f)
                                    },
                                    elevation = CardDefaults.cardElevation(
                                        defaultElevation = if (isDarkTheme) 2.dp else 0.dp
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyHomeState(
    modifier: Modifier = Modifier,
    onCustomize: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            GameIcons.ViewModule,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(Res.string.home_empty_title),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(Res.string.home_empty_message),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = onCustomize) {
            Text(stringResource(Res.string.home_empty_action))
        }
    }
}
