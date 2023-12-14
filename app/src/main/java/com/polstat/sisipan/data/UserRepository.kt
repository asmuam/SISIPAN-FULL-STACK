package com.polstat.sisipan.data

import android.content.Context
import android.content.SharedPreferences

object UserRepository {

    private const val PREF_NAME = "user_data"
    private const val KEY_ACCESS_TOKEN = "accessToken"
    private const val KEY_EXPIRES_IN = "expiresIn"

    private var role: String? = null
    private var id: Long? = null
    private var email: String = ""
    private var idMhs: Long? = null
    private var accessToken: String? = null
    private var expiresIn: Long = 0L

    private lateinit var sharedPreferences: SharedPreferences

    fun initialize(context: Context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        accessToken = sharedPreferences.getString(KEY_ACCESS_TOKEN, null)
        expiresIn = sharedPreferences.getLong(KEY_EXPIRES_IN, 0)
    }

    fun setRole(userRole: String?) {
        role = userRole
    }

    fun getRole(): String? {
        return role
    }

    fun setId(userId: Long?) {
        id = userId
    }

    fun getId(): Long? {
        return id
    }

    fun setEmail(userEmail: String) {
        email = userEmail
    }

    suspend fun getEmail(): String {
        return email
    }

    fun setIdMhs(studentId: Long?) {
        idMhs = studentId
    }

    fun getIdMhs(): Long? {
        return idMhs
    }

    fun setAccessToken(token: String?) {
        accessToken = token
        sharedPreferences.edit().putString(KEY_ACCESS_TOKEN, token).apply()
    }

    fun setExpiresIn(second: Long) {
        expiresIn = calculateExpiryTime(second)
        sharedPreferences.edit().putLong(KEY_EXPIRES_IN, expiresIn).apply()
    }
    fun getExpiresIn(): Long {
        return expiresIn
    }
    fun setAllUserData(
        accessToken: String,
        role: String,
        id: Long,
        email: String,
        idMhs: Long,
        expiresIn: Long,
    ) {
        setAccessToken(accessToken)
        setRole(role)
        setId(id)
        setEmail(email)
        setIdMhs(idMhs)
        setExpiresIn(expiresIn)
    }

    fun clear() {
        setAccessToken(null)
        setRole(null)
        setId(null)
        setEmail("")
        setIdMhs(null)
        setExpiresIn(0L)
    }

    private fun calculateExpiryTime(expiresIn: Long): Long {
        return System.currentTimeMillis() + expiresIn * 1000
    }

    override fun toString(): String {
        return "com.polstat.sisipan.data.UserRepository(accessToken=$accessToken, role=$role, id=$id, email=$email, idMhs=$idMhs)"
    }
}

