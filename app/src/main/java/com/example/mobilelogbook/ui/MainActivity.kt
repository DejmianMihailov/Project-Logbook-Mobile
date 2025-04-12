package com.example.mobilelogbook.ui

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.work.*
import com.example.mobilelogbook.data.ApiService
import com.example.mobilelogbook.data.FlightDatabase
import com.example.mobilelogbook.repository.FlightRepository
import com.example.mobilelogbook.session.UserSession
import com.example.mobilelogbook.sync.SyncManager
import com.example.mobilelogbook.ui.theme.MobileLogbookTheme
import com.example.mobilelogbook.ui.theme.ThemeViewModel
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {

    private val themeViewModel: ThemeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //  Инициализация на локалната сесия
        UserSession.init(applicationContext)

        //  Стартиране на автоматична синхронизация
        SyncManager.startSyncWorker(applicationContext)

        //  Допълнително логване за потвърждение
        Log.d("MainActivity", "SyncWorker scheduled to run every 15 minutes.")

        //  Настройка на локалната база и API
        val dao = FlightDatabase.getDatabase(applicationContext).flightDao()
        val api = ApiService.create()
        val repository = FlightRepository(dao, api)

        //  Съдържание на Compose UI
        setContent {
            val isDark by themeViewModel.isDarkTheme.collectAsState()

            MobileLogbookTheme(darkTheme = isDark) {
                MainScreen(
                    repository = repository,
                    themeViewModel = themeViewModel
                )
            }
        }
    }
}
