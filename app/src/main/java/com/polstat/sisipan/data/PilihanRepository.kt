package com.polstat.sisipan.data

import android.util.Log
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

    // Metode lain sesuai kebutuhan
}

