package com.polstat.sisipan.data

import android.util.Log
import com.polstat.sisipan.api.FormasiService
import com.polstat.sisipan.data.room.TransactionRunner
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class FormasiRepository(
    private val formasiService: FormasiService,
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
                transactionRunner {
                    // Jika memaksa atau data di store kosong, panggil service dan simpan ke store
                    val formasiList = formasiService.getAll().data
                    Log.i("TAG", "refreshFormasi: ${formasiList} ")
                    formasiList?.let {
                        formasiStore.saveFormasiList(it)
                    }
                }
            }
        }
    }

    // Metode lain sesuai kebutuhan
}
