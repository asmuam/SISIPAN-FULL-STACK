package com.polstat.sisipan.api

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.polstat.sisipan.Graph.okHttpClient
import com.polstat.sisipan.data.Formasi
import com.polstat.sisipan.data.Mahasiswa
import com.polstat.sisipan.data.Pilihan
import com.polstat.sisipan.data.Provinsi
import com.polstat.sisipan.data.UserRepository
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path


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
            .client(okHttpClient.newBuilder().addInterceptor { chain ->
                val original = chain.request()
                val requestBuilder = original.newBuilder()
                    .header("Authorization", "Bearer ${UserRepository.accessToken}")
                val request = requestBuilder.build()
                chain.proceed(request)
            }.build())
            .build()
            .create(AuthService::class.java)
    }

    val formasiService: FormasiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient.newBuilder().addInterceptor { chain ->
                val original = chain.request()
                val requestBuilder = original.newBuilder()
                    .header("Authorization", "Bearer ${UserRepository.accessToken}")
                val request = requestBuilder.build()
                chain.proceed(request)
            }.build())
            .build()
            .create(FormasiService::class.java)
    }

    val provinsiService: ProvinsiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient.newBuilder().addInterceptor { chain ->
                val original = chain.request()
                val requestBuilder = original.newBuilder()
                    .header("Authorization", "Bearer ${UserRepository.accessToken}")
                val request = requestBuilder.build()
                chain.proceed(request)
            }.build())
            .build()
            .create(ProvinsiService::class.java)
    }

    val mahasiswaService: MahasiswaService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient.newBuilder().addInterceptor { chain ->
                val original = chain.request()
                val requestBuilder = original.newBuilder()
                    .header("Authorization", "Bearer ${UserRepository.accessToken}")
                val request = requestBuilder.build()
                chain.proceed(request)
            }.build())
            .build()
            .create(MahasiswaService::class.java)
    }
    val pilihanService: PilihanService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient.newBuilder().addInterceptor { chain ->
                val original = chain.request()
                val requestBuilder = original.newBuilder()
                    .header("Authorization", "Bearer ${UserRepository.accessToken}")
                val request = requestBuilder.build()
                chain.proceed(request)
            }.build())
            .build()
            .create(PilihanService::class.java)
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
    suspend fun getAll(): ApiResponse<List<Formasi>>
    @Headers("Content-Type: application/json")
    @POST("formasi")
    suspend fun insert(@Body request: Formasi): ApiResponse<Formasi>

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

}