package com.example.luma.ui.screens.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.AlarmOff
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.luma.database.AlarmEntity
import com.example.luma.database.DatabaseHelper
import com.example.luma.database.SleepSession
import com.example.luma.ui.theme.*
import com.example.luma.ui.widgets.SleepingCatMoonLogo
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HomeScreen(
    onNavigateToCalculator: () -> Unit,
    onNavigateToAlarms: () -> Unit
) {
    val context = LocalContext.current
    val dbHelper = remember { DatabaseHelper(context) }
    var sleepSessions by remember { mutableStateOf(dbHelper.getAllSleepSessions()) }
    var alarms by remember { mutableStateOf(dbHelper.getAllAlarms()) }
    var showLogDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        sleepSessions = dbHelper.getAllSleepSessions()
        alarms = dbHelper.getAllAlarms()
    }

    val enabledAlarms = alarms.filter { it.isEnabled }
    val nextAlarm: AlarmEntity? = remember(enabledAlarms) {
        if (enabledAlarms.isEmpty()) return@remember null
        val now = Calendar.getInstance()
        val currentHour = now.get(Calendar.HOUR_OF_DAY)
        val currentMinute = now.get(Calendar.MINUTE)
        var earliest: AlarmEntity? = null
        var minDiff = Int.MAX_VALUE
        for (alarm in enabledAlarms) {
            var diff = (alarm.hour * 60 + alarm.minute) - (currentHour * 60 + currentMinute)
            if (diff <= 0) diff += 24 * 60
            if (diff < minDiff) {
                minDiff = diff
                earliest = alarm
            }
        }
        earliest ?: enabledAlarms.first()
    }

    var qualityRating by remember { mutableFloatStateOf(4.5f) }
    var sleepNotes by remember { mutableStateOf("Felt refreshed") }

    val scrollState = rememberScrollState()
    val dateFormat = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault())
    val currentDate = dateFormat.format(Date())

    val calendar = Calendar.getInstance()
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val greeting = when (hour) {
        in 5..11 -> "Good morning"
        in 12..17 -> "Good afternoon"
        in 18..21 -> "Good evening"
        else -> "Good night"
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LumaMidnightNavy)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Permanent Educational Section at the VERY TOP
            ScienceOfSleepCard()

            // Header & Greeting Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        SleepingCatMoonLogo(size = 32.dp)
                        Text(
                            text = "LUMA",
                            color = LumaWhite,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = currentDate,
                        color = LumaSecondaryText,
                        fontSize = 14.sp
                    )
                }
                
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = LumaDeepBlue,
                    border = BorderStroke(1.dp, LumaCardBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bedtime,
                            contentDescription = null,
                            tint = LumaLightBlue,
                            modifier = Modifier.size(16.dp)
                        )
                        val avgDuration = if (sleepSessions.isNotEmpty()) {
                            val totalMins = sleepSessions.sumOf { it.durationMinutes }
                            val avgMins = totalMins / sleepSessions.size
                            String.format(Locale.getDefault(), "%dh %dm", avgMins / 60, avgMins % 60)
                        } else "8.0h"
                        Text(
                            text = "$avgDuration avg",
                            color = LumaWhite,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = LumaDeepBlue),
                border = BorderStroke(1.dp, LumaCardBorder)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "$greeting, Sleeper",
                            color = LumaSoftLavender,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Wake up to your rhythm. Align your rest with natural sleep cycles for effortless mornings.",
                            color = LumaSecondaryText,
                            fontSize = 14.sp,
                            lineHeight = 20.sp
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = onNavigateToCalculator,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = LumaSoftLavender)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Calculate,
                                contentDescription = null,
                                tint = LumaMidnightNavy,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Calculator",
                                color = LumaMidnightNavy,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }

                        OutlinedButton(
                            onClick = { showLogDialog = true },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, LumaSoftLavender),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = LumaSoftLavender)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Log Sleep",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = LumaDeepBlue),
                border = BorderStroke(1.dp, LumaCardBorder)
            ) {
                if (nextAlarm != null) {
                    val h24 = nextAlarm.hour
                    val min = nextAlarm.minute
                    val amPm = if (h24 < 12) "AM" else "PM"
                    val h12 = when {
                        h24 == 0 -> 12
                        h24 > 12 -> h24 - 12
                        else -> h24
                    }
                    val timeStr = String.format(Locale.getDefault(), "%02d:%02d %s", h12, min, amPm)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = LumaMidnightNavy,
                                modifier = Modifier.size(48.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Alarm,
                                        contentDescription = null,
                                        tint = LumaLightBlue
                                    )
                                }
                            }
                            Column {
                                Text(
                                    text = "Next Upcoming Alarm",
                                    color = LumaSecondaryText,
                                    fontSize = 12.sp
                                )
                                Text(
                                    text = timeStr,
                                    color = LumaWhite,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${nextAlarm.label} • ${nextAlarm.repeatDays}",
                                    color = LumaSoftLavender,
                                    fontSize = 12.sp
                                )
                            }
                        }
                        TextButton(onClick = onNavigateToAlarms) {
                            Text(
                                text = "Manage",
                                color = LumaLightBlue,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AlarmOff,
                            contentDescription = null,
                            tint = LumaSecondaryText,
                            modifier = Modifier.size(36.dp)
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "No upcoming alarms",
                                color = LumaWhite,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Set an alarm to start your morning with LUMA.",
                                color = LumaSecondaryText,
                                fontSize = 13.sp
                            )
                        }
                        Button(
                            onClick = onNavigateToAlarms,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = LumaSoftLavender)
                        ) {
                            Text(text = "Go to Alarms", color = LumaMidnightNavy, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Recent Sleep Summary",
                    color = LumaWhite,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                if (sleepSessions.isEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = LumaDeepBlue),
                        border = BorderStroke(1.dp, LumaCardBorder)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(text = "No sleep sessions recorded yet.", color = LumaSecondaryText, fontSize = 14.sp)
                            TextButton(onClick = { showLogDialog = true }) {
                                Text(text = "Tap here to log your first night", color = LumaSoftLavender)
                            }
                        }
                    }
                } else {
                    val latest = sleepSessions.first()
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = LumaDeepBlue),
                        border = BorderStroke(1.dp, LumaCardBorder)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = latest.date, color = LumaSecondaryText, fontSize = 14.sp)
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = LumaMidnightNavy
                                ) {
                                    Text(
                                        text = "⭐ ${latest.quality} / 5.0",
                                        color = LumaWarmCream,
                                        fontSize = 12.sp,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(text = "Bedtime", color = LumaSecondaryText, fontSize = 12.sp)
                                    Text(text = latest.bedtime, color = LumaWhite, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(text = "Duration", color = LumaSecondaryText, fontSize = 12.sp)
                                    val hours = latest.durationMinutes / 60
                                    val mins = latest.durationMinutes % 60
                                    Text(text = "${hours}h ${mins}m", color = LumaSoftLavender, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(text = "Wake up", color = LumaSecondaryText, fontSize = 12.sp)
                                    Text(text = latest.wakeUp, color = LumaWhite, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                                }
                            }

                            if (latest.notes.isNotBlank()) {
                                Text(
                                    text = "Notes: ${latest.notes}",
                                    color = LumaSecondaryText,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }

        if (showLogDialog) {
            AlertDialog(
                onDismissRequest = { showLogDialog = false },
                containerColor = LumaDeepBlue,
                title = { Text(text = "Log Sleep Session", color = LumaWhite) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(text = "How was your sleep quality (1 to 5)?", color = LumaSecondaryText, fontSize = 13.sp)
                        Slider(
                            value = qualityRating,
                            onValueChange = { qualityRating = it },
                            valueRange = 1f..5f,
                            steps = 8,
                            colors = SliderDefaults.colors(
                                thumbColor = LumaSoftLavender,
                                activeTrackColor = LumaSoftLavender,
                                inactiveTrackColor = LumaMidnightNavy
                            )
                        )
                        Text(text = "Rating: ${String.format(Locale.getDefault(), "%.1f", qualityRating)} / 5.0", color = LumaSoftLavender, fontWeight = FontWeight.Bold)
                        
                        OutlinedTextField(
                            value = sleepNotes,
                            onValueChange = { sleepNotes = it },
                            label = { Text("Personal Notes") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = LumaSoftLavender,
                                unfocusedBorderColor = LumaCardBorder,
                                focusedLabelColor = LumaSoftLavender,
                                unfocusedLabelColor = LumaSecondaryText,
                                focusedTextColor = LumaWhite,
                                unfocusedTextColor = LumaWhite
                            )
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        val newSession = SleepSession(
                            date = SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date()),
                            bedtime = "11:00 PM",
                            sleepOnset = "11:15 PM",
                            wakeUp = "07:00 AM",
                            durationMinutes = 465,
                            quality = qualityRating,
                            notes = sleepNotes
                        )
                        dbHelper.insertSleepSession(newSession)
                        sleepSessions = dbHelper.getAllSleepSessions()
                        showLogDialog = false
                    }) {
                        Text(text = "Save", color = LumaSoftLavender)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showLogDialog = false }) {
                        Text(text = "Cancel", color = LumaSecondaryText)
                    }
                }
            )
        }
    }
}

