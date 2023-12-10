package com.polstat.sisipan.api

import com.squareup.moshi.JsonClass


// Data classes for request and response
@JsonClass(generateAdapter = true)
data class LoginRequest(val email: String, val password: String)
@JsonClass(generateAdapter = true)
data class SignUpRequest(val email: String, val password: String)
@JsonClass(generateAdapter = true)
data class LoginResponse(val httpStatus: String, val httpStatusCode: Int, val message: String)
@JsonClass(generateAdapter = true)
data class SignUpResponse(val httpStatus: String, val httpStatusCode: Int, val message: String)
enum class AuthResult {
    SUCCESS,
    FAILURE
    // Tambahan status jika diperlukan
}
