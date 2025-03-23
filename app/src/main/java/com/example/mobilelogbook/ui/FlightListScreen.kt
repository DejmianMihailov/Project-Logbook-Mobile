package com.example.mobilelogbook.ui

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mobilelogbook.repository.FlightRepository
import com.example.mobilelogbook.data.FlightEntity
import kotlinx.coroutines.launch

@Composable
fun FlightListScreen(navController: NavController, repository: FlightRepository) {
    var flights by remember { mutableStateOf<List<FlightEntity>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    fun loadFlights() {
        coroutineScope.launch {
            isLoading = true
            try {
                val localFlights = repository.getAllFlights()
                val remoteFlights = repository.getAllFlightsFromSupabase()
                flights = (localFlights + remoteFlights).distinctBy { it.id }
                Log.d("FlightListScreen", "Total flights: ${flights.size} (Local + Supabase)")
            } catch (e: Exception) {
                Log.e("FlightListScreen", "Error fetching flights: ${e.message}")
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(Unit) {
        loadFlights()
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Your Flights") })
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("addFlight") },
                backgroundColor = MaterialTheme.colors.primary
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add Flight")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Your Flights", style = MaterialTheme.typography.h5)

                Button(onClick = { loadFlights() }) {
                    Text("Refresh")
                }
            }

            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally).padding(16.dp))
            } else if (flights.isEmpty()) {
                Text(
                    "No flights found.",
                    modifier = Modifier.align(Alignment.CenterHorizontally).padding(16.dp)
                )
            } else {
                LazyColumn {
                    items(flights) { flight ->
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(8.dp),
                            elevation = 4.dp
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
