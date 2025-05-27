package com.example.mobilelogbook.session

import android.content.Context
import android.content.SharedPreferences

object UserSession {
    private lateinit var prefs: SharedPreferences
    private var appContext: Context? = null

    fun init(context: Context) {
        prefs = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)
        appContext = context.applicationContext
    }

    fun setUsername(username: String) {
        prefs.edit().putString("username", username).apply()
    }

    fun getUsername(): String? {
        return prefs.getString("username", null)
    }

    fun setSessionId(sessionHeader: String) {
        // Извлича само стойността на JSESSIONID, не целия header
        val jsessionId = sessionHeader.split(";").firstOrNull()?.trim()
        prefs.edit().putString("JSESSIONID", jsessionId).apply()
    }

    fun getSessionId(): String? {
        val value = prefs.getString("JSESSIONID", null)
        return if (value != null) "JSESSIONID=$value" else null
    }

    fun getContext(): Context? {
        return appContext
    }

    fun clear() {
        prefs.edit().clear().apply()
    }
}
