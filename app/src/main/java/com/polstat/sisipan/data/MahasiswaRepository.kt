package com.polstat.sisipan.data

import android.util.Log
import com.polstat.sisipan.api.MahasiswaService
import com.polstat.sisipan.data.room.TransactionRunner
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MahasiswaRepository(
    private val mahasiswaService: MahasiswaService,
    private val mahasiswaStore: MahasiswaStore,
    private val transactionRunner: TransactionRunner,
    private val mainDispatcher: CoroutineDispatcher
) {
    private var refreshingJob: Job? = null

    private val scope = CoroutineScope(mainDispatcher)

    suspend fun refreshMahasiwa(force: Boolean) {
        if (refreshingJob?.isActive == true) {
            refreshingJob?.join()
        } else if (force || mahasiswaStore.isEmpty()) {
            refreshingJob = scope.launch {
                transactionRunner {
                    Log.e("repomhs", "cek")
                    val id: Long = UserRepository.getIdMhs() ?: 0L
                    Log.e("repomhs", "idmhs:${id}")

                    // Jika memaksa atau data di store kosong, panggil service dan simpan ke store
                    val mahasiswaList = mahasiswaService.getById(id).data
                    mahasiswaList?.let {
                        mahasiswaStore.addMahasiwa(it)
                    }
                }

            }
        }
    }

    // Metode lain sesuai kebutuhan
}
