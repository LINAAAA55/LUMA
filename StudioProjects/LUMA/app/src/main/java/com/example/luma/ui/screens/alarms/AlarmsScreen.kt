package com.example.luma.ui.screens.alarms

import android.Manifest
import android.app.TimePickerDialog
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.luma.database.AlarmEntity
import com.example.luma.database.DatabaseHelper
import com.example.luma.services.AlarmScheduler
import com.example.luma.ui.theme.*

@Composable
fun AlarmsScreen() {
    val context = LocalContext.current
    val dbHelper = remember { DatabaseHelper(context) }
    var alarms by remember { mutableStateOf(dbHelper.getAllAlarms()) }
    var showAddDialog by remember { mutableStateOf(false) }

    var newAlarmHour by remember { mutableIntStateOf(7) }
    var newAlarmMinute by remember { mutableIntStateOf(30) }
    var newAlarmLabel by remember { mutableStateOf("Morning Alarm") }
    val selectedDays by remember { mutableStateOf("Mon, Tue, Wed, Thu, Fri") }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ -> }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = LumaMidnightNavy,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = LumaSoftLavender,
                contentColor = LumaMidnightNavy,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Alarm")
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(LumaMidnightNavy)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Alarms",
                        color = LumaWhite,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Independent scheduled alarms with Android AlarmManager.",
                        color = LumaSecondaryText,
                        fontSize = 14.sp
                    )
                }

                if (alarms.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No alarms set. Tap + to add one.",
                            color = LumaSecondaryText,
                            fontSize = 14.sp
                        )
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        items(alarms) { alarm ->
                            AlarmCard(
                                alarm = alarm,
                                onToggle = { enabled ->
                                    dbHelper.updateAlarmEnabled(alarm.id, enabled)
                                    if (enabled) {
                                        AlarmScheduler.scheduleAlarm(context, alarm.copy(isEnabled = true))
                                    } else {
                                        AlarmScheduler.cancelAlarm(context, alarm.id)
                                    }
                                    alarms = dbHelper.getAllAlarms()
                                },
                                onDelete = {
                                    AlarmScheduler.cancelAlarm(context, alarm.id)
                                    dbHelper.deleteAlarm(alarm.id)
                                    alarms = dbHelper.getAllAlarms()
                                }
                            )
                        }
                    }
                }
            }

            if (showAddDialog) {
                val is24HourFormat = android.text.format.DateFormat.is24HourFormat(context)
                val timePickerDialog = remember {
                    TimePickerDialog(
                        context,
                        { _, hour: Int, minute: Int ->
                            newAlarmHour = hour
                            newAlarmMinute = minute
                        },
                        newAlarmHour,
                        newAlarmMinute,
                        is24HourFormat
                    )
                }

                AlertDialog(
                    onDismissRequest = { showAddDialog = false },
                    containerColor = LumaDeepBlue,
                    title = { Text(text = "Create New Alarm", color = LumaWhite) },
                    text = {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            val amPm = if (newAlarmHour < 12) "AM" else "PM"
                            val hour12 = when {
                                newAlarmHour == 0 -> 12
                                newAlarmHour > 12 -> newAlarmHour - 12
                                else -> newAlarmHour
                            }
                            val timeDisplay = if (is24HourFormat) {
                                String.format("%02d:%02d", newAlarmHour, newAlarmMinute)
                            } else {
                                String.format("%02d:%02d %s", hour12, newAlarmMinute, amPm)
                            }

                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .clickable {
                                        timePickerDialog.show()
                                    },
                                color = LumaMidnightNavy,
                                border = BorderStroke(1.dp, LumaSoftLavender)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Schedule,
                                            contentDescription = null,
                                            tint = LumaSoftLavender,
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Column {
                                            Text(
                                                text = "Alarm Time",
                                                color = LumaSecondaryText,
                                                fontSize = 12.sp
                                            )
                                            Text(
                                                text = timeDisplay,
                                                color = LumaWhite,
                                                fontSize = 22.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                    Text(
                                        text = "Tap to change",
                                        color = LumaLightBlue,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }

                            OutlinedTextField(
                                value = newAlarmLabel,
                                onValueChange = { newAlarmLabel = it },
                                label = { Text("Alarm Label") },
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
                            val newAlarm = AlarmEntity(
                                hour = newAlarmHour,
                                minute = newAlarmMinute,
                                label = newAlarmLabel.ifBlank { "Alarm" },
                                isEnabled = true,
                                repeatDays = selectedDays,
                                sound = "default",
                                vibration = true
                            )
                            val insertedId = dbHelper.insertAlarm(newAlarm)
                            val savedAlarm = newAlarm.copy(id = insertedId)
                            AlarmScheduler.scheduleAlarm(context, savedAlarm)
                            alarms = dbHelper.getAllAlarms()
                            showAddDialog = false
                        }) {
                            Text(text = "Save & Schedule", color = LumaSoftLavender, fontWeight = FontWeight.Bold)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showAddDialog = false }) {
                            Text(text = "Cancel", color = LumaSecondaryText)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun AlarmCard(alarm: AlarmEntity, onToggle: (Boolean) -> Unit, onDelete: () -> Unit) {
    val hour24 = alarm.hour
    val minute = alarm.minute
    val amPm = if (hour24 < 12) "AM" else "PM"
    val hour12 = when {
        hour24 == 0 -> 12
        hour24 > 12 -> hour24 - 12
        else -> hour24
    }
    val timeFormatted = String.format("%02d:%02d", hour12, minute)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = LumaDeepBlue),
        border = BorderStroke(1.dp, LumaCardBorder)
    ) {
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
                Icon(
                    imageVector = Icons.Default.Alarm,
                    contentDescription = null,
                    tint = if (alarm.isEnabled) LumaSoftLavender else LumaSecondaryText,
                    modifier = Modifier.size(28.dp)
                )
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = timeFormatted,
                            color = if (alarm.isEnabled) LumaWhite else LumaSecondaryText,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = amPm,
                            color = if (alarm.isEnabled) LumaSoftLavender else LumaSecondaryText,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }
                    Text(
                        text = "${alarm.label} • ${alarm.repeatDays}",
                        color = LumaSecondaryText,
                        fontSize = 12.sp
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(onClick = onDelete) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = LumaSecondaryText, modifier = Modifier.size(20.dp))
                }
                Switch(
                    checked = alarm.isEnabled,
                    onCheckedChange = onToggle,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = LumaMidnightNavy,
                        checkedTrackColor = LumaSoftLavender,
                        uncheckedThumbColor = LumaSecondaryText,
                        uncheckedTrackColor = LumaMidnightNavy
                    )
                )
            }
        }
    }
}
