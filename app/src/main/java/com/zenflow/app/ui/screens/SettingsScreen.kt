package com.zenflow.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.zenflow.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    var darkMode by remember { mutableStateOf(false) }
    var notifications by remember { mutableStateOf(true) }
    var soundEffects by remember { mutableStateOf(true) }
    var haptics by remember { mutableStateOf(true) }
    var showBackupDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Appearance
            item {
                SettingsSectionTitle("Appearance")
            }
            item {
                SettingsSwitchItem(
                    icon = Icons.Outlined.DarkMode,
                    title = "Dark Mode",
                    subtitle = "Use dark theme",
                    checked = darkMode,
                    onCheckedChange = { darkMode = it }
                )
            }

            // Notifications
            item {
                Spacer(modifier = Modifier.height(16.dp))
                SettingsSectionTitle("Notifications")
            }
            item {
                SettingsSwitchItem(
                    icon = Icons.Outlined.Notifications,
                    title = "Task Reminders",
                    subtitle = "Get reminded about upcoming tasks",
                    checked = notifications,
                    onCheckedChange = { notifications = it }
                )
            }
            item {
                SettingsSwitchItem(
                    icon = Icons.Outlined.Vibration,
                    title = "Haptic Feedback",
                    subtitle = "Vibrate on actions",
                    checked = haptics,
                    onCheckedChange = { haptics = it }
                )
            }
            item {
                SettingsSwitchItem(
                    icon = Icons.Outlined.VolumeUp,
                    title = "Sound Effects",
                    subtitle = "Play sounds on completion",
                    checked = soundEffects,
                    onCheckedChange = { soundEffects = it }
                )
            }

            // Data
            item {
                Spacer(modifier = Modifier.height(16.dp))
                SettingsSectionTitle("Data & Backup")
            }
            item {
                SettingsActionItem(
                    icon = Icons.Outlined.Backup,
                    title = "Export Backup",
                    subtitle = "Save your data to external storage",
                    onClick = { showBackupDialog = true }
                )
            }
            item {
                SettingsActionItem(
                    icon = Icons.Outlined.Restore,
                    title = "Import Backup",
                    subtitle = "Restore from a previous backup",
                    onClick = { }
                )
            }
            item {
                SettingsActionItem(
                    icon = Icons.Outlined.Delete,
                    title = "Clear All Data",
                    subtitle = "Delete all tasks and settings",
                    onClick = { },
                    tint = Error
                )
            }

            // About
            item {
                Spacer(modifier = Modifier.height(16.dp))
                SettingsSectionTitle("About")
            }
            item {
                SettingsActionItem(
                    icon = Icons.Outlined.Info,
                    title = "About Zen Flow",
                    subtitle = "Version 1.0.0",
                    onClick = { showAboutDialog = true }
                )
            }

            // Bottom padding
            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }

        // Backup Dialog
        if (showBackupDialog) {
            AlertDialog(
                onDismissRequest = { showBackupDialog = false },
                title = { Text("Export Backup") },
                text = {
                    Text("Your data will be exported as a JSON file to your Downloads folder. You can move this to your SD card for safekeeping.")
                },
                confirmButton = {
                    TextButton(onClick = { showBackupDialog = false }) {
                        Text("Export")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showBackupDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        // About Dialog
        if (showAboutDialog) {
            AlertDialog(
                onDismissRequest = { showAboutDialog = false },
                title = { Text("Zen Flow") },
                text = {
                    Column {
                        Text("A minimal, beautiful productivity app designed for NEET aspirants and focused students.")
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Features:")
                        Text("• Smart task management")
                        Text("• Focus timer & meditation")
                        Text("• Progress tracking")
                        Text("• Offline-first design")
                        Text("• Lightweight & fast")
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showAboutDialog = false }) {
                        Text("Nice!")
                    }
                }
            )
        }
    }
}

@Composable
fun SettingsSectionTitle(title: String) {
    Text(
        title,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
fun SettingsSwitchItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Switch(checked = checked, onCheckedChange = onCheckedChange)
        }
    }
}

@Composable
fun SettingsActionItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    tint: Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = tint)
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium, color = tint)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}