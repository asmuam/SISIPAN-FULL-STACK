package com.polstat.sisipan.api

import com.polstat.sisipan.data.Formasi
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
    val role: String?
)
@JsonClass(generateAdapter = true)
data class SignUpResponse(val httpStatus: String, val httpStatusCode: Int, val message: String)

@JsonClass(generateAdapter = true)
data class FormasiAllResponse(
    val data: List<Formasi>?,
    val message: String?,
    val httpStatus: String?,
    val httpStatusCode: Int?
)
//@JsonClass(generateAdapter = true)
//data class DataFormasi(
//    val id: Long,
//    val provinsi: Int,
//    val kodeSatker: String,
//    val namaSatuanKerja: String,
//    val kuotaSt: Int,
//    val kuotaKs: Int,
//    val kuotaD3: Int
//)











enum class AuthResult {
    SUCCESS,
    FAILURE
    // Tambahan status jika diperlukan
}
