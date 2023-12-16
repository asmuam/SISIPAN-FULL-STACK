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

import android.app.ActivityManager
import android.app.LauncherActivity
import com.polstat.sisipan.data.UserRepository
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.room.Room
import com.polstat.sisipan.data.room.TransactionRunner
import com.polstat.sisipan.data.room.SisipanDatabase
import com.polstat.sisipan.api.ApiClient
import com.polstat.sisipan.api.AuthService
import com.polstat.sisipan.api.FormasiService
import com.polstat.sisipan.api.MahasiswaService
import com.polstat.sisipan.api.PilihanService
import com.polstat.sisipan.api.ProvinsiService
import com.polstat.sisipan.data.FormasiRepository
import com.polstat.sisipan.data.FormasiStore
import com.polstat.sisipan.data.MahasiswaRepository
import com.polstat.sisipan.data.MahasiswaStore
import com.polstat.sisipan.data.PilihanRepository
import com.polstat.sisipan.data.PilihanStore
import com.polstat.sisipan.data.ProvinsiRepository
import com.polstat.sisipan.data.ProvinsiStore
import com.polstat.sisipan.ui.MainActivity
import java.io.File
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.LoggingEventListener

/**
 * A very simple global singleton dependency graph.
 *
 * For a real app, you would use something like Hilt/Dagger instead.
 */
object Graph {
    lateinit var okHttpClient: OkHttpClient

    lateinit var database: SisipanDatabase
        private set

    private val transactionRunner: TransactionRunner
        get() = database.transactionRunnerDao()


    private val mainDispatcher: CoroutineDispatcher
        get() = Dispatchers.Main

    private val ioDispatcher: CoroutineDispatcher
        get() = Dispatchers.IO

    val authService: AuthService
        get() = ApiClient.authService

    val formasiService: FormasiService
        get() = ApiClient.formasiService

    val provinsiService: ProvinsiService
        get() = ApiClient.provinsiService
    val mahasiswaService: MahasiswaService
        get() = ApiClient.mahasiswaService
    val pilihanService: PilihanService
        get() = ApiClient.pilihanService
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
            okHttpClient = OkHttpClient.Builder()
                .cache(Cache(File(context.cacheDir, "http_cache"), (20 * 1024 * 1024).toLong()))
                .apply {
                    if (BuildConfig.DEBUG) eventListenerFactory(LoggingEventListener.Factory())
                }
                .build()

            database = Room.databaseBuilder(context, SisipanDatabase::class.java, "data.db")
                // This is not recommended for normal apps, but the goal of this sample isn't to
                // showcase all of Room.
                .fallbackToDestructiveMigration()
                .build()
            userRepository = UserRepository
            userRepository.initialize(context)  // Memanggil initialize di sini
            isDatabaseInitialized = true
            Log.i("TAGra", "DATABASE STATUS: ${database.isOpen}")
        }
    }

    fun logOut(context: Context) {
        // Close the database to ensure it's not in use
        database.close()
        Log.i("TAGra", "DATABASE CLOSING STATUS: ${database.isOpen}")

        // Start the launcher activity to restart the application
        // Start the MainActivity to restart the application
        val intent = Intent(context, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
        Log.i("TAGra", "Is database initialized: ${isDatabaseInitialized()}")
        Log.i("TAGra", "GraphclearDatabaseContext: ${context}")
        Log.i("TAGra", "DATABASE STATUS: ${database.isOpen}")
    }
}
