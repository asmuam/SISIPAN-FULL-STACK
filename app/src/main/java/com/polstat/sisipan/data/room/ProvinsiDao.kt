package com.polstat.sisipan.data.room

import androidx.room.Dao
import androidx.room.Query
import com.polstat.sisipan.data.Provinsi
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ProvinsiDao : BaseDao<Provinsi> {
    @Query("SELECT COUNT(*) FROM provinsi")
    abstract suspend fun count(): Int
    @Query("DELETE FROM provinsi")
    abstract suspend fun deleteAll()
    @Query("SELECT * FROM provinsi WHERE id=:id")
    abstract suspend fun getProvById(id:Long): Provinsi
}
