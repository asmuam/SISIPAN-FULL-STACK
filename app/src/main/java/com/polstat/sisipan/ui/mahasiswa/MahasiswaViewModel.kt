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

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.provider.MediaStore
import android.util.Log
import com.polstat.sisipan.data.UserRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.polstat.sisipan.Graph
import com.polstat.sisipan.data.MahasiswaRepository
import com.polstat.sisipan.data.MahasiswaStore
import com.polstat.sisipan.data.Provinsi
import com.polstat.sisipan.data.ProvinsiStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class MahasiswaViewModel(
    private val userRepository: UserRepository = Graph.userRepository,
    private val mahasiswaRepository: MahasiswaRepository = Graph.mahasiswaRepository,
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
            ) { mahasiswaCollection, refreshing ->
                val mappedMahasiwaList = mahasiswaCollection.map { mhs ->
                    val prov = provinsiStore.getById(mhs.provinsiId)
                MahasiswaCollection(
                    id = mhs.id,
                    nim = mhs.nim,
                    name = mhs.name,
                    prodi = mhs.prodi,
                    provinsi = prov,
                    ipk = mhs.ipk,
                )
                }
                MahasiswaViewState(
                    role = userRepository.role,
                    email = userRepository.email,
                    mahasiswa = mappedMahasiwaList,
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
                mahasiswaRepository.refreshMahasiwa(force)
                // Handle the response
            } catch (e: Exception) {
                // Handle the error
                Log.e("MhsViewModel", "Error refreshing MHS", e)
            } finally {
                refreshing.value = false
            }
        }
    }

    fun editMahasiswaPhoto(context: Context) {
        // Buka galeri atau kamera, dan tangani hasilnya di sini
        // Misalnya, menggunakan Intent untuk membuka galeri:
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        (context as? Activity)?.startActivityForResult(intent, REQUEST_IMAGE_PICK)
    }

    companion object {
        const val REQUEST_IMAGE_PICK = 123
    }
}

data class MahasiswaViewState(
    val role: String? = null,
    val refreshing: Boolean = false,
    val errorMessage: String? = null,
    val email: String = "",
    val password: String ="",
    val mahasiswa: List<MahasiswaCollection> = emptyList(),
    val provinsi: Provinsi? = null,
)

data class MahasiswaCollection (
    val id: Long,
    val nim: String,
    val name: String,
    val prodi: String,
    val provinsi: Flow<Provinsi>,
    val ipk: Float,
)
