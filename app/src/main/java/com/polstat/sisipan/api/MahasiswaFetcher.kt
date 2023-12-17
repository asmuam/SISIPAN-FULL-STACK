package com.polstat.sisipan.api

import com.polstat.sisipan.data.Mahasiswa
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MahasiswaFetcher(
    private val okHttpClient: OkHttpClient,
    private val ioDispatcher: CoroutineDispatcher,
    private val url: String
) : MahasiswaService {

    private val mahasiswaService: MahasiswaService by lazy {
        Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
            .create(MahasiswaService::class.java)
    }

    override suspend fun getAll(): ApiResponse<List<Mahasiswa>> {
        return withContext(ioDispatcher) {
            mahasiswaService.getAll()
        }
    }

    override suspend fun getById(id: Long): ApiResponse<Mahasiswa> {
        return withContext(ioDispatcher) {
            mahasiswaService.getById(id)
        }
    }
}
