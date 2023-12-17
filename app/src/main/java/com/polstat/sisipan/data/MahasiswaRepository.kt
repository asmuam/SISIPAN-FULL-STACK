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

    // Metode lain sesuai kebutuhan
}
