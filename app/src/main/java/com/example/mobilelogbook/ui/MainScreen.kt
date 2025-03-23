package com.example.mobilelogbook.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.*
import com.example.mobilelogbook.repository.FlightRepository
import com.example.mobilelogbook.ui.screens.AddFlightScreen

@Composable
fun MainScreen(repository: FlightRepository) { // Подаваме repository тук
    val navController = rememberNavController()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Pilot Logbook") })
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = "flightList",
            modifier = Modifier.padding(padding)
        ) {
            composable("flightList") { FlightListScreen(navController, repository) } // Подаваме repository
            composable("addFlight") { AddFlightScreen(navController, repository) } // Подаваме repository
        }
    }
}
