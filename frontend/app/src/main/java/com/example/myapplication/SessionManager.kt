package com.example.myapplication

import android.content.Context
import android.content.SharedPreferences

object SessionManager {

    private const val PREFS_NAME = "TripCheckPrefs"
    private const val KEY_USER_ID = "user_id"
    private const val DEFAULT_USER_ID = -1L

    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun saveUserId(context: Context, userId: Long) {
        val editor = getPreferences(context).edit()
        editor.putLong(KEY_USER_ID, userId)
        editor.apply()
    }

    fun getUserId(context: Context): Long {
        return getPreferences(context).getLong(KEY_USER_ID, DEFAULT_USER_ID)
    }

    fun isLoggedIn(context: Context): Boolean {
        return getUserId(context) != DEFAULT_USER_ID
    }

    fun logout(context: Context) {
        val editor = getPreferences(context).edit()
        editor.remove(KEY_USER_ID)
        editor.apply()
    }
}
