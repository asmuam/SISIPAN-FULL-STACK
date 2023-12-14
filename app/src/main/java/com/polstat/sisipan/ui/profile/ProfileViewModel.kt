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

package com.polstat.sisipan.ui.profile

import android.util.Log
import com.polstat.sisipan.data.UserRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.polstat.sisipan.Graph
import com.polstat.sisipan.data.FormasiRepository
import com.polstat.sisipan.data.FormasiStore
import com.polstat.sisipan.data.Mahasiswa
import com.polstat.sisipan.data.MahasiswaRepository
import com.polstat.sisipan.data.MahasiswaStore
import com.polstat.sisipan.ui.formasi.FormasiViewState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val userRepository: UserRepository = Graph.userRepository,
    private val mahasiswaRepository: MahasiswaRepository = Graph.mahasiswaRepository,
    private val mahasiswaStore: MahasiswaStore = Graph.mahasiswaStore
) : ViewModel() {

    // Holds our view state which the UI collects via [state]
    private val _state = MutableStateFlow(ProfileViewState())

    private val refreshing = MutableStateFlow(false)

    val state: StateFlow<ProfileViewState>
        get() = _state

    init {

        viewModelScope.launch {
            combine(
                mahasiswaStore.getMahasiwa(userRepository.idMhs),
                refreshing
            ) { mahasiswaDetail, refreshing ->
                ProfileViewState(
                    role = userRepository.role,
                    email = userRepository.email,
                    mahasiswa = mahasiswaDetail,
                    refreshing = refreshing,
                    errorMessage = null /* TODO */
                )
            }.catch { throwable ->
                // TODO: emit a UI error here. For now, we'll just rethrow
                throw throwable
            }.collect {
                _state.value = it
            }
        }

        refresh(force = false)
    }

    private fun refresh(force: Boolean) {
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

}

data class ProfileViewState(
    val role: String? = null,
    val refreshing: Boolean = false,
    val errorMessage: String? = null,
    val email: String = "",
    val password: String ="",
    val mahasiswa: Mahasiswa? = null,
)
