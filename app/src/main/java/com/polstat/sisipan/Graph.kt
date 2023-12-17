/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.polstat.sisipan

import android.content.Context
import android.util.Log
import androidx.room.Room
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.polstat.sisipan.api.AuthFetcher
import com.polstat.sisipan.api.FormasiFetcher
import com.polstat.sisipan.api.MahasiswaFetcher
import com.polstat.sisipan.api.PilihanFetcher
import com.polstat.sisipan.api.ProvinsiFetcher
import com.polstat.sisipan.data.FormasiRepository
import com.polstat.sisipan.data.FormasiStore
import com.polstat.sisipan.data.MahasiswaRepository
import com.polstat.sisipan.data.MahasiswaStore
import com.polstat.sisipan.data.PilihanRepository
import com.polstat.sisipan.data.PilihanStore
import com.polstat.sisipan.data.ProvinsiRepository
import com.polstat.sisipan.data.ProvinsiStore
import com.polstat.sisipan.data.UserRepository
import com.polstat.sisipan.data.room.SisipanDatabase
import com.polstat.sisipan.data.room.TransactionRunner
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.LoggingEventListener
import java.io.File

/**
 * A very simple global singleton dependency graph.
 *
 * For a real app, you would use something like Hilt/Dagger instead.
 */
object Graph {

    private const val BASE_URL = "http://10.0.2.2:8080/"

    private val gson: Gson = GsonBuilder()
        .setLenient()
        .create()

    lateinit var okHttpClient: OkHttpClient

    lateinit var database: SisipanDatabase
        private set

    private val transactionRunner: TransactionRunner
        get() = database.transactionRunnerDao()


    private val mainDispatcher: CoroutineDispatcher
        get() = Dispatchers.Main

    private val ioDispatcher: CoroutineDispatcher
        get() = Dispatchers.IO


    private val mahasiswaService by lazy {
        MahasiswaFetcher(
            okHttpClient = okHttpClient,
            ioDispatcher = ioDispatcher,
            url = BASE_URL
        )
    }
    private val provinsiService by lazy {
        ProvinsiFetcher(
            okHttpClient = okHttpClient,
            ioDispatcher = ioDispatcher,
            url = BASE_URL
        )
    }
    private val formasiService by lazy {
        FormasiFetcher(
            okHttpClient = okHttpClient,
            ioDispatcher = ioDispatcher,
            url = BASE_URL
        )
    }
    val authService by lazy {
        AuthFetcher(
            ioDispatcher = ioDispatcher,
            url = BASE_URL
        )
    }
    private val pilihanService by lazy {
        PilihanFetcher(
            okHttpClient = okHttpClient,
            ioDispatcher = ioDispatcher,
            url = BASE_URL
        )
    }
    lateinit var userRepository: UserRepository
        private set
    private var isDatabaseInitialized = false


    val formasiRepository by lazy {
        FormasiRepository(
            formasiService = formasiService,
            formasiStore = formasiStore,
            mainDispatcher = mainDispatcher,
            transactionRunner = transactionRunner,
        )
    }


    val formasiStore by lazy {
        FormasiStore(
            formasiDao = database.formasiDao(),
            transactionRunner = transactionRunner
        )
    }
    val provinsiRepository by lazy {
        ProvinsiRepository(
            provinsiService = provinsiService,
            provinsiStore = provinsiStore,
            mainDispatcher = mainDispatcher,
            transactionRunner = transactionRunner,
        )
    }

    val provinsiStore by lazy {
        ProvinsiStore(
            provinsiDao = database.provinsiDao(),
            transactionRunner = transactionRunner
        )
    }
    val pilihanRepository by lazy {
        PilihanRepository(
            pilihanService = pilihanService,
            pilihanStore = pilihanStore,
            mainDispatcher = mainDispatcher,
            transactionRunner = transactionRunner,
        )
    }

    val pilihanStore by lazy {
        PilihanStore(
            pilihanDao = database.pilihanDao(),
            transactionRunner = transactionRunner
        )
    }
    val mahasiswaRepository by lazy {
        MahasiswaRepository(
            mahasiswaService = mahasiswaService,
            mahasiswaStore = mahasiswaStore,
            mainDispatcher = mainDispatcher,
            transactionRunner = transactionRunner,
        )
    }

    val mahasiswaStore by lazy {
        MahasiswaStore(
            mahasiswaDao = database.mahasiswaDao(),
            transactionRunner = transactionRunner
        )
    }

    fun isDatabaseInitialized(): Boolean {
        return isDatabaseInitialized
    }

    fun isDatabaseOpen(): Boolean {
        return database.isOpen
    }

    fun provide(context: Context) {
        if (!isDatabaseInitialized) {
            database = Room.databaseBuilder(context, SisipanDatabase::class.java, "data.db")
                // This is not recommended for normal apps, but the goal of this sample isn't to
                // showcase all of Room.
                .fallbackToDestructiveMigration()
                .build()
            userRepository = UserRepository
            userRepository.initialize(context)  // Memanggil initialize di sini
            okHttpClient = OkHttpClient.Builder()
                .cache(Cache(File(context.cacheDir, "http_cache"), (20 * 1024 * 1024).toLong()))
                .apply {
                    if (BuildConfig.DEBUG) eventListenerFactory(LoggingEventListener.Factory())
                }
                .addInterceptor { chain ->
                    val original = chain.request()
                    val requestBuilder = original.newBuilder()
                        .header("Authorization", "Bearer ${UserRepository.accessToken}")
                    val request = requestBuilder.build()
                    chain.proceed(request)
                }
                .build()
            isDatabaseInitialized = true
            Log.i("TAGraProv", "DATABASE open: ${database.isOpen}")
        }
    }

    fun logOut(context: Context) {
        if (isDatabaseInitialized()) {
            database.close()
        }
    }
}
