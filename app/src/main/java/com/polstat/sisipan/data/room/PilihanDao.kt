package com.polstat.sisipan.data.room

import androidx.room.Dao
import androidx.room.Query
import com.polstat.sisipan.data.Formasi
import com.polstat.sisipan.data.Pilihan
import kotlinx.coroutines.flow.Flow

@Dao
abstract class PilihanDao : BaseDao<Pilihan> {
    @Query("SELECT COUNT(*) FROM pilihan")
    abstract suspend fun count(): Int
    @Query("DELETE FROM pilihan")
    abstract suspend fun deleteAll()

    @Query("SELECT * FROM pilihan")
    abstract fun daftarPilihan(): Flow<List<Pilihan>>
    @Query("SELECT * FROM pilihan WHERE mahasiswa_id=:id")
    abstract suspend fun pilihanByMhs(id:Long): Pilihan
    @Query("SELECT * FROM pilihan WHERE id=:id")
    abstract suspend fun findById(id:Long): Pilihan
}
