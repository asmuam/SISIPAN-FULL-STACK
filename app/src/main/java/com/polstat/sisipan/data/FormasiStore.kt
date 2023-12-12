package com.polstat.sisipan.data

import com.polstat.sisipan.data.room.FormasiDao
import com.polstat.sisipan.data.room.TransactionRunner
import kotlinx.coroutines.flow.Flow

/**
 * A data repository for [Formasi] instances.
 */
class FormasiStore(
    private val formasiDao: FormasiDao,
    private val transactionRunner: TransactionRunner
) {
    /**
     * Returns a flow containing the list of [Formasi] instances where kuotaSt, kuotaKs, or kuotaD3 is greater than 0.
     */
    fun formasiBuka(): Flow<List<Formasi>> {
        return formasiDao.formasiBuka()
    }

    /**
     * Add a new [Formasi] to this store.
     *
     * This automatically switches to the main thread to maintain thread consistency.
     */
    suspend fun addFormasi(formasi: Formasi) {
        formasiDao.insert(formasi)
    }

    suspend fun saveFormasiList(formasiList: List<Formasi>) {
        // Hapus data lama di database (jika ada)
        formasiDao.deleteAll()

        // Simpan data baru ke database
        formasiDao.insertAll(formasiList)
    }
    suspend fun isEmpty(): Boolean = formasiDao.count() == 0
}

