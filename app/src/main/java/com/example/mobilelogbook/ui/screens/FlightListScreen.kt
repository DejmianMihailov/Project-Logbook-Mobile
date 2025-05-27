package com.example.mobilelogbook.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mobilelogbook.session.UserSession
import com.example.mobilelogbook.viewmodel.MobileFlightViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlightListScreen(
    navController: NavController,
    flightViewModel: MobileFlightViewModel,
    onAddFlightClick: () -> Unit,
    onSignOut: () -> Unit
) {
    val flights by flightViewModel.flights.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        flightViewModel.loadFlightsForCurrentUser()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Flight List") },
                actions = {
                    IconButton(onClick = {
                        UserSession.clear()
                        Toast.makeText(context, "Signed out", Toast.LENGTH_SHORT).show()
                        onSignOut()
                    }) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Sign Out")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddFlightClick,
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Flight")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            if (flights.isEmpty()) {
                Text("No flights available")
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(flights) { flight ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("Pilot: ${flight.pilotName}")
                                Text("From: ${flight.departureAirport}")
                                Text("To: ${flight.arrivalAirport}")
                                Text("Time: ${flight.departureTime}")
                            }
                        }
                    }
                }
            }
        }
    }
}
