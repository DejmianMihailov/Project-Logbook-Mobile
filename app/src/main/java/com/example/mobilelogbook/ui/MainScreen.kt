package com.example.mobilelogbook.ui

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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

    val isUserLoggedIn = remember { mutableStateOf(UserSession.getUsername() != null) }
    var refreshTrigger by remember { mutableStateOf(false) }
    var showAddFlight by remember { mutableStateOf(false) }

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
                    if (isUserLoggedIn.value) {
                        IconButton(onClick = {
                            UserSession.clear()
                            isUserLoggedIn.value = false
                            showAddFlight = false
                            Toast.makeText(context, "Signed out", Toast.LENGTH_SHORT).show()
                            if (!isLandscape) {
                                navController.navigate("login") {
                                    popUpTo("flightList") { inclusive = true }
                                }
                            }
                        }) {
                            Icon(Icons.Default.Logout, contentDescription = "Logout")
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            if (!isLandscape && isUserLoggedIn.value) {
                FloatingActionButton(onClick = {
                    navController.navigate("addFlight")
                }) {
                    Icon(Icons.Default.Add, contentDescription = "Add Flight")
                }
            }
        }
    ) { padding ->
        if (isLandscape && isUserLoggedIn.value) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                FlightListScreen(
                    navController = navController,
                    repository = repository,
                    modifier = Modifier.weight(1f),
                    refreshTrigger = refreshTrigger
                )

                if (showAddFlight) {
                    AddFlightScreen(
                        navController = navController,
                        repository = repository,
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 8.dp),
                        onFlightSaved = {
                            refreshTrigger = !refreshTrigger
                            showAddFlight = false
                        },
                        onBackToList = {
                            showAddFlight = false
                        }
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(32.dp)
                    ) {
                        Button(onClick = { showAddFlight = true }) {
                            Text("➕ Add Flight")
                        }
                    }
                }
            }
        } else if (!isUserLoggedIn.value) {
            NavHost(
                navController = navController,
                startDestination = "login",
                modifier = Modifier.padding(padding)
            ) {
                composable("login") {
                    LoginScreen(
                        navController = navController,
                        onLoginSuccess = {
                            isUserLoggedIn.value = true
                            refreshTrigger = !refreshTrigger
                            navController.navigate("flightList") {
                                popUpTo("login") { inclusive = true }
                            }
                        },
                        themeViewModel = themeViewModel
                    )
                }
            }
        } else {
            NavHost(
                navController = navController,
                startDestination = "flightList",
                modifier = Modifier.padding(padding)
            ) {
                composable("flightList") {
                    FlightListScreen(
                        navController = navController,
                        repository = repository,
                        refreshTrigger = refreshTrigger
                    )
                }
                composable("addFlight") {
                    AddFlightScreen(
                        navController = navController,
                        repository = repository,
                        onFlightSaved = {
                            refreshTrigger = !refreshTrigger
                            navController.popBackStack()
                        }
                    )
                }
            }
        }
    }
}
