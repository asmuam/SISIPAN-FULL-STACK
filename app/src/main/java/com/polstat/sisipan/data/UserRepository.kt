package com.polstat.sisipan.data

import android.content.Context
import android.content.SharedPreferences
import com.polstat.sisipan.SisipanApplication

object UserRepository {

    private const val PREF_NAME = "user_data"
    private const val KEY_ACCESS_TOKEN = "accessToken"
    private const val KEY_EXPIRES_IN = "expiresIn"
    private const val KEY_ROLE = "role"
    private const val KEY_ID = "id"
    private const val KEY_EMAIL = "email"
    private const val KEY_ID_MHS = "idMhs"

    private lateinit var sharedPreferences: SharedPreferences

    fun initialize(context: Context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    var role: String?
        get() = sharedPreferences.getString(KEY_ROLE, null)
        set(value) {
            sharedPreferences.edit().putString(KEY_ROLE, value).apply()
        }

    var id: Long?
        get() = sharedPreferences.getLong(KEY_ID, 0)
        set(value) {
            sharedPreferences.edit().putLong(KEY_ID, value ?: 0).apply()
        }

    var email: String
        get() = sharedPreferences.getString(KEY_EMAIL, "") ?: ""
        set(value) {
            sharedPreferences.edit().putString(KEY_EMAIL, value).apply()
        }

    var idMhs: Long?
        get() = sharedPreferences.getLong(KEY_ID_MHS, 0)
        set(value) {
            sharedPreferences.edit().putLong(KEY_ID_MHS, value ?: 0).apply()
        }

    var accessToken: String?
        get() = sharedPreferences.getString(KEY_ACCESS_TOKEN, null)
        set(value) {
            sharedPreferences.edit().putString(KEY_ACCESS_TOKEN, value).apply()
        }

    var expiresIn: Long
        get() = sharedPreferences.getLong(KEY_EXPIRES_IN, 0)
        set(value) {
            sharedPreferences.edit().putLong(KEY_EXPIRES_IN, calculateExpiryTime(value)).apply()
        }

    fun setAllUserData(accessToken: String, role: String, id: Long, email: String, idMhs: Long, expiresIn: Long) {
        this.accessToken = accessToken
        this.role = role
        this.id = id
        this.email = email
        this.idMhs = idMhs
        this.expiresIn = expiresIn
    }

    fun clear() {
        sharedPreferences.edit().clear().apply()
    }

    private fun calculateExpiryTime(expiresIn: Long): Long {
        return System.currentTimeMillis() + expiresIn * 1000
    }
    override fun toString(): String {
        return "UserRepository(" +
                "accessToken=$accessToken, " +
                "role=$role, " +
                "id=$id, " +
                "email=$email, " +
                "idMhs=$idMhs, " +
                "expiresIn=$expiresIn" +
                ")"
    }

}
