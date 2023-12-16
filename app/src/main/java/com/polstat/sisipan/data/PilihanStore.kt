package com.polstat.sisipan.data

import android.util.Log
import com.polstat.sisipan.data.room.PilihanDao
import com.polstat.sisipan.data.room.TransactionRunner
import kotlinx.coroutines.flow.Flow

class PilihanStore(
    private val pilihanDao: PilihanDao,
    private val transactionRunner: TransactionRunner
) {
    suspend fun isEmpty(): Boolean = pilihanDao.count() == 0
    suspend fun savePilihanList(pilihanList: List<Pilihan>) {
        // Hapus data lama di database (jika ada)
        pilihanDao.deleteAll()

        // Simpan data baru ke database
        pilihanDao.insertAll(pilihanList)
    }
    suspend fun count(): Int{
        Log.e("PILIHAN_STORE", "COUNT${pilihanDao.count()}")
        return pilihanDao.count()
    }

    suspend fun daftarPilihan(): Flow<List<Pilihan>>{
        return pilihanDao.daftarPilihan()
    }
    suspend fun pilihanByMhs(id:Long): Pilihan{
        return pilihanDao.pilihanByMhs(id)
    }
}
