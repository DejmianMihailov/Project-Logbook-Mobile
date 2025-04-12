package com.example.mobilelogbook.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mobilelogbook.data.FlightEntity
import com.example.mobilelogbook.repository.FlightRepository
import com.example.mobilelogbook.session.UserSession
import com.example.mobilelogbook.ui.components.LabeledDropdown
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFlightScreen(
    navController: NavController,
    repository: FlightRepository,
    modifier: Modifier = Modifier,
    onFlightSaved: (() -> Unit)? = null,
    onBackToList: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    val snackbarHostState = remember { SnackbarHostState() }

    var pilotName by remember { mutableStateOf("") }
    var departureAirport by remember { mutableStateOf("") }
    var arrivalAirport by remember { mutableStateOf("") }
    var departureTime by remember { mutableStateOf("") }
    var arrivalTime by remember { mutableStateOf("") }
    var aircraft by remember { mutableStateOf("") }

    val flightTimeOptions = listOf(1 to "Short (0–30 min)", 2 to "Medium (30–60 min)", 3 to "Long (> 60 min)")
    val landingOptions = listOf(1 to "Day Landing", 2 to "Night Landing")
    val pilotFunctionOptions = listOf(1 to "Pilot in Command", 2 to "First Officer")

    var selectedFlightTimeId by remember { mutableStateOf<Int?>(null) }
    var selectedLandingId by remember { mutableStateOf<Int?>(null) }
    var selectedPilotFunctionId by remember { mutableStateOf<Int?>(null) }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            Text("Add Flight", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(pilotName, { pilotName = it }, label = { Text("Pilot Name") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(departureAirport, { departureAirport = it }, label = { Text("Departure Airport") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(arrivalAirport, { arrivalAirport = it }, label = { Text("Arrival Airport") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(departureTime, { departureTime = it }, label = { Text("Departure Time (2025-04-08 12:00)") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(arrivalTime, { arrivalTime = it }, label = { Text("Arrival Time (2025-04-08 14:00)") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(aircraft, { aircraft = it }, label = { Text("Aircraft") }, modifier = Modifier.fillMaxWidth())

            LabeledDropdown(
                label = "Flight Time",
                options = flightTimeOptions,
                selectedId = selectedFlightTimeId,
                onSelectedChange = { selectedFlightTimeId = it },
                modifier = Modifier.fillMaxWidth()
            )

            LabeledDropdown(
                label = "Landing Type",
                options = landingOptions,
                selectedId = selectedLandingId,
                onSelectedChange = { selectedLandingId = it },
                modifier = Modifier.fillMaxWidth()
            )

            LabeledDropdown(
                label = "Pilot Role",
                options = pilotFunctionOptions,
                selectedId = selectedPilotFunctionId,
                onSelectedChange = { selectedPilotFunctionId = it },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    coroutineScope.launch {
                        if (pilotName.isBlank() || departureAirport.isBlank() || arrivalAirport.isBlank()
                            || departureTime.isBlank() || arrivalTime.isBlank()
                            || selectedFlightTimeId == null || selectedLandingId == null || selectedPilotFunctionId == null
                        ) {
                            Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                            return@launch
                        }

                        val newFlight = FlightEntity(
                            pilotName = pilotName.trim(),
                            departureAirport = departureAirport.trim(),
                            arrivalAirport = arrivalAirport.trim(),
                            departureTime = departureTime.trim(),
                            arrivalTime = arrivalTime.trim(),
                            aircraft = aircraft.trim(),
                            flightTimeId = selectedFlightTimeId!!.toLong(),
                            landingId = selectedLandingId!!.toLong(),
                            pilotFunctionId = selectedPilotFunctionId!!.toLong(),
                            username = UserSession.getUsername() ?: "unknown",
                            status = "pending"
                        )

                        repository.addFlight(newFlight)
                        snackbarHostState.showSnackbar("✅ Flight added successfully")

                        onFlightSaved?.invoke()
                        navController.popBackStack()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Flight")
            }

            Spacer(modifier = Modifier.height(8.dp))

            onBackToList?.let {
                Button(
                    onClick = { it() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("← Back to Flights")
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
