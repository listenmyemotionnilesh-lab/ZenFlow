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
import com.zenflow.app.data.model.Section
import com.zenflow.app.utils.IconMapper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCompletedTaskDialog(
    sections: List<Section>,
    onDismiss: () -> Unit,
    onConfirm: (Long, String, Int) -> Unit
) {
    var selectedSectionId by remember { mutableStateOf(sections.firstOrNull()?.id ?: 0L) }
    var taskName by remember { mutableStateOf("") }
    var durationText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Completed Task", fontWeight = FontWeight.SemiBold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                    "Already did something? Log it here!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Section Selector
                Text("Section", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Medium)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(sections) { section ->
                        val isSelected = selectedSectionId == section.id
                        val color = Color(android.graphics.Color.parseColor(section.color))
                        Surface(
                            onClick = { selectedSectionId = section.id },
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) color.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant,
                            border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, color) else null
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    IconMapper.getIcon(section.icon),
                                    contentDescription = null,
                                    tint = if (isSelected) color else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    section.name,
                                    style = MaterialTheme.typography.labelLarge,
                                    color = if (isSelected) color else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = taskName,
                    onValueChange = { taskName = it },
                    label = { Text("What did you do? *") },
                    leadingIcon = { Icon(Icons.Outlined.CheckCircle, contentDescription = null) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = durationText,
                    onValueChange = { durationText = it.filter { it.isDigit() } },
                    label = { Text("Duration (minutes)") },
                    leadingIcon = { Icon(Icons.Outlined.Timer, contentDescription = null) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (taskName.isNotBlank() && selectedSectionId != 0L) {
                        val duration = durationText.toIntOrNull() ?: 0
                        onConfirm(selectedSectionId, taskName, duration)
                    }
                },
                enabled = taskName.isNotBlank() && selectedSectionId != 0L,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Log It!")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}