/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.polstat.sisipan.ui

import com.polstat.sisipan.data.Formasi
import com.polstat.sisipan.data.Provinsi

val formasiDummy: List<Formasi> = listOf(
    Formasi(provinsi = 1, kodeSatker = "1100", namaSatuanKerja = "BPS Provinsi Aceh", kuotaSt = 3, kuotaKs = 0, kuotaD3 = 1),
    Formasi(provinsi = 1, kodeSatker = "1101", namaSatuanKerja = "BPS Kabupaten Simeulue", kuotaSt = 2, kuotaKs = 1, kuotaD3 = 2),
    Formasi(provinsi = 1, kodeSatker = "1102", namaSatuanKerja = "BPS Kabupaten Aceh Singkil", kuotaSt = 1, kuotaKs = 3, kuotaD3 = 5),
    Formasi(provinsi = 1, kodeSatker = "1103", namaSatuanKerja = "BPS Kabupaten Aceh Selatan", kuotaSt = 4, kuotaKs = 0, kuotaD3 = 3),
    Formasi(provinsi = 1, kodeSatker = "1104", namaSatuanKerja = "BPS Kabupaten Aceh Tenggara", kuotaSt = 4, kuotaKs = 1, kuotaD3 = 1),
    Formasi(provinsi = 1, kodeSatker = "1105", namaSatuanKerja = "BPS Kabupaten Aceh Timur", kuotaSt = 2, kuotaKs = 5, kuotaD3 = 4),
    Formasi(provinsi = 1, kodeSatker = "1106", namaSatuanKerja = "BPS Kabupaten Aceh Tengah", kuotaSt = 2, kuotaKs = 4, kuotaD3 = 1),
    Formasi(provinsi = 1, kodeSatker = "1107", namaSatuanKerja = "BPS Kabupaten Aceh Barat", kuotaSt = 5, kuotaKs = 4, kuotaD3 = 2),
    Formasi(provinsi = 1, kodeSatker = "1108", namaSatuanKerja = "BPS Kabupaten Aceh Besar", kuotaSt = 1, kuotaKs = 2, kuotaD3 = 2),
    Formasi(provinsi = 1, kodeSatker = "1109", namaSatuanKerja = "BPS Kabupaten Pidie", kuotaSt = 3, kuotaKs = 6, kuotaD3 = 1),

    Formasi(provinsi = 1, kodeSatker = "1110", namaSatuanKerja = "BPS Kabupaten Bireuen", kuotaSt = 2, kuotaKs = 4, kuotaD3 = 1),
    Formasi(provinsi = 1, kodeSatker = "1111", namaSatuanKerja = "BPS Kabupaten Aceh Utara", kuotaSt = 5, kuotaKs = 4, kuotaD3 = 2),
    Formasi(provinsi = 1, kodeSatker = "1112", namaSatuanKerja = "BPS Kabupaten Aceh Barat Daya", kuotaSt = 1, kuotaKs = 2, kuotaD3 = 2),
    Formasi(provinsi = 1, kodeSatker = "1113", namaSatuanKerja = "BPS Kabupaten Gayo Lues", kuotaSt = 3, kuotaKs = 6, kuotaD3 = 1),
    // Add more dummy data as needed
)

val provDummy: List<Provinsi> = listOf(
    Provinsi(id = 1, kodeProvinsi = "11", namaProvinsi = "Aceh"),
    Provinsi(id = 2, kodeProvinsi = "12", namaProvinsi = "Sumatera Utara"),
    Provinsi(id = 3, kodeProvinsi = "13", namaProvinsi = "Sumatera Barat"),
    Provinsi(id = 4, kodeProvinsi = "14", namaProvinsi = "Riau"),
    Provinsi(id = 5, kodeProvinsi = "15", namaProvinsi = "Jambi"),
    Provinsi(id = 6, kodeProvinsi = "16", namaProvinsi = "Sumatera Selatan"),
    Provinsi(id = 7, kodeProvinsi = "17", namaProvinsi = "Bengkulu"),
    Provinsi(id = 8, kodeProvinsi = "18", namaProvinsi = "Lampung"),
    Provinsi(id = 9, kodeProvinsi = "19", namaProvinsi = "Kep. Bangka Belitung"),
    Provinsi(id = 10, kodeProvinsi = "21", namaProvinsi = "Kep. Riau"),
    Provinsi(id = 11, kodeProvinsi = "31", namaProvinsi = "Dki Jakarta"),
    Provinsi(id = 12, kodeProvinsi = "32", namaProvinsi = "Jawa Barat"),
    Provinsi(id = 13, kodeProvinsi = "33", namaProvinsi = "Jawa Tengah"),
    Provinsi(id = 14, kodeProvinsi = "34", namaProvinsi = "Di Yogyakarta"),
    Provinsi(id = 15, kodeProvinsi = "35", namaProvinsi = "Jawa Timur"),
    Provinsi(id = 16, kodeProvinsi = "36", namaProvinsi = "Banten"),
    Provinsi(id = 17, kodeProvinsi = "51", namaProvinsi = "Bali"),
    Provinsi(id = 18, kodeProvinsi = "52", namaProvinsi = "Nusa Tenggara Barat"),
    Provinsi(id = 19, kodeProvinsi = "53", namaProvinsi = "Nusa Tenggara Timur"),
    Provinsi(id = 20, kodeProvinsi = "61", namaProvinsi = "Kalimantan Barat"),
    Provinsi(id = 21, kodeProvinsi = "62", namaProvinsi = "Kalimantan Tengah"),
    Provinsi(id = 22, kodeProvinsi = "63", namaProvinsi = "Kalimantan Selatan"),
    Provinsi(id = 23, kodeProvinsi = "64", namaProvinsi = "Kalimantan Timur"),
    Provinsi(id = 24, kodeProvinsi = "65", namaProvinsi = "Kalimantan Utara"),
    Provinsi(id = 25, kodeProvinsi = "71", namaProvinsi = "Sulawesi Utara"),
    Provinsi(id = 26, kodeProvinsi = "72", namaProvinsi = "Sulawesi Tengah"),
    Provinsi(id = 27, kodeProvinsi = "73", namaProvinsi = "Sulawesi Selatan"),
    Provinsi(id = 28, kodeProvinsi = "74", namaProvinsi = "Sulawesi Tenggara"),
    Provinsi(id = 29, kodeProvinsi = "75", namaProvinsi = "Gorontalo"),
    Provinsi(id = 30, kodeProvinsi = "76", namaProvinsi = "Sulawesi Barat"),
    Provinsi(id = 31, kodeProvinsi = "81", namaProvinsi = "Maluku"),
    Provinsi(id = 32, kodeProvinsi = "82", namaProvinsi = "Maluku Utara"),
    Provinsi(id = 33, kodeProvinsi = "91", namaProvinsi = "Papua Barat"),
    Provinsi(id = 34, kodeProvinsi = "94", namaProvinsi = "Papua")
)



