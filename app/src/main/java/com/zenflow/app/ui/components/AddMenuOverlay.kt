package com.zenflow.app.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.zenflow.app.ui.theme.*

@Composable
fun AddMenuOverlay(
    onDismiss: () -> Unit,
    onAddTask: () -> Unit,
    onAddSection: () -> Unit,
    onAddCompletedTask: () -> Unit,
    onAddGoal: () -> Unit,
    onAddNote: () -> Unit,
    onStartFocus: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.6f))
            .clickable(onClick = onDismiss)
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Menu Items
            val items = listOf(
                MenuItem("Add Task", Icons.Outlined.AddTask, PrimaryLight, onAddTask),
                MenuItem("Add Completed Task", Icons.Outlined.CheckCircle, Success, onAddCompletedTask),
                MenuItem("New Section", Icons.Outlined.CreateNewFolder, AccentPurple, onAddSection),
                MenuItem("Add Goal", Icons.Outlined.Flag, AccentPink, onAddGoal),
                MenuItem("Quick Note", Icons.Outlined.NoteAdd, AccentOrange, onAddNote),
                MenuItem("Start Focus", Icons.Outlined.Timer, AccentCyan, onStartFocus)
            )

            items.forEachIndexed { index, item ->
                val delay = index * 50
                val visible by remember { mutableStateOf(true) }

                AnimatedVisibility(
                    visible = visible,
                    enter = slideInVertically(
                        initialOffsetY = { it * 2 },
                        animationSpec = tween(300, delayMillis = delay)
                    ) + fadeIn(tween(300, delayMillis = delay)),
                    exit = slideOutVertically(
                        targetOffsetY = { it * 2 },
                        animationSpec = tween(200)
                    ) + fadeOut(tween(200))
                ) {
                    MenuActionButton(item)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Close button
            FloatingActionButton(
                onClick = onDismiss,
                containerColor = MaterialTheme.colorScheme.error,
                shape = CircleShape,
                modifier = Modifier.size(56.dp)
            ) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
            }
        }
    }
}

data class MenuItem(
    val label: String,
    val icon: ImageVector,
    val color: Color,
    val onClick: () -> Unit
)

@Composable
fun MenuActionButton(item: MenuItem) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clickable(onClick = item.onClick)
    ) {
        Surface(
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                item.label,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Medium
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        FloatingActionButton(
            onClick = item.onClick,
            containerColor = item.color,
            shape = CircleShape,
            modifier = Modifier.size(48.dp)
        ) {
            Icon(item.icon, contentDescription = item.label, tint = Color.White, modifier = Modifier.size(22.dp))
        }
    }
}