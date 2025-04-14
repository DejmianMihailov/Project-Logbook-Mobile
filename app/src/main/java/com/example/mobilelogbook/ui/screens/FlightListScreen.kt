package com.example.mobilelogbook.ui.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mobilelogbook.data.FlightEntity
import com.example.mobilelogbook.repository.FlightRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlightListScreen(
    navController: NavController,
    repository: FlightRepository,
    modifier: Modifier = Modifier,
    refreshTrigger: Boolean = false
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var flights by remember { mutableStateOf<List<FlightEntity>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }

    fun loadFlights() {
        coroutineScope.launch {
            isLoading = true
            try {
                val localFlights = repository.getAllFlights()
                val remoteFlights = repository.getFlightsForCurrentUser()
                flights = (localFlights + remoteFlights).distinctBy { it.id }
                Log.d("FlightListScreen", "✅ Loaded ${flights.size} total flights")
            } catch (e: Exception) {
                Log.e("FlightListScreen", "❌ Error loading flights: ${e.message}")
            } finally {
                isLoading = false
            }
        }
    }

    fun syncNow() {
        coroutineScope.launch {
            try {
                repository.syncFlights()
                Toast.makeText(context, "✅ Synced successfully", Toast.LENGTH_SHORT).show()
                loadFlights()
            } catch (e: Exception) {
                Toast.makeText(context, "❌ Sync failed: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    LaunchedEffect(refreshTrigger) {
        loadFlights()
    }

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(
                "Your Flights",
                style = MaterialTheme.typography.headlineSmall
            )
            IconButton(onClick = { syncNow() }) {
                Icon(Icons.Default.Sync, contentDescription = "Sync Flights")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (isLoading) {
            CircularProgressIndicator()
        } else if (flights.isEmpty()) {
            Text("No flights found.")
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
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
