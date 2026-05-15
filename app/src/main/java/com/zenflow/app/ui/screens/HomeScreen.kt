package com.zenflow.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.zenflow.app.data.model.Section
import com.zenflow.app.data.model.Task
import com.zenflow.app.ui.components.*
import com.zenflow.app.ui.theme.*
import com.zenflow.app.ui.viewmodel.HomeViewModel
import com.zenflow.app.utils.IconMapper
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val sections by viewModel.sections.collectAsState()
    val todayStats by viewModel.todayStats.collectAsState()
    val pendingTasks by viewModel.pendingTasks.collectAsState()
    val completedTasks by viewModel.completedTasks.collectAsState()
    val totalPending by viewModel.totalPending.collectAsState()
    val todayCompleted by viewModel.todayCompleted.collectAsState()
    val maxStreak by viewModel.maxStreak.collectAsState()
    val todayFocusMinutes by viewModel.todayFocusMinutes.collectAsState()
    val weeklyStats by viewModel.weeklyStats.collectAsState()
    val goals by viewModel.goals.collectAsState()

    var showAddMenu by remember { mutableStateOf(false) }
    var showAddSection by remember { mutableStateOf(false) }
    var showAddCompletedTask by remember { mutableStateOf(false) }
    var showAddGoal by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Zen Flow",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, MMM d")),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate("search") }) {
                        Icon(Icons.Outlined.Search, contentDescription = "Search")
                    }
                    IconButton(onClick = { navController.navigate("settings") }) {
                        Icon(Icons.Outlined.Settings, contentDescription = "Settings")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = !showAddMenu,
                enter = scaleIn(),
                exit = scaleOut()
            ) {
                FloatingActionButton(
                    onClick = { showAddMenu = true },
                    containerColor = MaterialTheme.colorScheme.primary,
                    shape = CircleShape,
                    modifier = Modifier.size(64.dp)
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Add",
                        modifier = Modifier.size(28.dp),
                        tint = Color.White
                    )
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Greeting & Motivation
                item {
                    GreetingCard(streak = maxStreak ?: 0)
                }

                // Progress Ring
                item {
                    ProgressRingCard(
                        completed = todayCompleted,
                        total = totalPending + todayCompleted,
                        focusMinutes = todayFocusMinutes ?: 0,
                        productivityScore = viewModel.calculateProductivityScore()
                    )
                }

                // Section Cards
                item {
                    Text(
                        "Your Spaces",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                item {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(horizontal = 4.dp)
                    ) {
                        items(sections, key = { it.id }) { section ->
                            SectionCard(
                                section = section,
                                onClick = { navController.navigate("section/${section.id}") },
                                onDelete = { viewModel.deleteSection(section) }
                            )
                        }
                    }
                }

                // Today's Tasks
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Today's Tasks",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                        TextButton(onClick = { navController.navigate("calendar") }) {
                            Text("Calendar")
                        }
                    }
                }

                if (pendingTasks.isEmpty() && completedTasks.isEmpty()) {
                    item {
                        EmptyStateCard(
                            icon = Icons.Outlined.CheckCircle,
                            title = "All Caught Up!",
                            subtitle = "Add tasks or enjoy your free time"
                        )
                    }
                } else {
                    items(pendingTasks.take(5), key = { it.id }) { task ->
                        TaskItem(
                            task = task,
                            onComplete = { viewModel.completeTask(task) },
                            onClick = { /* Edit task */ }
                        )
                    }

                    if (completedTasks.isNotEmpty()) {
                        item {
                            Text(
                                "Completed Today",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                        items(completedTasks.take(3), key = { it.id }) { task ->
                            CompletedTaskItem(task = task)
                        }
                    }
                }

                // Weekly Progress
                item {
                    WeeklyProgressCard(weeklyStats = weeklyStats)
                }

                // Goals
                if (goals.isNotEmpty()) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Active Goals",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.SemiBold
                            )
                            TextButton(onClick = { navController.navigate("goals") }) {
                                Text("View All")
                            }
                        }
                    }
                    items(goals.take(2), key = { it.id }) { goal ->
                        GoalProgressCard(goal = goal)
                    }
                }

                // Bottom padding
                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }

            // Add Menu Overlay
            AnimatedVisibility(
                visible = showAddMenu,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                AddMenuOverlay(
                    onDismiss = { showAddMenu = false },
                    onAddTask = { showAddMenu = false /* Navigate to section */ },
                    onAddSection = { showAddMenu = false; showAddSection = true },
                    onAddCompletedTask = { showAddMenu = false; showAddCompletedTask = true },
                    onAddGoal = { showAddMenu = false; showAddGoal = true },
                    onAddNote = { showAddMenu = false },
                    onStartFocus = { 
                        showAddMenu = false
                        navController.navigate("focus")
                    }
                )
            }

            // Add Section Dialog
            if (showAddSection) {
                AddSectionDialog(
                    onDismiss = { showAddSection = false },
                    onConfirm = { name, icon, color, quote ->
                        viewModel.addSection(name, icon, color, quote)
                        showAddSection = false
                    }
                )
            }

            // Add Completed Task Dialog
            if (showAddCompletedTask) {
                AddCompletedTaskDialog(
                    sections = sections,
                    onDismiss = { showAddCompletedTask = false },
                    onConfirm = { sectionId, title, duration ->
                        viewModel.addCompletedTask(sectionId, title, duration)
                        showAddCompletedTask = false
                    }
                )
            }

            // Add Goal Dialog
            if (showAddGoal) {
                AddGoalDialog(
                    onDismiss = { showAddGoal = false },
                    onConfirm = { title, description, targetDate ->
                        // viewModel.addGoal(...)
                        showAddGoal = false
                    }
                )
            }
        }
    }
}

