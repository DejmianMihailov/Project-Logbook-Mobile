package com.example.mobilelogbook.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobilelogbook.data.FlightEntity
import com.example.mobilelogbook.repository.MobileFlightRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MobileFlightViewModel(
    private val repository: MobileFlightRepository
) : ViewModel() {

    private val _flights = MutableStateFlow<List<FlightEntity>>(emptyList())
    val flights: StateFlow<List<FlightEntity>> = _flights

    // ✅ Зареждане на локални полети
    fun loadFlightsForCurrentUser() {
        viewModelScope.launch {
            val localFlights = repository.loadFlightsForCurrentUser()
            _flights.value = localFlights
        }
    }

    // ✅ Запис и опит за sync (ако има интернет)
    fun addFlightAndSync(flight: FlightEntity) {
        viewModelScope.launch {
            repository.saveFlightAndSync(flight)
            loadFlightsForCurrentUser()
        }
    }

    // ✅ Само локален запис (офлайн режим)
    fun saveFlightOffline(flight: FlightEntity) {
        viewModelScope.launch {
            repository.saveFlightOffline(flight)
            loadFlightsForCurrentUser()
        }
    }

    // ✅ Опит за sync от сървъра и обновяване на локалната база
    fun syncFromServerIfOnline() {
        viewModelScope.launch {
            repository.syncFlightsIfOnline()
            loadFlightsForCurrentUser()
        }
    }
}