@Composable
fun ScienceOfSleepCard() {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = LumaDeepBlue),
        border = BorderStroke(1.dp, LumaCardBorder)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = LumaMidnightNavy,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = LumaSoftLavender,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Column {
                        Text(
                            text = "THE SCIENCE OF SLEEP",
                            color = LumaSoftLavender,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp
                        )
                        Text(
                            text = "Why sleep cycles matter",
                            color = LumaWhite,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = if (expanded) "Collapse" else "Expand",
                        tint = LumaLightBlue
                    )
                }
            }

            if (!expanded) {
                Text(
                    text = "Sleep isn't simply switching off. Throughout the night, your brain moves through different stages, each contributing to memory, learning, physical recovery...",
                    color = LumaSecondaryText,
                    fontSize = 13.sp,
                    maxLines = 2,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
            }

            if (expanded) {
                HorizontalDivider(color = LumaCardBorder, thickness = 1.dp)

                Text(
                    text = "Sleep isn't simply switching off. Throughout the night, your brain moves through different stages, each contributing to memory, learning, physical recovery, and emotional well-being.",
                    color = LumaSecondaryText,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    ScienceInfoItem(
                        title = "MEMORY & LEARNING",
                        description = "Sleep supports memory consolidation and helps your brain process information learned during the day."
                    )
                    ScienceInfoItem(
                        title = "PHYSICAL RECOVERY",
                        description = "Deep sleep supports physical recovery, tissue repair, and normal growth."
                    )
                    ScienceInfoItem(
                        title = "BRAIN & EMOTIONAL HEALTH",
                        description = "Different sleep stages contribute to brain function, emotional processing, and overall well-being."
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Sleep Stages Progression",
                        color = LumaWhite,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        StageBadge("N1", "Light sleep")
                        StageBadge("N2", "Stable light")
                        StageBadge("N3", "Deep sleep")
                        StageBadge("REM", "Dreaming")
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = LumaMidnightNavy,
                    border = BorderStroke(1.dp, LumaCardBorder)
                ) {
                    Text(
                        text = "Scientific Note: Sleep cycles typically last around 90 minutes, but their duration varies between people and throughout the night. There is no guarantee that waking at the end of a calculated cycle will make you feel more refreshed.",
                        color = LumaSecondaryText,
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        modifier = Modifier.padding(12.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF222B52),
                    border = BorderStroke(1.dp, LumaSoftLavender)
                ) {
                    Text(
                        text = "💡 Key Fact: Your goal isn't just to complete sleep cycles. It's to get enough quality sleep consistently.",
                        color = LumaSoftLavender,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 18.sp,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ScienceInfoItem(title: String, description: String) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(text = title, color = LumaLightBlue, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Text(text = description, color = LumaSecondaryText, fontSize = 13.sp, lineHeight = 18.sp)
    }
}

@Composable
fun StageBadge(stage: String, desc: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .background(LumaMidnightNavy, RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        Text(text = stage, color = LumaSoftLavender, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        Text(text = desc, color = LumaSecondaryText, fontSize = 10.sp)
    }
}