@Composable
fun GreetingCard(streak: Int) {
    val greeting = when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
        in 5..11 -> "Good Morning"
        in 12..16 -> "Good Afternoon"
        in 17..21 -> "Good Evening"
        else -> "Good Night"
    }

    val motivationalLines = listOf(
        "Small steps today, big results tomorrow.",
        "Your future self will thank you.",
        "Consistency beats intensity.",
        "One task at a time. You've got this.",
        "Progress, not perfection."
    )

    val randomLine = remember { motivationalLines.random() }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(GradientStart, GradientEnd),
                        start = Offset(0f, 0f),
                        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                    )
                )
                .padding(20.dp)
        ) {
            Column {
                Text(
                    greeting,
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    randomLine,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.9f)
                )
                if (streak > 0) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.LocalFireDepartment,
                            contentDescription = null,
                            tint = Color(0xFFFFA726),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "${streak} day streak!",
                            style = MaterialTheme.typography.labelLarge,
                            color = Color.White.copy(alpha = 0.9f),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProgressRingCard(
    completed: Int,
    total: Int,
    focusMinutes: Int,
    productivityScore: Int
) {
    val progress = if (total > 0) completed.toFloat() / total else 0f
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(1000, easing = EaseOutQuart),
        label = "progress"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Progress Ring
            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    progress = { 1f },
                    modifier = Modifier.size(100.dp),
                    color = MaterialTheme.colorScheme.outlineVariant,
                    strokeWidth = 8.dp,
                    trackColor = Color.Transparent
                )
                CircularProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier.size(100.dp),
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 8.dp,
                    trackColor = Color.Transparent,
                    strokeCap = StrokeCap.Round
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "${(animatedProgress * 100).toInt()}%",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text("Done", style = MaterialTheme.typography.labelSmall)
                }
            }

            // Stats
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                StatItem("${focusMinutes / 60}h ${focusMinutes % 60}m", "Focus Time", Icons.Outlined.Timer)
                Spacer(modifier = Modifier.height(12.dp))
                StatItem("$productivityScore", "Productivity", Icons.Outlined.TrendingUp)
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                StatItem("$completed", "Completed", Icons.Outlined.CheckCircle)
                Spacer(modifier = Modifier.height(12.dp))
                StatItem("$total", "Total", Icons.Outlined.List)
            }
        }
    }
}

@Composable
fun StatItem(value: String, label: String, icon: ImageVector) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(4.dp))
        Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun SectionCard(
    section: Section,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val color = Color(android.graphics.Color.parseColor(section.color))
    val icon = IconMapper.getIcon(section.icon)

    Card(
        onClick = onClick,
        modifier = Modifier
            .width(160.dp)
            .height(120.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            color.copy(alpha = 0.15f),
                            color.copy(alpha = 0.05f)
                        )
                    )
                )
                .padding(16.dp)
        ) {
            Column {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(color.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(22.dp))
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    section.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                if (section.motivationalQuote.isNotBlank()) {
                    Text(
                        section.motivationalQuote,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Composable
fun WeeklyProgressCard(weeklyStats: List<com.zenflow.app.data.model.DailyStats>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                "Weekly Progress",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (weeklyStats.isEmpty()) {
                Text(
                    "No data yet. Start completing tasks!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                // Simple bar chart
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.Bottom
                ) {
                    weeklyStats.takeLast(7).forEach { stat ->
                        val height = (stat.tasksCompleted * 20 + 20).dp
                        val animatedHeight by animateDpAsState(
                            targetValue = height,
                            animationSpec = tween(800, easing = EaseOutQuart),
                            label = "bar"
                        )

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .width(24.dp)
                                    .height(animatedHeight)
                                    .background(
                                        if (stat.tasksCompleted > 0) MaterialTheme.colorScheme.primary
                                        else MaterialTheme.colorScheme.outlineVariant,
                                        RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
                                    )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                stat.date.takeLast(2),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GoalProgressCard(goal: com.zenflow.app.data.model.Goal) {
    val color = Color(android.graphics.Color.parseColor(goal.color))
    val animatedProgress by animateFloatAsState(
        targetValue = goal.progress / 100f,
        animationSpec = tween(1000, easing = EaseOutQuart),
        label = "goal"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(goal.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text("${goal.progress}%", style = MaterialTheme.typography.labelLarge, color = color)
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = color,
                trackColor = MaterialTheme.colorScheme.outlineVariant
            )
        }
    }
}

@Composable
fun EmptyStateCard(icon: ImageVector, title: String, subtitle: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
        }
    }
}