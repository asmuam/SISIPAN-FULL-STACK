package com.polstat.sisipan.data.room

import androidx.room.Dao
import androidx.room.Query
import com.polstat.sisipan.data.Mahasiswa
import kotlinx.coroutines.flow.Flow

@Dao
abstract class MahasiswaDao : BaseDao<Mahasiswa> {

    @Query("SELECT * FROM mahasiswa WHERE id=:id")
    abstract fun findById(id: Long?): Flow<Mahasiswa?>

    @Query("DELETE FROM mahasiswa")
    abstract suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM mahasiswa")
    abstract suspend fun count(): Int
}
