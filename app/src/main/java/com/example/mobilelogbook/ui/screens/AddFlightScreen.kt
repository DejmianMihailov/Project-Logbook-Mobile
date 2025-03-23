package com.example.mobilelogbook.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mobilelogbook.data.FlightEntity
import com.example.mobilelogbook.repository.FlightRepository
import kotlinx.coroutines.launch

@Composable
fun AddFlightScreen(navController: NavController, repository: FlightRepository) {
    var pilotName by remember { mutableStateOf("") }
    var departureAirport by remember { mutableStateOf("") }
    var arrivalAirport by remember { mutableStateOf("") }
    var departureTime by remember { mutableStateOf("") }
    var arrivalTime by remember { mutableStateOf("") }
    var aircraft by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Add Flight", style = MaterialTheme.typography.h5)

        Spacer(modifier = Modifier.height(8.dp))

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
            label = { Text("Departure Time (YYYY-MM-DD HH:MM)") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions.Default
        )

        OutlinedTextField(
            value = arrivalTime,
            onValueChange = { arrivalTime = it },
            label = { Text("Arrival Time (YYYY-MM-DD HH:MM)") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions.Default
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
                    val newFlight = FlightEntity(
                        id = 0L, // Room ще генерира ID автоматично
                        pilotName = pilotName,
                        departureAirport = departureAirport,
                        arrivalAirport = arrivalAirport,
                        departureTime = departureTime,
                        arrivalTime = arrivalTime,
                        aircraft = aircraft,
                        status = "pending"
                    )
                    repository.addFlight(newFlight)
                    navController.popBackStack()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Flight")
        }
    }
}
