package com.polstat.sisipan.api

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST



// Singleton object to create the Retrofit instance
object ApiClient {
    private const val BASE_URL = "http://10.0.2.2:8080/"

    private val gson: Gson = GsonBuilder()
        .setLenient()
        .create()

    val authService: AuthService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthService::class.java)
    }

    val formasiService: FormasiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FormasiService::class.java)
    }
}

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
    suspend fun getAll(): FormasiAllResponse

}