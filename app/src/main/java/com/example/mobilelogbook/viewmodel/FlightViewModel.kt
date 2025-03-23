package com.example.mobilelogbook.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobilelogbook.data.FlightEntity
import com.example.mobilelogbook.repository.FlightRepository
import kotlinx.coroutines.launch

class FlightViewModel(private val repository: FlightRepository) : ViewModel() {

    fun addFlight(flight: FlightEntity) {
        viewModelScope.launch {
            repository.addFlight(flight)
        }
    }

    fun syncFlights() {
        viewModelScope.launch {
            repository.syncFlights()
        }
    }

    fun fetchLatestFlights() {
        viewModelScope.launch {
            repository.fetchLatestFlights()
        }
    }
}
