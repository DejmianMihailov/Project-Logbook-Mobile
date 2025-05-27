package com.example.mobilelogbook.ui.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mobilelogbook.data.FlightEntity
import com.example.mobilelogbook.session.UserSession
import com.example.mobilelogbook.ui.theme.ThemeViewModel
import com.example.mobilelogbook.viewmodel.MobileFlightViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navController: NavController,
    flightViewModel: MobileFlightViewModel,
    themeViewModel: ThemeViewModel
) {
    val context = LocalContext.current
    val flights by flightViewModel.flights.collectAsState()
    val isDarkTheme by themeViewModel.isDarkTheme.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val username = UserSession.getUsername() ?: "Unknown"

    LaunchedEffect(Unit) {
        flightViewModel.loadFlightsForCurrentUser()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Flight Logbook") },
                actions = {
                    // Toggle Theme
                    IconButton(onClick = { themeViewModel.toggleTheme() }) {
                        Icon(
                            imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "Toggle Theme"
                        )
                    }
                    // Manual Sync
                    IconButton(onClick = {
                        coroutineScope.launch {
                            Log.d("MainScreen", "📡 Manual sync triggered")
                            flightViewModel.syncFromServerIfOnline()
                            Toast.makeText(context, "Synced with server (if online)", Toast.LENGTH_SHORT).show()
                        }
                    }) {
                        Icon(Icons.Default.CloudSync, contentDescription = "Sync")
                    }
                    // Profile Section - подаване на flightCount към savedStateHandle!
                    IconButton(onClick = {
                        navController.currentBackStackEntry
                            ?.savedStateHandle
                            ?.set("flightCount", flights.size)
                        navController.navigate("profile")
                    }) {
                        Icon(Icons.Default.Person, contentDescription = "Profile")
                    }
                    // Logout
                    IconButton(onClick = {
                        UserSession.clear()
                        Toast.makeText(context, "Signed out", Toast.LENGTH_SHORT).show()
                        navController.navigate("login") {
                            popUpTo("main") { inclusive = true }
                        }
                    }) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Logout")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                navController.navigate("addFlight")
            }) {
                Icon(Icons.Default.Add, contentDescription = "Add Flight")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text(text = "Logged in as: $username", style = MaterialTheme.typography.titleSmall)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Total flights: ${flights.size}")
            Spacer(modifier = Modifier.height(16.dp))

            if (flights.isEmpty()) {
                Text("No flights found.")
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(flights) { flight ->
                        FlightItem(flight)
                    }
                }
            }
        }
    }
}

@Composable
fun FlightItem(flight: FlightEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text("✈Flight ${flight.departureAirport} ➡ ${flight.arrivalAirport}")
            Text("👨‍✈️ ${flight.pilotName}")
            Text("🕒 Duration: ${flight.flightDuration} min")
            Text("✅ Synced: ${if (flight.synced) "Yes" else "No"}")
        }
    }
}
