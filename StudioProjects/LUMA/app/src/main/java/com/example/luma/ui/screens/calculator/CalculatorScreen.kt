package com.example.luma.ui.screens.calculator

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AlarmAdd
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.WbSunny
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
import com.example.luma.services.AlarmScheduler
import com.example.luma.services.SleepCalculatorService
import com.example.luma.services.SleepSuggestion
import com.example.luma.ui.theme.*
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun CalculatorScreen() {
    val context = LocalContext.current
    val dbHelper = remember { DatabaseHelper(context) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    var selectedTab by remember { mutableIntStateOf(0) }
    
    var bedHour by remember { mutableIntStateOf(23) }
    var bedMinute by remember { mutableIntStateOf(0) }
    var sleepLatencyMinutes by remember { mutableIntStateOf(15) }

    var wakeHour by remember { mutableIntStateOf(7) }
    var wakeMinute by remember { mutableIntStateOf(0) }

    var showAlarmDialog by remember { mutableStateOf(false) }
    var dialogAlarmHour by remember { mutableIntStateOf(7) }
    var dialogAlarmMinute by remember { mutableIntStateOf(30) }
    var dialogAlarmLabel by remember { mutableStateOf("Wake Up (LUMA)") }

    val calculatorService = remember { SleepCalculatorService() }

    val wakeUpSuggestions = remember(bedHour, bedMinute, sleepLatencyMinutes) {
        calculatorService.calculateWakeUpTimes(bedHour, bedMinute, sleepLatencyMinutes)
    }

    val bedtimeSuggestions = remember(wakeHour, wakeMinute, sleepLatencyMinutes) {
        calculatorService.calculateBedtimes(wakeHour, wakeMinute, sleepLatencyMinutes)
    }

    val scrollState = rememberScrollState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = LumaMidnightNavy,
        snackbarHost = { SnackbarHost(snackbarHostState) }
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
                    .verticalScroll(scrollState)
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Sleep Calculator",
                        color = LumaWhite,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Calculate optimal sleep schedules and set alarms instantly.",
                        color = LumaSecondaryText,
                        fontSize = 14.sp
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(LumaDeepBlue, RoundedCornerShape(16.dp))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    TabButton(
                        title = "When to Wake Up",
                        icon = Icons.Default.WbSunny,
                        isSelected = selectedTab == 0,
                        modifier = Modifier.weight(1f),
                        onClick = { selectedTab = 0 }
                    )
                    TabButton(
                        title = "When to Sleep",
                        icon = Icons.Default.Bedtime,
                        isSelected = selectedTab == 1,
                        modifier = Modifier.weight(1f),
                        onClick = { selectedTab = 1 }
                    )
                }

                if (selectedTab == 0) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = LumaDeepBlue),
                        border = BorderStroke(1.dp, LumaCardBorder)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = "I plan to go to sleep at:",
                                color = LumaSoftLavender,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TimeSpinner(
                                    value = bedHour,
                                    label = "Hour",
                                    onIncrement = { bedHour = (bedHour + 1) % 24 },
                                    onDecrement = { bedHour = if (bedHour > 0) bedHour - 1 else 23 }
                                )
                                Text(text = ":", color = LumaWhite, fontSize = 28.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp))
                                TimeSpinner(
                                    value = bedMinute,
                                    label = "Minute",
                                    onIncrement = { bedMinute = (bedMinute + 5) % 60 },
                                    onDecrement = { bedMinute = if (bedMinute >= 5) bedMinute - 5 else 55 }
                                )
                            }

                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    text = "Estimated time to fall asleep: $sleepLatencyMinutes mins",
                                    color = LumaSecondaryText,
                                    fontSize = 14.sp
                                )
                                Slider(
                                    value = sleepLatencyMinutes.toFloat(),
                                    onValueChange = { sleepLatencyMinutes = it.toInt() },
                                    valueRange = 5f..60f,
                                    steps = 11,
                                    colors = SliderDefaults.colors(
                                        thumbColor = LumaSoftLavender,
                                        activeTrackColor = LumaSoftLavender,
                                        inactiveTrackColor = LumaMidnightNavy
                                    )
                                )
                            }
                        }
                    }

                    Text(
                        text = "Suggested Wake-up Times",
                        color = LumaWhite,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    wakeUpSuggestions.forEach { suggestion ->
                        SuggestionResultCard(
                            suggestion = suggestion,
                            onSetAlarm = { h, m ->
                                dialogAlarmHour = h
                                dialogAlarmMinute = m
                                dialogAlarmLabel = "Wake Up (${suggestion.durationFormatted})"
                                showAlarmDialog = true
                            }
                        )
                    }

                } else {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = LumaDeepBlue),
                        border = BorderStroke(1.dp, LumaCardBorder)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = "I need to wake up at:",
                                color = LumaLightBlue,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TimeSpinner(
                                    value = wakeHour,
                                    label = "Hour",
                                    onIncrement = { wakeHour = (wakeHour + 1) % 24 },
                                    onDecrement = { wakeHour = if (wakeHour > 0) wakeHour - 1 else 23 }
                                )
                                Text(text = ":", color = LumaWhite, fontSize = 28.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp))
                                TimeSpinner(
                                    value = wakeMinute,
                                    label = "Minute",
                                    onIncrement = { wakeMinute = (wakeMinute + 5) % 60 },
                                    onDecrement = { wakeMinute = if (wakeMinute >= 5) wakeMinute - 5 else 55 }
                                )
                            }

                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    text = "Estimated time to fall asleep: $sleepLatencyMinutes mins",
                                    color = LumaSecondaryText,
                                    fontSize = 14.sp
                                )
                                Slider(
                                    value = sleepLatencyMinutes.toFloat(),
                                    onValueChange = { sleepLatencyMinutes = it.toInt() },
                                    valueRange = 5f..60f,
                                    steps = 11,
                                    colors = SliderDefaults.colors(
                                        thumbColor = LumaLightBlue,
                                        activeTrackColor = LumaLightBlue,
                                        inactiveTrackColor = LumaMidnightNavy
                                    )
                                )
                            }
                        }
                    }

                    Text(
                        text = "Suggested Bedtimes",
                        color = LumaWhite,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    bedtimeSuggestions.forEach { suggestion ->
                        SuggestionResultCard(
                            suggestion = suggestion,
                            onSetAlarm = { h, m ->
                                dialogAlarmHour = h
                                dialogAlarmMinute = m
                                dialogAlarmLabel = "Bedtime (${suggestion.durationFormatted})"
                                showAlarmDialog = true
                            }
                        )
                    }
                }

                Text(
                    text = "Note: Sleep cycles typically last around 90 minutes and vary between individuals. Teenagers benefit from 8-10 hours of rest. These are estimates, not medical predictions.",
                    color = LumaSecondaryText,
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )

                Spacer(modifier = Modifier.height(40.dp))
            }

            if (showAlarmDialog) {
                AlertDialog(
                    onDismissRequest = { showAlarmDialog = false },
                    containerColor = LumaDeepBlue,
                    title = { Text(text = "Set Alarm from Calculation", color = LumaWhite) },
                    text = {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            val amPm = if (dialogAlarmHour < 12) "AM" else "PM"
                            val h12 = when {
                                dialogAlarmHour == 0 -> 12
                                dialogAlarmHour > 12 -> dialogAlarmHour - 12
                                else -> dialogAlarmHour
                            }
                            val timeStr = String.format(Locale.getDefault(), "%02d:%02d %s", h12, dialogAlarmMinute, amPm)

                            Text(
                                text = "Time: $timeStr",
                                color = LumaSoftLavender,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            OutlinedTextField(
                                value = dialogAlarmLabel,
                                onValueChange = { dialogAlarmLabel = it },
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
                            try {
                                val newAlarm = AlarmEntity(
                                    hour = dialogAlarmHour,
                                    minute = dialogAlarmMinute,
                                    label = dialogAlarmLabel.ifBlank { "LUMA Alarm" },
                                    isEnabled = true,
                                    repeatDays = "Mon, Tue, Wed, Thu, Fri",
                                    sound = "default",
                                    vibration = true
                                )
                                val insertedId = dbHelper.insertAlarm(newAlarm)
                                val savedAlarm = newAlarm.copy(id = insertedId)
                                AlarmScheduler.scheduleAlarm(context, savedAlarm)
                                showAlarmDialog = false

                                val amPm = if (dialogAlarmHour < 12) "AM" else "PM"
                                val h12 = when {
                                    dialogAlarmHour == 0 -> 12
                                    dialogAlarmHour > 12 -> dialogAlarmHour - 12
                                    else -> dialogAlarmHour
                                }
                                val formattedTime = String.format(Locale.getDefault(), "%02d:%02d %s", h12, dialogAlarmMinute, amPm)

                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar(
                                        message = "Alarm successfully scheduled! Your alarm is set for $formattedTime."
                                    )
                                }
                            } catch (e: Exception) {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar(
                                        message = "Failed to schedule alarm: ${e.localizedMessage}"
                                    )
                                }
                            }
                        }) {
                            Text(text = "Save & Schedule Alarm", color = LumaSoftLavender, fontWeight = FontWeight.Bold)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showAlarmDialog = false }) {
                            Text(text = "Cancel", color = LumaSecondaryText)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun TabButton(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) LumaSoftLavender else Color.Transparent
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) LumaMidnightNavy else LumaSecondaryText,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = title,
                color = if (isSelected) LumaMidnightNavy else LumaSecondaryText,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun TimeSpinner(
    value: Int,
    label: String,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        IconButton(onClick = onIncrement, modifier = Modifier.size(24.dp)) {
            Icon(imageVector = Icons.Default.KeyboardArrowUp, contentDescription = "Increase", tint = LumaSoftLavender)
        }
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = LumaMidnightNavy,
            border = BorderStroke(1.dp, LumaCardBorder),
            modifier = Modifier.size(64.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = String.format(Locale.getDefault(), "%02d", value),
                    color = LumaWhite,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        IconButton(onClick = onDecrement, modifier = Modifier.size(24.dp)) {
            Icon(imageVector = Icons.Default.KeyboardArrowDown, contentDescription = "Decrease", tint = LumaSoftLavender)
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = label, color = LumaSecondaryText, fontSize = 11.sp)
    }
}

@Composable
fun SuggestionResultCard(suggestion: SleepSuggestion, onSetAlarm: (Int, Int) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (suggestion.isRecommended) Color(0xFF222B52) else LumaDeepBlue
        ),
        border = BorderStroke(1.dp, if (suggestion.isRecommended) LumaSoftLavender else LumaCardBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = suggestion.timeFormatted,
                    color = if (suggestion.isRecommended) LumaSoftLavender else LumaWhite,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = suggestion.description,
                    color = LumaSecondaryText,
                    fontSize = 13.sp
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (suggestion.isRecommended) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = LumaSoftLavender
                    ) {
                        Text(
                            text = "BEST",
                            color = LumaMidnightNavy,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Button(
                    onClick = { onSetAlarm(suggestion.hour, suggestion.minute) },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = LumaMidnightNavy),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AlarmAdd,
                        contentDescription = "Set Alarm",
                        tint = LumaSoftLavender,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Set Alarm",
                        color = LumaWhite,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
