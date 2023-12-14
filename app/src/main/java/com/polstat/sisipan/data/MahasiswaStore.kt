package com.polstat.sisipan.data

import com.polstat.sisipan.data.room.FormasiDao
import com.polstat.sisipan.data.room.MahasiswaDao
import com.polstat.sisipan.data.room.TransactionRunner
import kotlinx.coroutines.flow.Flow

/**
 * A data repository for [Formasi] instances.
 */
class MahasiswaStore(
    private val mahasiswaDao: MahasiswaDao,
    private val transactionRunner: TransactionRunner
) {

    /**
     * Add a new [Mahasiwa] to this store.
     *
     * This automatically switches to the main thread to maintain thread consistency.
     */
    suspend fun addMahasiwa(mahasiswa: Mahasiswa) {
        mahasiswaDao.insert(mahasiswa)
    }

    suspend fun saveMahasiwaList(mahasiswaList: List<Mahasiswa>) {
        // Hapus data lama di database (jika ada)
        mahasiswaDao.deleteAll()

        // Simpan data baru ke database
        mahasiswaDao.insertAll(mahasiswaList)
    }

    suspend fun isEmpty(): Boolean = mahasiswaDao.count() == 0

}

