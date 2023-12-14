package com.polstat.sisipan.data.room

import androidx.room.Dao
import androidx.room.Query
import com.polstat.sisipan.data.Mahasiswa

@Dao
abstract class MahasiswaDao : BaseDao<Mahasiswa> {
    @Query("DELETE FROM mahasiswa")
    abstract suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM mahasiswa")
    abstract fun count(): Int

}
