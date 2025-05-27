package com.example.mobilelogbook.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.OptIn
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.media3.common.util.UnstableApi
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mobilelogbook.data.ApiService
import com.example.mobilelogbook.data.FlightDatabase
import com.example.mobilelogbook.repository.MobileFlightRepository
import com.example.mobilelogbook.session.UserSession
import com.example.mobilelogbook.sync.SyncManager
import com.example.mobilelogbook.ui.screens.AddFlightScreen
import com.example.mobilelogbook.ui.screens.LoginScreen
import com.example.mobilelogbook.ui.screens.MainScreen
import com.example.mobilelogbook.ui.screens.ProfileScreen
import com.example.mobilelogbook.ui.theme.MobileLogbookTheme
import com.example.mobilelogbook.ui.theme.ThemeViewModel
import com.example.mobilelogbook.viewmodel.MobileFlightViewModel

class MainActivity : ComponentActivity() {

    private val themeViewModel: ThemeViewModel by viewModels()

    @OptIn(UnstableApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        UserSession.init(applicationContext)
        SyncManager.startPeriodicSync(applicationContext)

        val database = FlightDatabase.getDatabase(applicationContext)
        val flightDao = database.flightDao()
        val userDao = database.userDao()
        val apiService = ApiService.create()

        val mobileRepository = MobileFlightRepository(apiService, flightDao, userDao)
        val flightViewModel = MobileFlightViewModel(mobileRepository)

        setContent {
            val isDark by themeViewModel.isDarkTheme.collectAsState()
            val navController = rememberNavController()
            val startDestination = if (UserSession.getUsername() != null) "main" else "login"

            MobileLogbookTheme(darkTheme = isDark) {
                NavHost(
                    navController = navController,
                    startDestination = startDestination
                ) {
                    composable("login") {
                        LoginScreen(
                            navController = navController,
                            onLoginSuccess = {
                                navController.navigate("main") {
                                    popUpTo("login") { inclusive = true }
                                }
                            },
                            themeViewModel = themeViewModel,
                            flightRepository = mobileRepository
                        )
                    }
                    composable("main") {
                        MainScreen(
                            navController = navController,
                            flightViewModel = flightViewModel,
                            themeViewModel = themeViewModel
                        )
                    }
                    composable("addFlight") {
                        AddFlightScreen(
                            navController = navController,
                            flightViewModel = flightViewModel,
                            onFlightSaved = {
                                navController.popBackStack()
                            }
                        )
                    }
                    composable("profile") {
                        ProfileScreen(
                            navController = navController,
                            themeViewModel = themeViewModel,
                            flightViewModel = flightViewModel // Подай ViewModel-а тук!
                        )
                    }
                }
            }
        }
    }
}
