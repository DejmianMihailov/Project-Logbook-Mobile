package com.example.mobilelogbook.session

import android.content.Context
import android.content.SharedPreferences

object UserSession {
    private lateinit var preferences: SharedPreferences

    private const val PREF_NAME = "user_session"
    private const val KEY_USERNAME = "username"

    fun init(context: Context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun setUsername(username: String) {
        preferences.edit().putString(KEY_USERNAME, username).apply()
    }

    fun clear() {
        preferences.edit().remove(KEY_USERNAME).apply()
    }

    val username: String?
        get() = preferences.getString(KEY_USERNAME, null)
}
