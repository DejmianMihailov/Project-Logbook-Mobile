package com.example.mobilelogbook.ui

import android.content.res.Configuration
import androidx.compose.ui.unit.dp
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
    val isUserLoggedIn = UserSession.getUsername() != null

    // 🛡️ Блокира достъпа до landscape layout ако потребителят не е логнат
    if (isLandscape && !isUserLoggedIn) {
        LoginScreen(navController = navController)
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
                    if (isUserLoggedIn) {
                        IconButton(onClick = {
                            UserSession.clear()
                            Toast.makeText(context, "Signed out successfully", Toast.LENGTH_SHORT).show()
                            navController.navigate("login") {
                                popUpTo("flightList") { inclusive = true }
                            }
                        }) {
                            Icon(Icons.Default.Logout, contentDescription = "Sign Out")
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            if (!isLandscape && isUserLoggedIn) {
                FloatingActionButton(onClick = {
                    navController.navigate("addFlight")
                }) {
                    Icon(Icons.Default.Add, contentDescription = "Add Flight")
                }
            }
        }
    ) { padding ->
        if (isLandscape && isUserLoggedIn) {
            // 🖥️ Landscape режим – два панела
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
            // 📱 Portrait режим – класическа навигация
            NavHost(
                navController = navController,
                startDestination = if (isUserLoggedIn) "flightList" else "login",
                modifier = Modifier.padding(padding)
            ) {
                composable("login") {
                    LoginScreen(navController)
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
