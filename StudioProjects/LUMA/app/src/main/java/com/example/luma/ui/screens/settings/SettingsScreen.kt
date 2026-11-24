package com.example.luma.ui.screens.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.luma.ui.theme.*

@Composable
fun SettingsScreen() {
    val scrollState = rememberScrollState()
    var is24HourFormat by remember { mutableStateOf(false) }
    var vibrationEnabled by remember { mutableStateOf(true) }
    var notificationsEnabled by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LumaMidnightNavy)
            .verticalScroll(scrollState)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = "Settings",
                color = LumaWhite,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Customize your sleep tracking and alarm experience.",
                color = LumaSecondaryText,
                fontSize = 14.sp
            )
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = LumaDeepBlue),
            border = BorderStroke(1.dp, LumaCardBorder)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Preferences",
                    color = LumaSoftLavender,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                SettingToggleRow(
                    icon = Icons.Default.AccessTime,
                    title = "24-Hour Time Format",
                    subtitle = "Use 24h instead of AM/PM",
                    checked = is24HourFormat,
                    onCheckedChange = { is24HourFormat = it }
                )

                SettingToggleRow(
                    icon = Icons.Default.Vibration,
                    title = "Vibration",
                    subtitle = "Vibrate when alarm triggers",
                    checked = vibrationEnabled,
                    onCheckedChange = { vibrationEnabled = it }
                )

                SettingToggleRow(
                    icon = Icons.Default.Notifications,
                    title = "Alarm Notifications",
                    subtitle = "Allow scheduled alarms and notifications",
                    checked = notificationsEnabled,
                    onCheckedChange = { notificationsEnabled = it }
                )
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = LumaDeepBlue),
            border = BorderStroke(1.dp, LumaCardBorder)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Privacy & Data",
                    color = LumaSoftLavender,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = LumaLightBlue)
                        Column {
                            Text(text = "Local-First Storage", color = LumaWhite, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                            Text(text = "All sleep logs stay on your device securely.", color = LumaSecondaryText, fontSize = 12.sp)
                        }
                    }
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
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "About LUMA",
                    color = LumaSoftLavender,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "LUMA — Wake Up to Your Rhythm\nVersion 1.0.0 (Academic Project)\nDesigned for optimal sleep tracking and natural rhythm scheduling.",
                    color = LumaSecondaryText,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
fun SettingToggleRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = LumaLightBlue)
            Column {
                Text(text = title, color = LumaWhite, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                Text(text = subtitle, color = LumaSecondaryText, fontSize = 12.sp)
            }
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = LumaMidnightNavy,
                checkedTrackColor = LumaSoftLavender,
                uncheckedThumbColor = LumaSecondaryText,
                uncheckedTrackColor = LumaMidnightNavy
            )
        )
    }
}
