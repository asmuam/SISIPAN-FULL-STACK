package com.polstat.sisipan.api

import com.polstat.sisipan.data.Provinsi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ProvinsiFetcher(
    private val okHttpClient: OkHttpClient,
    private val ioDispatcher: CoroutineDispatcher,
    private val url: String
) : ProvinsiService {

    private val provinsiService: ProvinsiService by lazy {
        Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
            .create(ProvinsiService::class.java)
    }

    override suspend fun getAll(): ApiResponse<List<Provinsi>> {
        return withContext(ioDispatcher) {
            provinsiService.getAll()
        }
    }
}
