package com.polstat.sisipan.api

import com.polstat.sisipan.data.Formasi
import com.polstat.sisipan.data.Mahasiswa
import com.polstat.sisipan.data.Pilihan
import com.polstat.sisipan.data.Provinsi
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path


// Define your API interface
interface AuthService {
    @Headers("Content-Type: application/json")
    @POST("login")
    suspend fun login(@Body credentials: AuthRequest): LoginResponse

    @Headers("Content-Type: application/json")
    @POST("register")
    suspend fun register(@Body credentials: AuthRequest): SignUpResponse
}

interface FormasiService {
    @Headers("Content-Type: application/json")
    @GET("formasi")
    suspend fun getAll(): ApiResponse<List<Formasi>>
    @Headers("Content-Type: application/json")
    @POST("formasi")
    suspend fun insert(@Body request: Formasi): ApiResponse<Formasi>
    @Headers("Content-Type: application/json")
    @PUT("formasi/{id}")
    suspend fun ubah(@Path ("id") id:Long, @Body request: Formasi): ApiResponse<Formasi>
    @Headers("Content-Type: application/json")
    @DELETE("formasi/{id}")
    suspend fun delete(@Path ("id") id:Long): ApiResponse<String>
}

interface ProvinsiService {
    @Headers("Content-Type: application/json")
    @GET("provinsi")
    suspend fun getAll(): ApiResponse<List<Provinsi>>
}

interface PilihanService {
    @Headers("Content-Type: application/json")
    @GET("pilihan")
    suspend fun getAll(): ApiResponse<List<Pilihan>>

    @Headers("Content-Type: application/json")
    @GET("penempatan")
    suspend fun doPenempatan(): ApiResponse<List<Pilihan>>

    @Headers("Content-Type: application/json")
    @POST("pilihan/{id}")
    suspend fun pilih(@Path ("id") id:Long, @Body request: PilihanRequest): ApiResponse<Pilihan>

    @Headers("Content-Type: application/json")
    @PUT("pilihan/{id}")
    suspend fun ubah(@Path ("id") id:Long, @Body request: PilihanRequest): ApiResponse<Pilihan>

}

data class PilihanRequest(
    val pilihan1:Long,
    val pilihan2:Long,
    val pilihan3:Long,
)
interface MahasiswaService {
    @Headers("Content-Type: application/json")
    @GET("mahasiswa")
    suspend fun getAll(): ApiResponse<List<Mahasiswa>>

    @Headers("Content-Type: application/json")
    @GET("mahasiswa/{id}")
    suspend fun getById(@Path("id") id: Long): ApiResponse<Mahasiswa>

    @Headers("Content-Type: application/json")
    @POST("mahasiswa")
    suspend fun insert(@Body request: Mahasiswa): ApiResponse<Mahasiswa>

    @Headers("Content-Type: application/json")
    @DELETE("mahasiswa/{id}")
    suspend fun delete(@Path ("id") id:Long): ApiResponse<String>

}