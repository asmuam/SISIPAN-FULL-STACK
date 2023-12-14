package com.polstat.sisipan.data

import com.polstat.sisipan.data.room.ProvinsiDao
import com.polstat.sisipan.data.room.TransactionRunner
import kotlinx.coroutines.flow.Flow

class ProvinsiStore(
    private val provinsiDao: ProvinsiDao,
    private val transactionRunner: TransactionRunner
) {
    suspend fun isEmpty(): Boolean = provinsiDao.count() == 0
    suspend fun saveProvinsiList(it: List<Provinsi>) {
        // Hapus data lama di database (jika ada)
        provinsiDao.deleteAll()

        // Simpan data baru ke database
        provinsiDao.insertAll(it)
    }

    suspend fun getById(id:Long):Provinsi{
        return provinsiDao.getProvById(id)
    }


}
