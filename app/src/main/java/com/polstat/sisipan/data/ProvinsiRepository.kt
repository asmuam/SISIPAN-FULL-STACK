package com.polstat.sisipan.data

import android.util.Log
import com.polstat.sisipan.Graph.provinsiService
import com.polstat.sisipan.Graph.provinsiStore
import com.polstat.sisipan.api.ProvinsiService
import com.polstat.sisipan.data.room.TransactionRunner
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ProvinsiRepository(
    private val provinsiService: ProvinsiService,
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
                transactionRunner {
                    // Jika memaksa atau data di store kosong, panggil service dan simpan ke store
                    val provinsiList = provinsiService.getAll().data
                    provinsiList?.let {
                        provinsiStore.saveProvinsiList(it)
                    }
                }
            }
        }
    }

    // Metode lain sesuai kebutuhan
}
