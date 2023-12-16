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
import com.polstat.sisipan.data.UserRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.polstat.sisipan.Graph
import com.polstat.sisipan.Graph.pilihanStore
import com.polstat.sisipan.data.FormasiStore
import com.polstat.sisipan.data.MahasiswaRepository
import com.polstat.sisipan.data.MahasiswaStore
import com.polstat.sisipan.data.Pilihan
import com.polstat.sisipan.data.PilihanNested
import com.polstat.sisipan.data.PilihanRepository
import com.polstat.sisipan.data.PilihanStore
import com.polstat.sisipan.data.ProvinsiRepository
import com.polstat.sisipan.ui.formasi.FormasiViewState
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
        Log.i("TAGHomebefore","DATABASE init ${Graph.isDatabaseInitialized()}")
        Log.i("TAGHomebefore","DATABASE open ${Graph.isDatabaseOpen()}")
        observeRefresh()
        refresh(force = false)
        Log.i("TAGHomeafter","DATABASE init ${Graph.isDatabaseInitialized()}")
        Log.i("TAGHomeafter","DATABASE open ${Graph.isDatabaseOpen()}")
    }

    private fun observeRefresh() {
        viewModelScope.launch {
            combine(
                refreshing
            ) { refreshingValue  ->
                val idmhs = userRepository.idMhs
                Log.i("TAG", "observeRefresh: Checkpoint1")
                if (idmhs!=null) {
                    Log.i("TAG", "observeRefresh: Checkpoint2")
                    val pilihan = pilihanStore.pilihanByMhs(idmhs)
                    val memilih = true
                    val mahasiswa = mahasiswaStore.getMahasiswa(pilihan.mahasiswa)
                    val pilihan1 = formasiStore.formasiById(pilihan.pilihan1 ?: 0)
                    val pilihan2 = formasiStore.formasiById(pilihan.pilihan2 ?: 0)
                    val pilihan3 = formasiStore.formasiById(pilihan.pilihan3 ?: 0)
                    val pilihanSistem = formasiStore.formasiById(pilihan.pilihanSistem ?: 0)
                    val pilihanSaya = PilihanNested(
                        id = pilihan.id,
                        mahasiswa = mahasiswa,
                        pilihan1 = pilihan1,
                        pilihan2 = pilihan2,
                        pilihan3 = pilihan3,
                        pilihanSistem = pilihanSistem,
                        indeksPilihan1 = pilihan.indeksPilihan1,
                        indeksPilihan2 = pilihan.indeksPilihan2,
                        indeksPilihan3 = pilihan.indeksPilihan3,
                        ipk = pilihan.ipk,
                        hasil = pilihan.hasil
                    )
                    Log.i("TAG", "observeRefresh: Checkpoint3")
                    HomeViewState(
                        role = userRepository.role,
                        pilihanSaya = pilihanSaya,
                        mahasiswaMemilih = pilihanStore.count(),
                        jmlhMhs = mahasiswaStore.count(),
                        memilih = memilih,
                        refreshing = refreshingValue.get(0) ,
                        errorMessage = null /* TODO */
                    )
                } else{
                    Log.i("TAG", "observeRefresh: Checkpoint4")
                    HomeViewState(
                        role = userRepository.role,
                        mahasiswaMemilih = pilihanStore.count(),
                        jmlhMhs = mahasiswaStore.count(),
                        memilih = false,
                        refreshing = refreshingValue.get(0) ,
                        errorMessage = null /* TODO */
                    )
                }
            }.catch { throwable ->
                Log.i("TAG", "observeRefresh: Checkpoint5")
                // TODO: emit a UI error here. For now, we'll just rethrow
                throw throwable
            }.collect {
                Log.i("TAG", "observeRefresh: Checkpoint6")
                _state.value = it
                Log.i("TAG", "DATABASE open after collect: ${Graph.isDatabaseOpen()}")
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
