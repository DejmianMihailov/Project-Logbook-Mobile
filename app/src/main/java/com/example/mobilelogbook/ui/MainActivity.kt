package com.example.mobilelogbook.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.mobilelogbook.data.ApiService
import com.example.mobilelogbook.data.FlightDatabase
import com.example.mobilelogbook.repository.FlightRepository
import com.example.mobilelogbook.session.UserSession
import com.example.mobilelogbook.ui.theme.MobileLogbookTheme
import com.example.mobilelogbook.ui.theme.ThemeViewModel

class MainActivity : ComponentActivity() {

    private val themeViewModel: ThemeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UserSession.init(applicationContext)

        val dao = FlightDatabase.getDatabase(applicationContext).flightDao()
        val api = ApiService.create()
        val repository = FlightRepository(dao, api)

        setContent {
            val isDark by themeViewModel.isDarkTheme.collectAsState()
            MobileLogbookTheme(darkTheme = isDark) {
                MainScreen(repository = repository, themeViewModel = themeViewModel)
            }
        }
    }
}
