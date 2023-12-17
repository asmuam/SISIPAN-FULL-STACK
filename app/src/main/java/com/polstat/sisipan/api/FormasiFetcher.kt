package com.polstat.sisipan.api

import com.polstat.sisipan.data.Formasi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Objects

class FormasiFetcher(
    private val okHttpClient: OkHttpClient,
    private val ioDispatcher: CoroutineDispatcher,
    private val url: String
) : FormasiService {

    private val formasiService: FormasiService by lazy {
        Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
            .create(FormasiService::class.java)
    }

    override suspend fun getAll(): ApiResponse<List<Formasi>> {
        return withContext(ioDispatcher) {
            formasiService.getAll()
        }
    }

    override suspend fun insert(request: Formasi): ApiResponse<Formasi> {
        return withContext(ioDispatcher) {
            formasiService.insert(request)
        }
    }

    override suspend fun ubah(id: Long, request: Formasi): ApiResponse<Formasi> {
        return withContext(ioDispatcher) {
            formasiService.ubah(id, request)
        }
    }

    override suspend fun delete(id: Long): ApiResponse<String> {
        return withContext(ioDispatcher) {
            formasiService.delete(id)
        }
    }
}
