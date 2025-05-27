package com.example.mobilelogbook.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mobilelogbook.data.FlightEntity
import com.example.mobilelogbook.session.UserSession
import com.example.mobilelogbook.viewmodel.MobileFlightViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFlightScreen(
    navController: NavController,
    flightViewModel: MobileFlightViewModel,
    onFlightSaved: () -> Unit,
    onBackToList: (() -> Unit)? = null
) {
    val scrollState = rememberScrollState()

    var departureAirport by remember { mutableStateOf("") }
    var arrivalAirport by remember { mutableStateOf("") }
    var pilotName by remember { mutableStateOf("") }
    var flightDuration by remember { mutableStateOf("") }

    var aircraftMake by remember { mutableStateOf("") }
    var aircraftModel by remember { mutableStateOf("") }
    var aircraftRegistration by remember { mutableStateOf("") }

    var multiPilotTime by remember { mutableStateOf("") }
    var singlePilotTime by remember { mutableStateOf("") }
    var totalFlightTime by remember { mutableStateOf("") }

    var takeoffType by remember { mutableStateOf("") }
    var landingType by remember { mutableStateOf("") }
    var operationalCondition by remember { mutableStateOf("") }

    var pilotFunction by remember { mutableStateOf("") }
    var pilotRole by remember { mutableStateOf("") }

    var remarksText by remember { mutableStateOf("") }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Add Flight") }) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(value = departureAirport, onValueChange = { departureAirport = it }, label = { Text("Departure Airport") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = arrivalAirport, onValueChange = { arrivalAirport = it }, label = { Text("Arrival Airport") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = pilotName, onValueChange = { pilotName = it }, label = { Text("Pilot Name") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = flightDuration, onValueChange = { flightDuration = it }, label = { Text("Flight Duration (minutes)") }, modifier = Modifier.fillMaxWidth())

            Text("Aircraft", style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(value = aircraftMake, onValueChange = { aircraftMake = it }, label = { Text("Make") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = aircraftModel, onValueChange = { aircraftModel = it }, label = { Text("Model") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = aircraftRegistration, onValueChange = { aircraftRegistration = it }, label = { Text("Registration") }, modifier = Modifier.fillMaxWidth())

            Text("Flight Time", style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(value = multiPilotTime, onValueChange = { multiPilotTime = it }, label = { Text("Multi Pilot Time (min)") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = singlePilotTime, onValueChange = { singlePilotTime = it }, label = { Text("Single Pilot Time (min)") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = totalFlightTime, onValueChange = { totalFlightTime = it }, label = { Text("Total Flight Time (min)") }, modifier = Modifier.fillMaxWidth())

            OutlinedTextField(value = takeoffType, onValueChange = { takeoffType = it }, label = { Text("Takeoff Type") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = landingType, onValueChange = { landingType = it }, label = { Text("Landing Type") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = operationalCondition, onValueChange = { operationalCondition = it }, label = { Text("Operational Condition") }, modifier = Modifier.fillMaxWidth())

            OutlinedTextField(value = pilotFunction, onValueChange = { pilotFunction = it }, label = { Text("Pilot Function") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = pilotRole, onValueChange = { pilotRole = it }, label = { Text("Pilot Role") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = remarksText, onValueChange = { remarksText = it }, label = { Text("Remarks") }, modifier = Modifier.fillMaxWidth())

            Button(
                onClick = {
                    val username = UserSession.getUsername() ?: return@Button
                    val now = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)

                    val flight = FlightEntity(
                        departureTime = now,
                        arrivalTime = now,
                        flightDuration = flightDuration.toIntOrNull() ?: 0,
                        pilotName = pilotName,
                        username = username,
                        departureAirport = departureAirport,
                        arrivalAirport = arrivalAirport,
                        aircraftMake = aircraftMake,
                        aircraftModel = aircraftModel,
                        aircraftRegistration = aircraftRegistration,
                        multiPilotTime = multiPilotTime.toIntOrNull(),
                        singlePilotTime = singlePilotTime.toIntOrNull(),
                        totalFlightTime = totalFlightTime.toLongOrNull(),
                        takeoffType = takeoffType,
                        landingType = landingType,
                        operationalCondition = operationalCondition,
                        pilotFunction = pilotFunction,
                        pilotRole = pilotRole,
                        remarksText = remarksText,
                        synced = false
                    )

                    flightViewModel.addFlightAndSync(flight)
                    onFlightSaved()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Flight")
            }

            onBackToList?.let {
                TextButton(onClick = { it() }) {
                    Text("Back to List")
                }
            }
        }
    }
}
