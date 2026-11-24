package com.example.luma

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.sp
import com.example.luma.ui.screens.alarms.AlarmsScreen
import com.example.luma.ui.screens.calculator.CalculatorScreen
import com.example.luma.ui.screens.home.HomeScreen
import com.example.luma.ui.screens.insights.InsightsScreen
import com.example.luma.ui.screens.settings.SettingsScreen
import com.example.luma.ui.theme.LUMATheme
import com.example.luma.ui.theme.LumaDeepBlue
import com.example.luma.ui.theme.LumaMidnightNavy
import com.example.luma.ui.theme.LumaSoftLavender
import com.example.luma.ui.theme.LumaSecondaryText
import com.example.luma.ui.theme.LumaWhite

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Home : Screen("home", "Home", Icons.Default.Home)
    object Calculator : Screen("calculator", "Calculator", Icons.Default.Calculate)
    object Alarms : Screen("alarms", "Alarms", Icons.Default.Alarm)
    object Insights : Screen("insights", "Insights", Icons.Default.Insights)
    object Settings : Screen("settings", "Settings", Icons.Default.Settings)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LUMATheme {
                LumaMainApp()
            }
        }
    }
}

@Composable
fun LumaMainApp() {
    var selectedScreen by remember { mutableStateOf<Screen>(Screen.Home) }
    val items = listOf(
        Screen.Home,
        Screen.Calculator,
        Screen.Alarms,
        Screen.Insights,
        Screen.Settings
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                containerColor = LumaDeepBlue,
                contentColor = LumaWhite
            ) {
                items.forEach { screen ->
                    val isSelected = selectedScreen == screen
                    NavigationBarItem(
                        icon = { Icon(imageVector = screen.icon, contentDescription = screen.title) },
                        label = { Text(text = screen.title, fontSize = 11.sp) },
                        selected = isSelected,
                        onClick = { selectedScreen = screen },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = LumaMidnightNavy,
                            selectedTextColor = LumaSoftLavender,
                            indicatorColor = LumaSoftLavender,
                            unselectedIconColor = LumaSecondaryText,
                            unselectedTextColor = LumaSecondaryText
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = LumaMidnightNavy
        ) {
            when (selectedScreen) {
                Screen.Home -> HomeScreen(
                    onNavigateToCalculator = { selectedScreen = Screen.Calculator },
                    onNavigateToAlarms = { selectedScreen = Screen.Alarms }
                )
                Screen.Calculator -> CalculatorScreen()
                Screen.Alarms -> AlarmsScreen()
                Screen.Insights -> InsightsScreen()
                Screen.Settings -> SettingsScreen()
            }
        }
    }
}
