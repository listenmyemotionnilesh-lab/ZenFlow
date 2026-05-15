package com.zenflow.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSectionDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var quote by remember { mutableStateOf("") }
    var selectedIcon by remember { mutableStateOf("book") }
    var selectedColor by remember { mutableStateOf("#6366F1") }

    val icons = listOf(
        "book" to Icons.Outlined.School,
        "work" to Icons.Outlined.Work,
        "self_improvement" to Icons.Outlined.SelfImprovement,
        "fitness_center" to Icons.Outlined.FitnessCenter,
        "person" to Icons.Outlined.Person,
        "code" to Icons.Outlined.Code,
        "palette" to Icons.Outlined.Palette,
        "music_note" to Icons.Outlined.MusicNote,
        "language" to Icons.Outlined.Language,
        "science" to Icons.Outlined.Science
    )

    val colors = listOf(
        "#6366F1", "#10B981", "#8B5CF6", "#F59E0B",
        "#EC4899", "#06B6D4", "#EF4444", "#84CC16"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Section", fontWeight = FontWeight.SemiBold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Section Name *") },
                    leadingIcon = { Icon(Icons.Outlined.CreateNewFolder, contentDescription = null) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = quote,
                    onValueChange = { quote = it },
                    label = { Text("Motivational Quote") },
                    leadingIcon = { Icon(Icons.Outlined.FormatQuote, contentDescription = null) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                // Icon Selector
                Text("Icon", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Medium)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(icons) { (iconName, icon) ->
                        val isSelected = selectedIcon == iconName
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isSelected) Color(android.graphics.Color.parseColor(selectedColor)).copy(alpha = 0.2f)
                                    else MaterialTheme.colorScheme.surfaceVariant
                                )
                                .clickable { selectedIcon = iconName },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                icon,
                                contentDescription = iconName,
                                tint = if (isSelected) Color(android.graphics.Color.parseColor(selectedColor))
                                    else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Color Selector
                Text("Color", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Medium)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(colors) { colorHex ->
                        val color = Color(android.graphics.Color.parseColor(colorHex))
                        val isSelected = selectedColor == colorHex
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(color)
                                .clickable { selectedColor = colorHex }
                                .then(
                                    if (isSelected) Modifier.padding(2.dp) else Modifier
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Icon(Icons.Outlined.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onConfirm(name, selectedIcon, selectedColor, quote)
                    }
                },
                enabled = name.isNotBlank(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}