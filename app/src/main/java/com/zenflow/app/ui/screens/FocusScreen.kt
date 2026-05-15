package com.zenflow.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.zenflow.app.data.model.SessionType
import com.zenflow.app.ui.theme.*
import com.zenflow.app.ui.viewmodel.FocusViewModel
import com.zenflow.app.ui.viewmodel.TimerState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FocusScreen(
    navController: NavController,
    viewModel: FocusViewModel = hiltViewModel()
) {
    val timerState by viewModel.timerState.collectAsState()
    val timeRemaining by viewModel.timeRemaining.collectAsState()
    val totalTime by viewModel.totalTime.collectAsState()
    val sessionType by viewModel.sessionType.collectAsState()
    val todaySessions by viewModel.todaySessions.collectAsState()

    val progress = if (totalTime > 0) 1f - (timeRemaining.toFloat() / totalTime) else 0f
    val minutes = timeRemaining / 60
    val seconds = timeRemaining % 60

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Focus Timer", fontWeight = FontWeight.SemiBold) },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Session Type Selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                SessionTypeChip(
                    type = SessionType.POMODORO,
                    label = "Pomodoro",
                    minutes = 25,
                    isSelected = sessionType == SessionType.POMODORO,
                    onClick = { viewModel.setSessionType(SessionType.POMODORO, 25) }
                )
                SessionTypeChip(
                    type = SessionType.MEDITATION,
                    label = "Meditate",
                    minutes = 15,
                    isSelected = sessionType == SessionType.MEDITATION,
                    onClick = { viewModel.setSessionType(SessionType.MEDITATION, 15) }
                )
                SessionTypeChip(
                    type = SessionType.DEEP_WORK,
                    label = "Deep Work",
                    minutes = 60,
                    isSelected = sessionType == SessionType.DEEP_WORK,
                    onClick = { viewModel.setSessionType(SessionType.DEEP_WORK, 60) }
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Timer Circle
            Box(
                modifier = Modifier.size(280.dp),
                contentAlignment = Alignment.Center
            ) {
                // Background circle
                CircularProgressIndicator(
                    progress = { 1f },
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                    strokeWidth = 12.dp,
                    trackColor = Color.Transparent
                )

                // Progress circle
                val animatedProgress by animateFloatAsState(
                    targetValue = progress,
                    animationSpec = tween(1000, easing = LinearEasing),
                    label = "timer"
                )

                CircularProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier.fillMaxSize(),
                    color = when (sessionType) {
                        SessionType.POMODORO -> PrimaryLight
                        SessionType.MEDITATION -> AccentPurple
                        SessionType.DEEP_WORK -> AccentCyan
                        else -> PrimaryLight
                    },
                    strokeWidth = 12.dp,
                    trackColor = Color.Transparent,
                    strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                )

                // Time display
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        String.format("%02d:%02d", minutes, seconds),
                        style = MaterialTheme.typography.displayLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        when (timerState) {
                            TimerState.IDLE -> "Ready to focus"
                            TimerState.RUNNING -> "Stay focused"
                            TimerState.PAUSED -> "Paused"
                            TimerState.COMPLETED -> "Session complete!"
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Controls
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Reset
                IconButton(
                    onClick = { viewModel.resetTimer() },
                    modifier = Modifier
                        .size(56.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                ) {
                    Icon(Icons.Outlined.Refresh, contentDescription = "Reset")
                }

                // Play/Pause
                FloatingActionButton(
                    onClick = {
                        when (timerState) {
                            TimerState.IDLE -> viewModel.startTimer()
                            TimerState.RUNNING -> viewModel.pauseTimer()
                            TimerState.PAUSED -> viewModel.resumeTimer()
                            TimerState.COMPLETED -> viewModel.resetTimer()
                        }
                    },
                    modifier = Modifier.size(72.dp),
                    containerColor = MaterialTheme.colorScheme.primary,
                    shape = CircleShape
                ) {
                    Icon(
                        when (timerState) {
                            TimerState.IDLE, TimerState.PAUSED -> Icons.Default.PlayArrow
                            TimerState.RUNNING -> Icons.Default.Pause
                            TimerState.COMPLETED -> Icons.Default.Check
                        },
                        contentDescription = "Play/Pause",
                        modifier = Modifier.size(32.dp),
                        tint = Color.White
                    )
                }

                // Stop
                IconButton(
                    onClick = { viewModel.stopTimer() },
                    modifier = Modifier
                        .size(56.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                ) {
                    Icon(Icons.Default.Stop, contentDescription = "Stop")
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Today's Sessions
            if (todaySessions.isNotEmpty()) {
                Text(
                    "Today's Sessions",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(12.dp))
                todaySessions.take(3).forEach { session ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                when (session.type) {
                                    SessionType.POMODORO -> Icons.Outlined.Timer
                                    SessionType.MEDITATION -> Icons.Outlined.SelfImprovement
                                    else -> Icons.Outlined.Work
                                },
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    session.type.name.replace("_", " "),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    "${session.durationMinutes} minutes",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            if (session.isCompleted) {
                                Icon(
                                    Icons.Default.CheckCircle,
                                    contentDescription = "Completed",
                                    tint = Success
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun SessionTypeChip(
    type: SessionType,
    label: String,
    minutes: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val color = when (type) {
        SessionType.POMODORO -> PrimaryLight
        SessionType.MEDITATION -> AccentPurple
        SessionType.DEEP_WORK -> AccentCyan
        else -> PrimaryLight
    }

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) color.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant,
        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, color) else null
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(label, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
            Text("${minutes}m", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}