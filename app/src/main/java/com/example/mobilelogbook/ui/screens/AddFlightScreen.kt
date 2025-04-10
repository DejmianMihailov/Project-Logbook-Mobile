package com.example.mobilelogbook.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mobilelogbook.data.FlightEntity
import com.example.mobilelogbook.repository.FlightRepository
import com.example.mobilelogbook.session.UserSession
import kotlinx.coroutines.launch

@Composable
fun AddFlightScreen(
    navController: NavController,
    repository: FlightRepository,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    var pilotName by remember { mutableStateOf("") }
    var departureAirport by remember { mutableStateOf("") }
    var arrivalAirport by remember { mutableStateOf("") }
    var departureTime by remember { mutableStateOf("") }
    var arrivalTime by remember { mutableStateOf("") }
    var aircraft by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Add Flight", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = pilotName,
            onValueChange = { pilotName = it },
            label = { Text("Pilot Name") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = departureAirport,
            onValueChange = { departureAirport = it },
            label = { Text("Departure Airport") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = arrivalAirport,
            onValueChange = { arrivalAirport = it },
            label = { Text("Arrival Airport") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = departureTime,
            onValueChange = { departureTime = it },
            label = { Text("Departure Time (e.g. 2025-04-08 12:00)") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = arrivalTime,
            onValueChange = { arrivalTime = it },
            label = { Text("Arrival Time (e.g. 2025-04-08 14:30)") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = aircraft,
            onValueChange = { aircraft = it },
            label = { Text("Aircraft") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                coroutineScope.launch {
                    if (pilotName.isBlank() || departureAirport.isBlank() || arrivalAirport.isBlank() ||
                        departureTime.isBlank() || arrivalTime.isBlank()
                    ) {
                        Toast.makeText(
                            context,
                            "Please fill in all required fields",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@launch
                    }

                    val newFlight = FlightEntity(
                        pilotName = pilotName.trim(),
                        departureAirport = departureAirport.trim(),
                        arrivalAirport = arrivalAirport.trim(),
                        departureTime = departureTime.trim(),
                        arrivalTime = arrivalTime.trim(),
                        aircraft = aircraft.trim(),
                        username = UserSession.getUsername() ?: "unknown",
                        status = "pending"
                    )
                    repository.addFlight(newFlight)
                    Toast.makeText(context, "Flight added", Toast.LENGTH_SHORT).show()
                    navController.popBackStack()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Flight")
        }
    }
}
