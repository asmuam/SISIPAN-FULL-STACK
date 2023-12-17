package com.polstat.sisipan.api

import com.polstat.sisipan.BuildConfig
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.LoggingEventListener
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

class AuthFetcher(
    private val ioDispatcher: CoroutineDispatcher,
    private val url:String,
) : AuthService {

    private val okHttpClientWithoutAuth = OkHttpClient.Builder()
        .apply {
            if (BuildConfig.DEBUG) {
                eventListenerFactory(LoggingEventListener.Factory())
            }
        }
        .build()

    private val authService: AuthService by lazy {
        Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClientWithoutAuth)
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