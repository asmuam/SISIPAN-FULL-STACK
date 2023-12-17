package com.polstat.sisipan.data

import android.util.Log
import com.polstat.sisipan.api.FormasiFetcher
import com.polstat.sisipan.api.FormasiService
import com.polstat.sisipan.api.PilihanRequest
import com.polstat.sisipan.data.room.TransactionRunner
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class FormasiRepository(
    private val formasiService: FormasiFetcher,
    private val formasiStore: FormasiStore,
    private val transactionRunner: TransactionRunner,
    private val mainDispatcher: CoroutineDispatcher
) {
    private var refreshingJob: Job? = null

    private val scope = CoroutineScope(mainDispatcher)

    suspend fun refreshFormasi(force: Boolean) {
        if (refreshingJob?.isActive == true) {
            refreshingJob?.join()
        } else if (force || formasiStore.isEmpty()) {
            Log.i("TAG", "refreshFormasi: FORCING ")
            refreshingJob = scope.launch {
                try {
                    transactionRunner {
                        // Jika memaksa atau data di store kosong, panggil service dan simpan ke store
                        val formasiList = formasiService.getAll().data
                        Log.i("TAG", "refreshFormasi: ${formasiList} ")
                        formasiList?.let {
                            formasiStore.saveFormasiList(it)
                        }
                    }
                } catch (e: Exception) {
                    // Tangani kesalahan saat menyimpan data ke store
                    Log.e("FormasiRepository", "Error refreshing formasi", e)
                }
            }
        }
    }

    suspend fun insertFormasi(formasi: Formasi) {
        // Lakukan operasi penyimpanan menggunakan service atau store, sesuai kebutuhan
        // Misalnya, jika Anda memiliki service untuk menyimpan data ke server:
        try {
            val response = formasiService.insert(formasi)
            if (response.httpStatusCode == 200) {
                // Jika penyimpanan berhasil, refresh data atau lakukan tindakan lain
                refreshFormasi(force = false)
            } else {
                // Handle kesalahan jika diperlukan
                Log.e(
                    "FormasiRepository",
                    "Failed to insert formasi. Response: ${response.message}"
                )
            }
        } catch (e: Exception) {
            // Handle exception jika terjadi kesalahan dalam komunikasi dengan server
            Log.e("FormasiRepository", "Error inserting formasi", e)
        }
    }
    // Metode lain sesuai kebutuhan

    suspend fun ubahFormasi(id: Long, formasi: Formasi) {
        // Lakukan operasi penyimpanan menggunakan service atau store, sesuai kebutuhan
        // Misalnya, jika Anda memiliki service untuk menyimpan data ke server:
        try {
            val response = formasiService.ubah(id, formasi)
            if (response.httpStatusCode == 200) {
                // Jika penyimpanan berhasil, refresh data atau lakukan tindakan lain
                refreshFormasi(force = false)
            } else {
                // Handle kesalahan jika diperlukan
                Log.e(
                    "PILIHANREPOResponse",
                    "Failed to insert pilihan. Response: ${response.message}"
                )
            }
        } catch (e: Exception) {
            // Handle exception jika terjadi kesalahan dalam komunikasi dengan server atau operasi penyimpanan
            Log.e("PILIHANREPO", "Error inserting pilihan", e)
        }
    }

    fun deleteFormasi(id: Long) {
        scope.launch {
            try {
                val response = formasiService.delete(id)
                if (response.httpStatusCode == 200) {
                    // Jika penyimpanan berhasil, refresh data atau lakukan tindakan lain
                    refreshFormasi(force = false)
                } else {
                    // Handle kesalahan jika diperlukan
                    Log.e(
                        "FormasiRepository",
                        "Failed to delete formasi. Response: ${response.message}"
                    )
                }
            } catch (e: Exception) {
                // Handle exception jika terjadi kesalahan dalam komunikasi dengan server
                Log.e("FormasiRepository", "Error inserting formasi", e)
            }
        }
    }
}
