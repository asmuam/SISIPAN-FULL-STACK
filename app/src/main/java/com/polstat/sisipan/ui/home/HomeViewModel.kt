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

package com.polstat.sisipan.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.polstat.sisipan.Graph
import com.polstat.sisipan.data.FormasiStore
import com.polstat.sisipan.data.MahasiswaRepository
import com.polstat.sisipan.data.MahasiswaStore
import com.polstat.sisipan.data.PilihanNested
import com.polstat.sisipan.data.PilihanRepository
import com.polstat.sisipan.data.PilihanStore
import com.polstat.sisipan.data.ProvinsiRepository
import com.polstat.sisipan.data.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class HomeViewModel(
    private val userRepository: UserRepository = Graph.userRepository,
    private val pilihanRepository: PilihanRepository = Graph.pilihanRepository,
    private val pilihanStore: PilihanStore = Graph.pilihanStore,
    private val mahasiswaStore: MahasiswaStore = Graph.mahasiswaStore,
    private val provinsiRepository: ProvinsiRepository = Graph.provinsiRepository,
    private val mahasiswaRepository: MahasiswaRepository = Graph.mahasiswaRepository,
    private val formasiStore: FormasiStore = Graph.formasiStore,
) : ViewModel() {

    private val _state = MutableStateFlow(HomeViewState())
    private val refreshing = MutableStateFlow(false)

    val state: StateFlow<HomeViewState>
        get() = _state

    init {
        Log.i("TAGHomebefore", "DATABASE init ${Graph.isDatabaseInitialized()}")
        Log.i("TAGHomebefore", "DATABASE open ${Graph.isDatabaseOpen()}")
        observeRefresh()
        refresh(force = false)
        Log.i("TAGHomeafter", "DATABASE init ${Graph.isDatabaseInitialized()}")
        Log.i("TAGHomeafter", "DATABASE open ${Graph.isDatabaseOpen()}")
    }

    private fun observeRefresh() {
        doRefresh(true)
        viewModelScope.launch {
            combine(
                refreshing
            ) { refreshingValue ->
                val idmhs = userRepository.idMhs
                val pilihan = idmhs?.let { pilihanStore.pilihanByMhs(it) }
                val memilih = pilihan != null
                Log.i("TAG", "memilih:${memilih} ")
                Log.i("TAG", "memilih:${pilihan} ")
                val pilihanSaya = pilihan?.let {
                    val mahasiswa = mahasiswaStore.getMahasiswa(it.mahasiswa)
                    val pilihan1 = formasiStore.formasiById(it.pilihan1 ?: 0)
                    val pilihan2 = formasiStore.formasiById(it.pilihan2 ?: 0)
                    val pilihan3 = formasiStore.formasiById(it.pilihan3 ?: 0)
                    val pilihanSistem = formasiStore.formasiById(it.pilihanSistem ?: 0)

                    PilihanNested(
                        id = it.id,
                        mahasiswa = mahasiswa,
                        pilihan1 = pilihan1,
                        pilihan2 = pilihan2,
                        pilihan3 = pilihan3,
                        pilihanSistem = pilihanSistem,
                        indeksPilihan1 = it.indeksPilihan1,
                        indeksPilihan2 = it.indeksPilihan2,
                        indeksPilihan3 = it.indeksPilihan3,
                        ipk = it.ipk,
                        hasil = it.hasil
                    )
                }

                val homeViewState = if (idmhs != null) {
                    HomeViewState(
                        role = userRepository.role,
                        pilihanSaya = pilihanSaya,
                        mahasiswaMemilih = pilihanStore.count(),
                        jmlhMhs = mahasiswaStore.count(),
                        memilih = memilih,
                        refreshing = refreshingValue.get(0),
                        errorMessage = null /* TODO */
                    )
                } else {
                    HomeViewState(
                        role = userRepository.role,
                        mahasiswaMemilih = pilihanStore.count(),
                        jmlhMhs = mahasiswaStore.count(),
                        memilih = false,
                        refreshing = refreshingValue.get(0),
                        errorMessage = null /* TODO */
                    )
                }

                homeViewState // Return the computed HomeViewState
            }.catch { throwable ->
                // Handle errors here
                throw throwable
            }.collect {
                _state.value = it
            }
        }
    }


    private fun doRefresh(force: Boolean) {
        viewModelScope.launch {
            runCatching {
                refreshing.value = true
                pilihanRepository.refreshPilihan(force)
                provinsiRepository.refreshProvinsi(force)
                mahasiswaRepository.refreshMahasiswa(force)
            }
            // TODO: handle result and show any errors

            refreshing.value = false
        }
    }

    fun refresh(force: Boolean) {
        doRefresh(force)
    }

    fun penempatan() {
        Log.i("TAG", "penempatan: DO")
        viewModelScope.launch {
            Log.i("TAG", "start penempatan: DO")
            pilihanRepository.doPenempatan()
            Log.i("TAG", "finish penempatan: DO")
        }
    }

}

data class HomeViewState(
    val role: String? = null,
    val pilihanSaya: PilihanNested? = null,
    val mahasiswaMemilih: Int = 0,
    val jmlhMhs: Int = 0,
    val memilih: Boolean = false,
    val refreshing: Boolean = false,
    val errorMessage: String? = null
)
