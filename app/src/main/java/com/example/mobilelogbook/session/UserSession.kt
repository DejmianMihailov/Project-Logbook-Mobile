package com.example.mobilelogbook.session

import android.content.Context
import android.content.SharedPreferences

object UserSession {
    private const val PREFS_NAME = "user_prefs"
    private const val KEY_USERNAME = "username"

    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun setUsername(username: String) {
        prefs.edit().putString(KEY_USERNAME, username).apply()
    }

    fun getUsername(): String? {
        return prefs.getString(KEY_USERNAME, null)
    }

    fun clear() {
        prefs.edit().clear().apply()
    }
}
