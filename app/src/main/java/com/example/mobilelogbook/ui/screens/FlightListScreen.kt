package com.example.mobilelogbook.ui.screens

import android.util.Log
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mobilelogbook.data.FlightEntity
import com.example.mobilelogbook.repository.FlightRepository
import com.example.mobilelogbook.session.UserSession
import kotlinx.coroutines.launch
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState

@Composable
fun FlightListScreen(
    navController: NavController,
    repository: FlightRepository,
    modifier: Modifier = Modifier,
    refreshTrigger: Boolean = false
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    var flights by remember { mutableStateOf<List<FlightEntity>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }

    fun loadFlights() {
        coroutineScope.launch {
            isLoading = true
            try {
                flights = repository.getFlightsForCurrentUser()
                Log.d("FlightListScreen", "Loaded ${flights.size} flights")
            } catch (e: Exception) {
                Log.e("FlightListScreen", "Load error: ${e.message}")
            } finally {
                isLoading = false
            }
        }
    }

    fun syncToSupabase() {
        coroutineScope.launch {
            try {
                repository.syncFlights()
                snackbarHostState.showSnackbar("✅ Synced successfully with Supabase")
                loadFlights()
            } catch (e: Exception) {
                snackbarHostState.showSnackbar("❌ Sync failed: ${e.message}")
            }
        }
    }

    LaunchedEffect(Unit, refreshTrigger) {
        loadFlights()
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Your Flights",
                    style = MaterialTheme.typography.headlineSmall
                )
                Row {
                    Button(onClick = { loadFlights() }) {
                        Text("Refresh")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = { syncToSupabase() }) {
                        Text("🔄 Sync")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                CircularProgressIndicator()
            } else if (flights.isEmpty()) {
                Text("No flights found.")
            } else {
                LazyColumn {
                    items(flights) { flight ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Pilot: ${flight.pilotName}")
                                Text("Aircraft: ${flight.aircraft}")
                                Text("${flight.departureAirport} → ${flight.arrivalAirport}")
                                Text("Departure: ${flight.departureTime}")
                                Text("Arrival: ${flight.arrivalTime}")
                            }
                        }
                    }
                }
            }
        }
    }
}