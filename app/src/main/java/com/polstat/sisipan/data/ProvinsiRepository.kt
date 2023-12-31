package com.polstat.sisipan.data

import android.util.Log
import com.polstat.sisipan.api.ProvinsiFetcher
import com.polstat.sisipan.api.ProvinsiService
import com.polstat.sisipan.data.room.TransactionRunner
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ProvinsiRepository(
    private val provinsiService: ProvinsiFetcher,
    private val provinsiStore: ProvinsiStore,
    private val mainDispatcher: CoroutineDispatcher,
    private val transactionRunner: TransactionRunner
) {
    private var refreshingJob: Job? = null

    private val scope = CoroutineScope(mainDispatcher)

    suspend fun refreshProvinsi(force: Boolean) {
        if (refreshingJob?.isActive == true) {
            refreshingJob?.join()
        } else if (force || provinsiStore.isEmpty()) {
            Log.i("PROVINSI", "refreshFormasi: DO")
            refreshingJob = scope.launch {
                try {
                    transactionRunner {
                        // Jika memaksa atau data di store kosong, panggil service dan simpan ke store
                        val provinsiList = provinsiService.getAll().data
                        provinsiList?.let {
                            provinsiStore.saveProvinsiList(it)
                        }
                    }
                } catch (e: Exception) {
                    // Tangani kesalahan saat menyimpan data ke store
                    Log.e("PROVINSI_REPO", "Error refreshing provinsi", e)
                }
            }
        }
    }

    // Metode lain sesuai kebutuhan
}
