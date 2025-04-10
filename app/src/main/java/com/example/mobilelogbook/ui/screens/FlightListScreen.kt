package com.example.mobilelogbook.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
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
    modifier: Modifier = Modifier
) {
    var flights by remember { mutableStateOf<List<FlightEntity>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var syncMessage by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    fun loadFlights() {
        coroutineScope.launch {
            isLoading = true
            try {
                val localFlights = repository.getAllFlights()
                val remoteFlights = repository.getFlightsForCurrentUser()
                flights = (localFlights + remoteFlights).distinctBy { it.id }
                Log.d("FlightListScreen", "Loaded ${flights.size} total flights")
            } catch (e: Exception) {
                Log.e("FlightListScreen", "Error loading flights: ${e.message}")
            } finally {
                isLoading = false
            }
        }
    }

    fun syncFlights() {
        coroutineScope.launch {
            try {
                repository.syncFlights()
                syncMessage = "Sync successful"
                Log.d("FlightListScreen", "Synced flights to Supabase")
                loadFlights()
            } catch (e: Exception) {
                syncMessage = "Error during sync: ${e.message}"
                Log.e("FlightListScreen", "Sync error: ${e.message}")
            }
        }
    }

    LaunchedEffect(Unit) {
        loadFlights()
    }

    Column(
        modifier = modifier
            .fillMaxHeight()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Your Flights", style = MaterialTheme.typography.headlineSmall)
            IconButton(onClick = { syncFlights() }) {
                Icon(Icons.Default.CloudUpload, contentDescription = "Sync")
            }
        }

        if (syncMessage.isNotEmpty()) {
            Text(syncMessage, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (isLoading) {
            CircularProgressIndicator()
        } else if (flights.isEmpty()) {
            Text("No flights found.")
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxHeight()
            ) {
                items(flights) { flight ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Pilot: ${flight.pilotName}")
                            Text("Aircraft: ${flight.aircraft}")
                            Text("${flight.departureAirport} → ${flight.arrivalAirport}")
                            Text("Departure: ${flight.departureTime}")
                            Text("Arrival: ${flight.arrivalTime}")
                            Text("Status: ${flight.status}")
                        }
                    }
                }
            }
        }
    }
}
