package com.example.mobilelogbook.ui

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.*
import com.example.mobilelogbook.repository.FlightRepository
import com.example.mobilelogbook.session.UserSession
import com.example.mobilelogbook.ui.screens.AddFlightScreen
import com.example.mobilelogbook.ui.screens.FlightListScreen
import com.example.mobilelogbook.ui.screens.LoginScreen
import com.example.mobilelogbook.ui.theme.ThemeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    repository: FlightRepository,
    themeViewModel: ThemeViewModel
) {
    val navController = rememberNavController()
    val isDarkTheme by themeViewModel.isDarkTheme.collectAsState()
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    // 🔁 Реактивно състояние за логнат потребител
    val usernameState = remember { mutableStateOf(UserSession.getUsername()) }

    // 🛡️ Ако не е логнат и е в landscape → показваме Login ръчно
    if (isLandscape && usernameState.value == null) {
        LoginScreen(
            navController = navController,
            themeViewModel = themeViewModel,
            onLoginSuccess = {
                usernameState.value = UserSession.getUsername()
            }
        )
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mobile Logbook") },
                actions = {
                    IconButton(onClick = { themeViewModel.toggleTheme() }) {
                        Icon(
                            imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "Toggle Theme"
                        )
                    }
                    if (usernameState.value != null) {
                        IconButton(onClick = {
                            UserSession.clear()
                            usernameState.value = null
                            Toast.makeText(context, "Signed out successfully", Toast.LENGTH_SHORT).show()

                            // ❗️ Навигация само в portrait
                            if (!isLandscape) {
                                navController.navigate("login") {
                                    popUpTo("flightList") { inclusive = true }
                                }
                            }
                        }) {
                            Icon(Icons.Default.Logout, contentDescription = "Sign Out")
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            if (!isLandscape && usernameState.value != null) {
                FloatingActionButton(onClick = {
                    navController.navigate("addFlight")
                }) {
                    Icon(Icons.Default.Add, contentDescription = "Add Flight")
                }
            }
        }
    ) { padding ->
        if (isLandscape && usernameState.value != null) {
            // 💻 Split layout на таблет
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                FlightListScreen(
                    navController = navController,
                    repository = repository,
                    modifier = Modifier.weight(1f)
                )
                AddFlightScreen(
                    navController = navController,
                    repository = repository,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp)
                )
            }
        } else {
            // 📱 Навигация за телефон/portrait
            NavHost(
                navController = navController,
                startDestination = if (usernameState.value != null) "flightList" else "login",
                modifier = Modifier.padding(padding)
            ) {
                composable("login") {
                    LoginScreen(
                        navController = navController,
                        themeViewModel = themeViewModel,
                        onLoginSuccess = {
                            usernameState.value = UserSession.getUsername()
                            // ✅ Само в portrait навигираме
                            if (!isLandscape) {
                                navController.navigate("flightList") {
                                    popUpTo("login") { inclusive = true }
                                }
                            }
                        }
                    )
                }
                composable("flightList") {
                    FlightListScreen(navController, repository)
                }
                composable("addFlight") {
                    AddFlightScreen(navController, repository)
                }
            }
        }
    }
}
