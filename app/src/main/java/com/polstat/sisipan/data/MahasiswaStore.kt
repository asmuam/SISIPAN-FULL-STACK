package com.polstat.sisipan.data

import android.util.Log
import com.polstat.sisipan.data.room.MahasiswaDao
import com.polstat.sisipan.data.room.TransactionRunner
import kotlinx.coroutines.flow.Flow

/**
 * A data repository for [Mahasiswa] instances.
 */
class MahasiswaStore(
    private val mahasiswaDao: MahasiswaDao,
    private val transactionRunner: TransactionRunner
) {

    /**
     * Add a new [Mahasiswa] to this store.
     *
     * This automatically switches to the main thread to maintain thread consistency.
     */
    suspend fun addMahasiswa(mahasiswa: Mahasiswa) {
        mahasiswaDao.insert(mahasiswa)
    }

    suspend fun getMahasiswaFlow(id: Long): Flow<Mahasiswa> {
        return mahasiswaDao.findByIdFlow(id)
    }

    suspend fun getMahasiswa(id: Long): Mahasiswa {
        return mahasiswaDao.findById(id)
    }


    suspend fun getAll(): Flow<List<Mahasiswa>> {
        return mahasiswaDao.findAll()
    }
    suspend fun saveMahasiswaList(mahasiswaList: List<Mahasiswa>) {
        // Hapus data lama di database (jika ada)
        mahasiswaDao.deleteAll()

        // Simpan data baru ke database
        mahasiswaDao.insertAll(mahasiswaList)
    }
    suspend fun count(): Int{
        return mahasiswaDao.count()
    }

    suspend fun isEmpty(): Boolean = mahasiswaDao.count() == 0

}

