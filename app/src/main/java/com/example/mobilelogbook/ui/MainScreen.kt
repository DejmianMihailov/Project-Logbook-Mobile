package com.example.mobilelogbook.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
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
                    IconButton(onClick = {
                        UserSession.clear()
                        navController.navigate("login") {
                            popUpTo("flightList") { inclusive = true }
                        }
                    }) {
                        Icon(Icons.Default.Logout, contentDescription = "Logout")
                    }
                }
            )
        },
        floatingActionButton = {
            if (UserSession.username != null) {
                FloatingActionButton(onClick = {
                    navController.navigate("addFlight")
                }) {
                    Icon(Icons.Default.Add, contentDescription = "Add Flight")
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = if (UserSession.username != null) "flightList" else "login",
            modifier = Modifier.padding(padding)
        ) {
            composable("login") {
                LoginScreen(navController = navController)
            }
            composable("flightList") {
                FlightListScreen(
                    navController = navController,
                    repository = repository
                )
            }
            composable("addFlight") {
                AddFlightScreen(
                    navController = navController,
                    repository = repository,
                    themeViewModel = themeViewModel
                )
            }
        }
    }
}



