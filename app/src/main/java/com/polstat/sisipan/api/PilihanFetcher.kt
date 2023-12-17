package com.polstat.sisipan.api

import com.polstat.sisipan.data.Pilihan
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class PilihanFetcher(
    private val okHttpClient: OkHttpClient,
    private val ioDispatcher: CoroutineDispatcher,
    private val url: String
) : PilihanService {

    private val pilihanService: PilihanService by lazy {
        Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
            .create(PilihanService::class.java)
    }

    override suspend fun getAll(): ApiResponse<List<Pilihan>> {
        return withContext(ioDispatcher) {
            pilihanService.getAll()
        }
    }

    override suspend fun pilih(id: Long, request: PilihanRequest): ApiResponse<Pilihan> {
        return withContext(ioDispatcher) {
            pilihanService.pilih(id, request)
        }
    }

    override suspend fun ubah(id: Long, request: PilihanRequest): ApiResponse<Pilihan> {
        return withContext(ioDispatcher) {
            pilihanService.ubah(id, request)
        }
    }
}
