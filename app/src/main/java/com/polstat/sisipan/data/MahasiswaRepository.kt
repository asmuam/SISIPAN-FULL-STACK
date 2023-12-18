package com.polstat.sisipan.data

import android.util.Log
import com.polstat.sisipan.api.MahasiswaFetcher
import com.polstat.sisipan.api.MahasiswaService
import com.polstat.sisipan.data.room.TransactionRunner
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MahasiswaRepository(
    private val mahasiswaService: MahasiswaFetcher,
    private val mahasiswaStore: MahasiswaStore,
    private val transactionRunner: TransactionRunner,
    private val mainDispatcher: CoroutineDispatcher
) {
    private var refreshingJob: Job? = null

    private val scope = CoroutineScope(mainDispatcher)

    suspend fun refreshMahasiswa(force: Boolean) {
        if (refreshingJob?.isActive == true) {
            refreshingJob?.join()
        } else if (force || mahasiswaStore.isEmpty()) {
            refreshingJob = scope.launch {
                try {
                    transactionRunner {
                        // Jika memaksa atau data di store kosong, panggil service dan simpan ke store
                        val mahasiswaList = mahasiswaService.getAll().data
                        mahasiswaList?.let {
                            mahasiswaStore.saveMahasiswaList(it)
                        }
                    }
                } catch (e: Exception) {
                    // Tangani kesalahan di sini
                    e.printStackTrace()
                    // Misalnya, Anda dapat menampilkan pesan kesalahan atau melakukan tindakan lain
                }
            }
        }
    }

    suspend fun insertMahasiswa(mhs: Mahasiswa) {
        // Lakukan operasi penyimpanan menggunakan service atau store, sesuai kebutuhan
        // Misalnya, jika Anda memiliki service untuk menyimpan data ke server:
        try {
            val response = mahasiswaService.insert(mhs)
            if (response.httpStatusCode == 201) {
                // Jika penyimpanan berhasil, refresh data atau lakukan tindakan lain
                refreshMahasiswa(force = true)
            } else {
                // Handle kesalahan jika diperlukan
                Log.e("FormasiRepository", "Failed to insert formasi. Response: ${response.message}")
            }
        } catch (e: Exception) {
            // Handle exception jika terjadi kesalahan dalam komunikasi dengan server
            Log.e("FormasiRepository", "Error inserting formasi", e)
        }
    }

    fun delete(id: Long) {
        scope.launch {
            try {
                val response = mahasiswaService.delete(id)
                if (response.httpStatusCode == 200) {
                    // Jika penyimpanan berhasil, refresh data atau lakukan tindakan lain
                    refreshMahasiswa(force = false)
                } else {
                    // Handle kesalahan jika diperlukan
                    Log.e(
                        "FormasiRepository",
                        "Failed to delete formasi. Response: ${response.message}"
                    )
                }
            } catch (e: Exception) {
                // Handle exception jika terjadi kesalahan dalam komunikasi dengan server
                Log.e("FormasiRepository", "Error deleting mhs", e)
            }
        }
    }
    // Metode lain sesuai kebutuhan
}
