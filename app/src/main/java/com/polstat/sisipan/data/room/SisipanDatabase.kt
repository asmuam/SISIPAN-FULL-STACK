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

package com.polstat.sisipan.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.polstat.sisipan.data.Formasi
import com.polstat.sisipan.data.Mahasiswa
import com.polstat.sisipan.data.Pilihan
import com.polstat.sisipan.data.Provinsi

/**
 * The [RoomDatabase] we use in this app.
 */

// Room database class
@Database(
    entities = [Formasi::class, Mahasiswa::class, Pilihan::class, Provinsi::class],
    version = 6,
    exportSchema = false
)
@TypeConverters(DateTimeTypeConverters::class) // Assuming you need a TypeConverter for Date
abstract class SisipanDatabase : RoomDatabase() {
    abstract fun formasiDao(): FormasiDao
    abstract fun mahasiswaDao(): MahasiswaDao
    abstract fun pilihanDao(): PilihanDao
    abstract fun provinsiDao(): ProvinsiDao
    abstract fun transactionRunnerDao(): TransactionRunnerDao
}
