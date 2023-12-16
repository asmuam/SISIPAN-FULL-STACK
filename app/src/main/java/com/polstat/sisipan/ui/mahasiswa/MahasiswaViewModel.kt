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

package com.polstat.sisipan.ui.mahasiswa

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.polstat.sisipan.Graph
import com.polstat.sisipan.data.MahasiswaRepository
import com.polstat.sisipan.data.MahasiswaStore
import com.polstat.sisipan.data.Provinsi
import com.polstat.sisipan.data.ProvinsiRepository
import com.polstat.sisipan.data.ProvinsiStore
import com.polstat.sisipan.data.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class MahasiswaViewModel(
    private val userRepository: UserRepository = Graph.userRepository,
    private val mahasiswaRepository: MahasiswaRepository = Graph.mahasiswaRepository,
    private val provinsiRepository: ProvinsiRepository = Graph.provinsiRepository,
    private val mahasiswaStore: MahasiswaStore = Graph.mahasiswaStore,
    private val provinsiStore: ProvinsiStore = Graph.provinsiStore,
) : ViewModel() {

    // Holds our view state which the UI collects via [state]
    private val _state = MutableStateFlow(MahasiswaViewState())

    private val refreshing = MutableStateFlow(false)

    val state: StateFlow<MahasiswaViewState>
        get() = _state

    init {
        refresh(force = false)
        viewModelScope.launch {
            combine(
                mahasiswaStore.getAll(),
                refreshing,
            ) { listMahasiswa, refreshing ->
                val mappedMahasiswaList = listMahasiswa.map { mhs ->
                    val prov = provinsiStore.getById(mhs.provinsi)
                MahasiswaCollection(
                    id = mhs.id,
                    nim = mhs.nim,
                    name = mhs.name,
                    prodi = mhs.prodi,
                    provinsi = prov?: Provinsi(0L,"0000",""),
                    ipk = mhs.ipk,
                )
                }
                MahasiswaViewState(
                    role = userRepository.role,
                    mahasiswa = mappedMahasiswaList,
                    refreshing = refreshing,
                    errorMessage = null /* TODO */,
                )
            }.catch { throwable ->
                // TODO: emit a UI error here. For now, we'll just rethrow
                throw throwable
            }.collect {
                _state.value = it
            }
        }
    }

    fun refresh(force: Boolean) {
        viewModelScope.launch {
            try {
                refreshing.value = true
                mahasiswaRepository.refreshMahasiswa(force)
                provinsiRepository.refreshProvinsi(force)
                // Handle the response
            } catch (e: Exception) {
                // Handle the error
                Log.e("MhsViewModel", "Error refreshing MHS", e)
            } finally {
                refreshing.value = false
            }
        }
    }

}

data class MahasiswaViewState(
    val role: String? = null,
    val refreshing: Boolean = false,
    val errorMessage: String? = null,
    val password: String ="",
    val mahasiswa: List<MahasiswaCollection> = emptyList(),
)

data class MahasiswaCollection (
    val id: Long,
    val nim: String,
    val name: String,
    val prodi: String,
    val provinsi: Provinsi,
    val ipk: Float,
)
