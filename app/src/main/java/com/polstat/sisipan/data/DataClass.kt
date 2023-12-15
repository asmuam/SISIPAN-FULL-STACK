package com.polstat.sisipan.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.coroutines.flow.Flow
import java.util.Date

// Kotlin data class for Mahasiswa entity
@Entity(tableName = "mahasiswa")
data class Mahasiswa(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Long = 0,
    @ColumnInfo(name = "nim") val nim: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "prodi") val prodi: String,
    @ColumnInfo(name = "provinsi_id") val provinsiId: Long,
    @ColumnInfo(name = "ipk") val ipk: Float
)

// Kotlin data class for Formasi entity
@Entity(tableName = "formasi")
data class Formasi(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Long = 0,
    @ColumnInfo(name = "provinsi_id") val provinsiId: Long,
    @ColumnInfo(name = "kodeSatker") val kodeSatker: String,
    @ColumnInfo(name = "namaSatuanKerja") val namaSatuanKerja: String,
    @ColumnInfo(name = "kuotaSt") val kuotaSt: Int,
    @ColumnInfo(name = "kuotaKs") val kuotaKs: Int,
    @ColumnInfo(name = "kuotaD3") val kuotaD3: Int,
)


// Kotlin data class for Provinsi entity
@Entity(tableName = "provinsi")
data class Provinsi(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Long = 0,
    @ColumnInfo(name = "kodeProvinsi") val kodeProvinsi: String,
    @ColumnInfo(name = "namaProvinsi") val namaProvinsi: String
)

// Kotlin data class for Pilihan entity
@Entity(tableName = "pilihan")
data class Pilihan(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Long = 0,
    @ColumnInfo(name = "mahasiswa_id") val mahasiswaId: Long,
    @ColumnInfo(name = "pilihan1_id") val pilihan1Id: Long?,
    @ColumnInfo(name = "pilihan2_id") val pilihan2Id: Long?,
    @ColumnInfo(name = "pilihan3_id") val pilihan3Id: Long?,
    @ColumnInfo(name = "pilihanSistem_id") val pilihanSistemId: Long?,
    @ColumnInfo(name = "indeksPilihan1") val indeksPilihan1: Float,
    @ColumnInfo(name = "indeksPilihan2") val indeksPilihan2: Float,
    @ColumnInfo(name = "indeksPilihan3") val indeksPilihan3: Float,
    @ColumnInfo(name = "ipk") val ipk: Float,
    //@ColumnInfo(name = "waktuMemilih") val waktuMemilih: Date,
    @ColumnInfo(name = "hasil") val hasil: String?
)

data class PilihanNested(
    val id: Long,
    val mahasiswa: Flow<Mahasiswa>,
    val pilihan1: Flow<Formasi>,
    val pilihan2: Flow<Formasi>,
    val pilihan3: Flow<Formasi>,
    val pilihanSistem: Flow<Formasi>,
    val indeksPilihan1: Float,
    val indeksPilihan2: Float,
    val indeksPilihan3: Float,
    val ipk: Float,
    val hasil: String?
)
