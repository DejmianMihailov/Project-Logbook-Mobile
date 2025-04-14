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
import com.example.mobilelogbook.ui.components.DateTimePickerDialog
import com.example.mobilelogbook.ui.components.LabeledDropdown
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

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

    val isoFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

    var pilotName by remember { mutableStateOf("") }
    var departureAirport by remember { mutableStateOf("") }
    var arrivalAirport by remember { mutableStateOf("") }
    var aircraft by remember { mutableStateOf("") }

    var departureTime by remember { mutableStateOf<LocalDateTime?>(null) }
    var arrivalTime by remember { mutableStateOf<LocalDateTime?>(null) }

    val flightTimeOptions = listOf(1 to "Short (0–30 min)", 2 to "Medium (30–60 min)", 3 to "Long (> 60 min)")
    val landingOptions = listOf(1 to "Day Landing", 2 to "Night Landing")
    val pilotFunctionOptions = listOf(1 to "Pilot in Command", 2 to "First Officer")

    var selectedFlightTimeId by remember { mutableStateOf<Int?>(null) }
    var selectedLandingId by remember { mutableStateOf<Int?>(null) }
    var selectedPilotFunctionId by remember { mutableStateOf<Int?>(null) }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
        Column(
            modifier = modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(scrollState)
        ) {
            Text("Add Flight", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(pilotName, { pilotName = it }, label = { Text("Pilot Name") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(departureAirport, { departureAirport = it }, label = { Text("Departure Airport") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(arrivalAirport, { arrivalAirport = it }, label = { Text("Arrival Airport") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(aircraft, { aircraft = it }, label = { Text("Aircraft") }, modifier = Modifier.fillMaxWidth())

            DateTimePickerDialog(
                label = "Departure Time",
                selectedDateTime = departureTime,
                onDateTimeSelected = { departureTime = it },
                modifier = Modifier.fillMaxWidth()
            )

            DateTimePickerDialog(
                label = "Arrival Time",
                selectedDateTime = arrivalTime,
                onDateTimeSelected = { arrivalTime = it },
                modifier = Modifier.fillMaxWidth()
            )

            LabeledDropdown("Flight Time", flightTimeOptions, selectedFlightTimeId, { selectedFlightTimeId = it }, Modifier.fillMaxWidth())
            LabeledDropdown("Landing Type", landingOptions, selectedLandingId, { selectedLandingId = it }, Modifier.fillMaxWidth())
            LabeledDropdown("Pilot Role", pilotFunctionOptions, selectedPilotFunctionId, { selectedPilotFunctionId = it }, Modifier.fillMaxWidth())

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    coroutineScope.launch {
                        val currentUsername = UserSession.getUsername()
                        if (currentUsername.isNullOrBlank()) {
                            Toast.makeText(context, "⚠️ Not logged in", Toast.LENGTH_LONG).show()
                            return@launch
                        }

                        if (pilotName.isBlank() || departureAirport.isBlank() || arrivalAirport.isBlank()
                            || departureTime == null || arrivalTime == null
                            || selectedFlightTimeId == null || selectedLandingId == null || selectedPilotFunctionId == null
                        ) {
                            Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                            return@launch
                        }

                        try {
                            val duration = Duration.between(departureTime, arrivalTime).toMinutes().toInt()
                            val flight = FlightEntity(
                                pilotName = pilotName.trim(),
                                departureAirport = departureAirport.trim(),
                                arrivalAirport = arrivalAirport.trim(),
                                departureTime = departureTime!!.format(isoFormatter),
                                arrivalTime = arrivalTime!!.format(isoFormatter),
                                flightDuration = duration,
                                aircraft = aircraft.trim(),
                                username = currentUsername,
                                status = "pending",
                                flightTimeId = selectedFlightTimeId!!.toLong(),
                                landingId = selectedLandingId!!.toLong(),
                                pilotFunctionId = selectedPilotFunctionId!!.toLong()
                            )
                            repository.addFlight(flight)
                            snackbarHostState.showSnackbar("✅ Flight added")
                            onFlightSaved?.invoke()
                            navController.popBackStack()
                        } catch (e: Exception) {
                            Toast.makeText(context, "❌ Error: ${e.message}", Toast.LENGTH_LONG).show()
                        }
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
        }
    }
}
