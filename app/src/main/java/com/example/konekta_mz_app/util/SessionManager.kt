package com.example.konekta_mz_app.util

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("konekta_session", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_USER_ID = "user_id"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_USER_ROLE = "user_role"
        private const val KEY_USER_IMAGE = "user_image"
    }

    fun saveSession(userId: Long, name: String, email: String, role: String, imagePath: String = "") {
        prefs.edit().apply {
            putLong(KEY_USER_ID, userId)
            putBoolean(KEY_IS_LOGGED_IN, true)
            putString(KEY_USER_NAME, name)
            putString(KEY_USER_EMAIL, email)
            putString(KEY_USER_ROLE, role)
            putString(KEY_USER_IMAGE, imagePath)
            apply()
        }
    }

    fun isLoggedIn(): Boolean = prefs.getBoolean(KEY_IS_LOGGED_IN, false)

    fun getUserId(): Long = prefs.getLong(KEY_USER_ID, -1L)

    fun getUserName(): String = prefs.getString(KEY_USER_NAME, "") ?: ""

    fun getUserEmail(): String = prefs.getString(KEY_USER_EMAIL, "") ?: ""

    fun getUserRole(): String = prefs.getString(KEY_USER_ROLE, "") ?: ""

    fun getUserImage(): String = prefs.getString(KEY_USER_IMAGE, "") ?: ""

    fun clearSession() {
        prefs.edit().clear().apply()
    }
}
