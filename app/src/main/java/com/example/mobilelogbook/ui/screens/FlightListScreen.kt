package com.example.mobilelogbook.ui.screens

import android.util.Log
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlightListScreen(
    navController: NavController,
    repository: FlightRepository,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    var flights by remember { mutableStateOf<List<FlightEntity>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    val username = UserSession.username

    fun loadFlights() {
        if (username == null) {
            Log.e("FlightListScreen", "Username not set, cannot load flights")
            return
        }

        coroutineScope.launch {
            isLoading = true
            try {
                val local = repository.getAllFlights()
                val remote = repository.getFlightsForCurrentUser()
                flights = (local + remote).distinctBy { it.id }
                Log.d("FlightListScreen", "Loaded ${flights.size} flights")
            } catch (e: Exception) {
                Log.e("FlightListScreen", "Error: ${e.message}")
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(Unit) {
        loadFlights()
    }

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Your Flights",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground
            )
            Button(onClick = { loadFlights() }) {
                Text("Refresh")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (isLoading) {
            CircularProgressIndicator()
        } else if (flights.isEmpty()) {
            Text("No flights found", color = MaterialTheme.colorScheme.onBackground)
        } else {
            LazyColumn {
                items(flights) { flight ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
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
