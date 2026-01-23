package io.github.m0nkeysan.gamekeeper.ui.screens.counter

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.m0nkeysan.gamekeeper.GameIcons
import io.github.m0nkeysan.gamekeeper.core.model.MergedCounterChange
import io.github.m0nkeysan.gamekeeper.generated.resources.Res
import io.github.m0nkeysan.gamekeeper.generated.resources.action_back
import io.github.m0nkeysan.gamekeeper.generated.resources.action_cancel
import io.github.m0nkeysan.gamekeeper.generated.resources.counter_dialog_delete_message
import io.github.m0nkeysan.gamekeeper.generated.resources.counter_history_cd_delete
import io.github.m0nkeysan.gamekeeper.generated.resources.counter_history_deleted_emoji
import io.github.m0nkeysan.gamekeeper.generated.resources.counter_history_deleted_text
import io.github.m0nkeysan.gamekeeper.generated.resources.counter_history_empty
import io.github.m0nkeysan.gamekeeper.generated.resources.counter_history_subtitle
import io.github.m0nkeysan.gamekeeper.generated.resources.counter_history_title
import io.github.m0nkeysan.gamekeeper.generated.resources.dialog_delete_all_title
import io.github.m0nkeysan.gamekeeper.platform.formatTimestamp
import io.github.m0nkeysan.gamekeeper.ui.theme.LocalCustomColors
import org.jetbrains.compose.resources.stringResource

@Composable
fun CounterHistoryScreen(
    onBackPressed: () -> Unit,
    viewModel: CounterViewModel = viewModel { CounterViewModel() }
) {
    val mergedHistory by viewModel.mergedHistory.collectAsState()
    var showDeleteAllDialog by remember { mutableStateOf(false) }
    
    // Delete all confirmation dialog
    if (showDeleteAllDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteAllDialog = false },
            title = { Text(stringResource(Res.string.counter_history_cd_delete)) },
            text = { Text(stringResource(Res.string.counter_dialog_delete_message)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.clearCounterHistory()
                        showDeleteAllDialog = false
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(stringResource(Res.string.dialog_delete_all_title))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteAllDialog = false }) {
                    Text(stringResource(Res.string.action_cancel))
                }
            }
        )
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(stringResource(Res.string.counter_history_title))
                            Text(
                                stringResource(Res.string.counter_history_subtitle),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(GameIcons.ArrowBack, contentDescription = stringResource(Res.string.action_back))
                    }
                },
                actions = {
                    if (mergedHistory.isNotEmpty()) {
                        IconButton(onClick = { showDeleteAllDialog = true }) {
                            Icon(GameIcons.Delete, contentDescription = stringResource(Res.string.counter_history_cd_delete))
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (mergedHistory.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        stringResource(Res.string.counter_history_empty),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(mergedHistory.size) { index ->
                        CounterHistoryItem(mergedHistory[index])
                        if (index < mergedHistory.size - 1) {
                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                color = MaterialTheme.colorScheme.outlineVariant,
                                thickness = 1.dp
                            )
                        }
                    }
                }
            }
        }
    }
    
    BackHandler { onBackPressed() }
}

@Composable
fun CounterHistoryItem(mergedChange: MergedCounterChange) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Color indicator box or trash icon
        if (mergedChange.isDeleted) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = MaterialTheme.colorScheme.outline,
                        shape = RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(Res.string.counter_history_deleted_emoji),
                    style = MaterialTheme.typography.headlineSmall
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = Color(mergedChange.counterColor),
                        shape = RoundedCornerShape(8.dp)
                    )
            )
        }
        
        // Counter name and info
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = mergedChange.counterName,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                maxLines = 1,
                color = if (mergedChange.isDeleted) {
                    MaterialTheme.colorScheme.onSurfaceVariant
                } else {
                    MaterialTheme.colorScheme.onSurface
                }
            )
            
            Text(
                text = formatTimestamp(mergedChange.lastTimestamp),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        // Total delta display or stringResource(Res.string.counter_history_deleted_text) text
        Column(
            horizontalAlignment = Alignment.End
        ) {
            if (mergedChange.isDeleted) {
                Text(
                    text = stringResource(Res.string.counter_history_deleted_text),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    ),
                    color = MaterialTheme.colorScheme.outline
                )
            } else {
                 Text(
                     text = "${if (mergedChange.totalDelta > 0) "+" else ""}${mergedChange.totalDelta}",
                     style = MaterialTheme.typography.titleMedium.copy(
                         fontWeight = FontWeight.Bold,
                         fontSize = 18.sp
                     ),
                     color = if (mergedChange.totalDelta > 0) {
                         LocalCustomColors.current.success
                     } else if (mergedChange.totalDelta < 0) {
                         MaterialTheme.colorScheme.error
                     } else {
                         MaterialTheme.colorScheme.onSurfaceVariant
                     }
                 )
            }
        }
    }
}
