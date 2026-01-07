package io.github.m0nkeysan.gamekeeper.ui.screens.counter

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.m0nkeysan.gamekeeper.GameIcons

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditCounterScreen(
    id: String,
    initialName: String,
    initialCount: Int,
    initialColor: Long,
    onBack: () -> Unit,
    onSave: (String, String, Int, Long) -> Unit,
    onDelete: (String) -> Unit
) {
    var name by remember { mutableStateOf(initialName) }
    var countText by remember { mutableStateOf(initialCount.toString()) }
    var selectedColor by remember { mutableStateOf(Color(initialColor)) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showColorPicker by remember { mutableStateOf(false) }

    val presetColors = remember {
        listOf(
            Color(0xFFF44336), Color(0xFFE91E63), Color(0xFF9C27B0), Color(0xFF673AB7),
            Color(0xFF3F51B5), Color(0xFF2196F3), Color(0xFF00BCD4), Color(0xFF4CAF50),
            Color(0xFFFFC107)
        )
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            containerColor = Color.White,
            title = { Text("Delete Counter", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to delete this counter? This action cannot be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    onDelete(id)
                }) {
                    Text("Delete", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel", color = Color.Gray)
                }
            }
        )
    }

    if (showColorPicker) {
        ColorPickerDialog(
            initialColor = selectedColor,
            onDismiss = { showColorPicker = false },
            onColorSelected = { 
                selectedColor = it
                showColorPicker = false
            }
        )
    }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            // Flat, Immersive Header
            Surface(
                color = selectedColor,
                contentColor = Color.White,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .height(80.dp)
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(GameIcons.ArrowBack, contentDescription = "Back")
                    }
                    Text(
                        text = "Edit Counter",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold
                    )
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(GameIcons.Delete, contentDescription = "Delete")
                    }
                }
            }
        },
        bottomBar = {
            // Prominent Flat Save Button
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White
            ) {
                Button(
                    onClick = {
                        val count = countText.toIntOrNull() ?: 0
                        val colorArgb = (selectedColor.alpha * 255).toLong().shl(24) or
                                (selectedColor.red * 255).toLong().shl(16) or
                                (selectedColor.green * 255).toLong().shl(8) or
                                (selectedColor.blue * 255).toLong()
                        
                        onSave(id, name, count, colorArgb)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(24.dp)
                        .height(64.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = selectedColor,
                        contentColor = Color.White
                    ),
                    shape = MaterialTheme.shapes.extraLarge,
                    elevation = null
                ) {
                    Text(
                        text = "SAVE CHANGES",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding())
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // 1. Modern Color Selection
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Label(text = "ACCENT COLOR")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    presetColors.forEach { color ->
                        ColorCircle(
                            color = color,
                            isSelected = color == selectedColor,
                            size = 32.dp,
                            onClick = { selectedColor = color }
                        )
                    }
                    
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF5F5F5))
                            .clickable { showColorPicker = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = GameIcons.Palette,
                            contentDescription = "Custom",
                            tint = Color.DarkGray,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // 2. Name Field
            FlatField(
                value = name,
                onValueChange = { name = it },
                label = "NAME",
                placeholder = "Player Name",
                accentColor = selectedColor
            )

            // 3. Value Field
            FlatField(
                value = countText,
                onValueChange = { if (it.isEmpty() || it == "-" || it.all { char -> char.isDigit() || char == '-' }) countText = it },
                label = "VALUE",
                placeholder = "0",
                accentColor = selectedColor,
                keyboardType = KeyboardType.Number
            )
        }
    }
}

@Composable
private fun Label(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Black,
        color = Color.LightGray,
        letterSpacing = 1.2.sp
    )
}

@Composable
private fun FlatField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    accentColor: Color,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Label(text = label)
        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(placeholder, color = Color.Gray) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFFBFBFB),
                unfocusedContainerColor = Color(0xFFF5F5F5),
                focusedIndicatorColor = accentColor,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = accentColor,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            ),
            shape = MaterialTheme.shapes.large,
            textStyle = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
        )
    }
}

@Composable
private fun ColorCircle(
    color: Color,
    isSelected: Boolean,
    size: androidx.compose.ui.unit.Dp,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(color)
            .border(
                width = if (isSelected) 3.dp else 0.dp,
                color = Color.Black.copy(alpha = 0.3f),
                shape = CircleShape
            )
            .clickable(onClick = onClick)
    )
}

@Composable
private fun ColorPickerDialog(
    initialColor: Color,
    onDismiss: () -> Unit,
    onColorSelected: (Color) -> Unit
) {
    var hue by remember { mutableStateOf(0f) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        title = { 
            Text(
                "Pick a Color", 
                fontWeight = FontWeight.Black,
                style = MaterialTheme.typography.titleLarge
            ) 
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(MaterialTheme.shapes.extraLarge)
                        .background(Color.hsv(hue, 0.6f, 0.9f))
                )
                
                Slider(
                    value = hue,
                    onValueChange = { hue = it },
                    valueRange = 0f..360f,
                    colors = SliderDefaults.colors(
                        thumbColor = Color.hsv(hue, 0.6f, 0.9f),
                        activeTrackColor = Color.hsv(hue, 0.6f, 0.9f).copy(alpha = 0.24f)
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onColorSelected(Color.hsv(hue, 0.6f, 0.9f)) },
                colors = ButtonDefaults.buttonColors(containerColor = Color.hsv(hue, 0.6f, 0.9f)),
                shape = MaterialTheme.shapes.medium
            ) {
                Text("Select", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color.Gray)
            }
        }
    )
}
