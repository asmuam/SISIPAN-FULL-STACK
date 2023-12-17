package com.polstat.sisipan.api

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AuthFetcher(
    private val okHttpClient: OkHttpClient,
    private val ioDispatcher: CoroutineDispatcher,
    private val url:String,
) : AuthService {

    private val authService: AuthService by lazy {
        Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
            .create(AuthService::class.java)
    }

    override suspend fun login(credentials: AuthRequest): LoginResponse {
        return withContext(ioDispatcher) {
            authService.login(credentials)
        }
    }

    override suspend fun register(credentials: AuthRequest): SignUpResponse {
        return withContext(ioDispatcher) {
            authService.register(credentials)
        }
    }
}