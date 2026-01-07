package io.github.m0nkeysan.gamekeeper.ui.screens.addplayer

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.m0nkeysan.gamekeeper.GameIcons

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPlayerScreen(
    onBack: () -> Unit
) {
    // Initialisation du ViewModel compatible Navigation Compose
    val viewModel: AddPlayerViewModel = viewModel { AddPlayerViewModel() }
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Player") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(GameIcons.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        Text(
                            text = "New Player",
                            style = MaterialTheme.typography.headlineMedium
                        )

                        OutlinedTextField(
                            value = state.name,
                            onValueChange = { viewModel.updateName(it) },
                            label = { Text("Player Name") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            isError = state.nameError != null,
                            supportingText = { state.nameError?.let { Text(it) } }
                        )

                        Text(
                            text = "Avatar Color",
                            style = MaterialTheme.typography.titleMedium
                        )

                        // Liste des couleurs simplifiée
                        val colors = listOf(
                            "#FF6200", "#E91E63", "#9C27B0", "#673AB7",
                            "#3F51B5", "#2196F3", "#00BCD4", "#4CAF50"
                        )

                        // Affichage en grille (2 lignes de 4)
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            colors.chunked(4).forEach { rowColors ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    rowColors.forEach { colorHex ->
                                        ColorOption(
                                            colorHex = colorHex,
                                            isSelected = state.avatarColor == colorHex,
                                            onClick = { viewModel.updateColor(colorHex) },
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = {
                                viewModel.addPlayer()
                                onBack() // Retour à l'écran précédent
                            },
                            enabled = state.name.isNotBlank() && state.nameError == null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                        ) {
                            Text("Add Player", style = MaterialTheme.typography.titleLarge)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ColorOption(
    colorHex: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Parser de couleur KMP (Sécurisé pour Android et iOS)
    val color = remember(colorHex) {
        try {
            Color(0xFF000000 or colorHex.removePrefix("#").toLong(16))
        } catch (e: Exception) {
            Color.Gray
        }
    }

    Surface(
        shape = CircleShape,
        border = if (isSelected) {
            BorderStroke(3.dp, MaterialTheme.colorScheme.primary)
        } else {
            BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        },
        color = color,
        modifier = modifier
            .aspectRatio(1f) // Pour garder un cercle parfait
            .clickable(onClick = onClick)
    ) {}
}