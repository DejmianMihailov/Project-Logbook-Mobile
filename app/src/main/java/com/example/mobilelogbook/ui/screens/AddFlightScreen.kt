package com.example.mobilelogbook.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mobilelogbook.data.FlightEntity
import com.example.mobilelogbook.repository.FlightRepository
import com.example.mobilelogbook.session.UserSession
import com.example.mobilelogbook.ui.theme.ThemeViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFlightScreen(
    navController: NavController,
    repository: FlightRepository,
    themeViewModel: ThemeViewModel
) {
    val coroutineScope = rememberCoroutineScope()

    var pilotName by remember { mutableStateOf("") }
    var departureAirport by remember { mutableStateOf("") }
    var arrivalAirport by remember { mutableStateOf("") }
    var departureTime by remember { mutableStateOf("") }
    var arrivalTime by remember { mutableStateOf("") }
    var aircraft by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "Add New Flight",
            style = MaterialTheme.typography.headlineSmall
        )

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
            label = { Text("Departure Time (e.g. 2025-04-01 12:00)") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next)
        )

        OutlinedTextField(
            value = arrivalTime,
            onValueChange = { arrivalTime = it },
            label = { Text("Arrival Time (e.g. 2025-04-01 14:00)") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next)
        )

        OutlinedTextField(
            value = aircraft,
            onValueChange = { aircraft = it },
            label = { Text("Aircraft") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                coroutineScope.launch {
                    val username = UserSession.username ?: "unknown"

                    val flight = FlightEntity(
                        pilotName = pilotName,
                        departureAirport = departureAirport,
                        arrivalAirport = arrivalAirport,
                        departureTime = departureTime,
                        arrivalTime = arrivalTime,
                        aircraft = aircraft,
                        username = username
                    )

                    repository.addFlight(flight)
                    navController.popBackStack()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Flight")
        }
    }
}
