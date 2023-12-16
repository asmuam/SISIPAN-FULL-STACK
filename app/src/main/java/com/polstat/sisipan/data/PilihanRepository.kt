package com.polstat.sisipan.data

import android.util.Log
import com.polstat.sisipan.api.PilihanRequest
import com.polstat.sisipan.api.PilihanService
import com.polstat.sisipan.data.room.TransactionRunner
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class PilihanRepository(
    private val pilihanService: PilihanService,
    private val pilihanStore: PilihanStore,
    private val transactionRunner: TransactionRunner,
    private val mainDispatcher: CoroutineDispatcher
) {
    private var refreshingJob: Job? = null

    private val scope = CoroutineScope(mainDispatcher)

    suspend fun refreshPilihan(force: Boolean) {
        if (refreshingJob?.isActive == true) {
            refreshingJob?.join()
        } else if (force || pilihanStore.isEmpty()) {
            Log.e("PILIHAN_REPO", "cek")
            refreshingJob = scope.launch {
                transactionRunner {
                    // Jika memaksa atau data di store kosong, panggil service dan simpan ke store
                    val pilihanList = pilihanService.getAll().data
                    pilihanList?.let {
                        pilihanStore.savePilihanList(it)
                    }
                }
            }
        }
    }

    suspend fun insertPilihan(id:Long,pilihan: PilihanRequest) {
        // Lakukan operasi penyimpanan menggunakan service atau store, sesuai kebutuhan
        // Misalnya, jika Anda memiliki service untuk menyimpan data ke server:
        try {
            val response = pilihanService.pilih(id,pilihan)
            if (response.httpStatusCode==200) {
                // Jika penyimpanan berhasil, refresh data atau lakukan tindakan lain
                refreshPilihan(force = true)
            } else {
                // Handle kesalahan jika diperlukan
                Log.e("PILIHANREPO", "Failed to insert pilihan. Response: ${response.message}")
            }
        } catch (e: Exception) {
            // Handle exception jika terjadi kesalahan dalam komunikasi dengan server
            Log.e("PILIHANREPO", "Error inserting pilihan", e)
        }
    }

    suspend fun ubahPilihan(id:Long,pilihan: PilihanRequest) {
        // Lakukan operasi penyimpanan menggunakan service atau store, sesuai kebutuhan
        // Misalnya, jika Anda memiliki service untuk menyimpan data ke server:
        try {
            Log.e("TAG", "call api: ${id} : ${pilihan}")
            val response = pilihanService.ubah(id,pilihan)
            Log.e("TAG", "resp api: ${response}")
            if (response.httpStatusCode==200) {
                // Jika penyimpanan berhasil, refresh data atau lakukan tindakan lain
                refreshPilihan(force = true)
            } else {
                // Handle kesalahan jika diperlukan
                Log.e("PILIHANREPOResponse", "Failed to insert pilihan. Response: ${response.message}")
            }
        } catch (e: Exception) {
            // Handle exception jika terjadi kesalahan dalam komunikasi dengan server
            Log.e("PILIHANREPO", "Error inserting pilihan", e)
        }
    }
    // Metode lain sesuai kebutuhan
}

