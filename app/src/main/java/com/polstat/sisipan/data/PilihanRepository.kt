package com.polstat.sisipan.data

import android.util.Log
import com.polstat.sisipan.api.PilihanFetcher
import com.polstat.sisipan.api.PilihanRequest
import com.polstat.sisipan.api.PilihanService
import com.polstat.sisipan.data.room.TransactionRunner
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class PilihanRepository(
    private val pilihanService: PilihanFetcher,
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
                try {
                    transactionRunner {
                        // Jika memaksa atau data di store kosong, panggil service dan simpan ke store
                        val response = pilihanService.getAll()
                        Log.i("PILIHAN REPO", "refreshPilihan:${response} ")
                        if (response.httpStatusCode==200) {
                            if (response.data!=null) {
                                if (response.data.isEmpty()){
                                        pilihanStore.deleteAll()
                                }
                                pilihanStore.savePilihanList(response.data)
                            }
                        }

                    }
                } catch (e: Exception) {
                    // Tangani kesalahan saat menyimpan data ke store
                    Log.e("PILIHAN_REPO", "Error refreshing pilihan", e)
                }
            }
        }
    }

    suspend fun insertPilihan(id: Long, pilihan: PilihanRequest) {
        // Lakukan operasi penyimpanan menggunakan service atau store, sesuai kebutuhan
        // Misalnya, jika Anda memiliki service untuk menyimpan data ke server:
        try {
            val response = pilihanService.pilih(id, pilihan)
            if (response.httpStatusCode == 200) {
                // Jika penyimpanan berhasil, refresh data atau lakukan tindakan lain
                refreshPilihan(force = true)
            } else {
                // Handle kesalahan jika diperlukan
                Log.e("PILIHANREPOSTATUS", "Failed to insert pilihan. Response: ${response.message}")
            }
        } catch (e: Exception) {
            // Handle exception jika terjadi kesalahan dalam komunikasi dengan server atau operasi penyimpanan
            Log.e("PILIHANREPO", "Error inserting pilihan", e)
        }
    }

    suspend fun ubahPilihan(id: Long, pilihan: PilihanRequest) {
        // Lakukan operasi penyimpanan menggunakan service atau store, sesuai kebutuhan
        // Misalnya, jika Anda memiliki service untuk menyimpan data ke server:
        try {
            Log.e("TAG", "call api: $id : $pilihan")
            val response = pilihanService.ubah(id, pilihan)
            Log.e("TAG", "resp api: $response")
            if (response.httpStatusCode == 200) {
                // Jika penyimpanan berhasil, refresh data atau lakukan tindakan lain
                refreshPilihan(force = false)
            } else {
                // Handle kesalahan jika diperlukan
                Log.e("PILIHANREPOResponse", "Failed to insert pilihan. Response: ${response.message}")
            }
        } catch (e: Exception) {
            // Handle exception jika terjadi kesalahan dalam komunikasi dengan server atau operasi penyimpanan
            Log.e("PILIHANREPO", "Error inserting pilihan", e)
        }
    }

    suspend fun doPenempatan() {
        try {
            val response = pilihanService.doPenempatan()
            Log.e("TAG", "resp api: $response")
            if (response.httpStatusCode == 200) {
                // Jika penyimpanan berhasil, refresh data atau lakukan tindakan lain
                refreshPilihan(force = false)
            } else {
                // Handle kesalahan jika diperlukan
                Log.e("PILIHANREPOResponse", "Failed to insert pilihan. Response: ${response.message}")
            }
        } catch (e: Exception){
            Log.e("PILIHANREPO", "Error do penempatan", e)
        }
    }

    fun deleteAll() {
        scope.launch {
            try {
                pilihanService.deleteAll()
                refreshPilihan(false)
            } catch (e: Exception) {
                // Handle exception jika terjadi kesalahan dalam komunikasi dengan server
                Log.e("PilihanRepository", "Error deleting pilihan", e)
            }
        }
    }
        // Metode lain sesuai kebutuhan
}
