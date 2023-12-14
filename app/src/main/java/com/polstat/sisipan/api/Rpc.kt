package com.polstat.sisipan.api

import com.polstat.sisipan.data.Formasi
import com.polstat.sisipan.data.Mahasiswa
import com.squareup.moshi.JsonClass


// Data classes for request and response
@JsonClass(generateAdapter = true)
data class AuthRequest(val email: String, val password: String)
@JsonClass(generateAdapter = true)
data class LoginResponse(
    val data: DataLogin?,
    val message: String?,
    val httpStatus: String?,
    val httpStatusCode: Int?
)

@JsonClass(generateAdapter = true)
data class DataLogin(
    val email: String?,
    val accessToken: String?,
    val id: Long?,  // Menambahkan properti id
    val idMhs: Long?,  // Menambahkan properti idMhs
    val role: String?,
    val expiresIn: Long?
)
@JsonClass(generateAdapter = true)
data class SignUpResponse(val httpStatus: String, val httpStatusCode: Int, val message: String)

@JsonClass(generateAdapter = true)
data class ApiResponse<T>(
    val data: T?,
    val message: String?,
    val httpStatus: String?,
    val httpStatusCode: Int?
)
//@JsonClass(generateAdapter = true)
//data class FormasiAllResponse(
//    val data: List<Formasi>?,
//    val message: String?,
//    val httpStatus: String?,
//    val httpStatusCode: Int?
//)
//
//@JsonClass(generateAdapter = true)
//data class MahasiswaAllResponse(
//    val data: List<Mahasiswa>?,
//    val message: String?,
//    val httpStatus: String?,
//    val httpStatusCode: Int?
//)


enum class AuthResult {
    SUCCESS,
    FAILURE
    // Tambahan status jika diperlukan
}
