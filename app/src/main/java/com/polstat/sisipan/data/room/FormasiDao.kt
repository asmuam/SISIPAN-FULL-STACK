package com.polstat.sisipan.data.room

import androidx.room.Dao
import androidx.room.Query
import com.polstat.sisipan.data.Formasi
import kotlinx.coroutines.flow.Flow

@Dao
abstract class FormasiDao : BaseDao<Formasi> {
    @Query("SELECT * FROM formasi WHERE kuotaSt > 0 OR kuotaKs > 0 OR kuotaD3 > 0")
    abstract fun formasiBuka(): Flow<List<Formasi>>

    @Query("SELECT COUNT(*) FROM formasi")
    abstract suspend fun count(): Int

    @Query("DELETE FROM formasi")
    abstract suspend fun deleteAll()
}
