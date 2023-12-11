package com.polstat.sisipan.api

object TokenManager {
    private var accessToken: String? = null
    private var role: String? = null
    private var id: Long? = null
    private var email: String? = null
    private var idMhs: Long? = null

    fun setAccessToken(token: String) {
        accessToken = token
    }

    fun getAccessToken(): String? {
        return accessToken
    }

    fun setRole(userRole: String) {
        role = userRole
    }

    fun getRole(): String? {
        return role
    }

    fun setId(userId: Long) {
        id = userId
    }

    fun getId(): Long? {
        return id
    }

    fun setEmail(userEmail: String) {
        email = userEmail
    }

    fun getEmail(): String? {
        return email
    }

    fun setIdMhs(studentId: Long) {
        idMhs = studentId
    }

    fun getIdMhs(): Long? {
        return idMhs
    }
}
