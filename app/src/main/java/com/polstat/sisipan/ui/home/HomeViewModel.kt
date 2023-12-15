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
    private val provinsiRepository: ProvinsiRepository = Graph.provinsiRepository,
) : ViewModel() {

    // Holds our view state which the UI collects via [state]
    private val _state = MutableStateFlow(HomeViewState())

    private val refreshing = MutableStateFlow(false)

    val state: StateFlow<HomeViewState>
        get() = _state

    init {
        refresh(force = false)
        viewModelScope.launch {
            combine(
                refreshing
            ) { refreshingValue  ->
                HomeViewState(
                    role = userRepository.role,
                    mahasiswaMemilih = pilihanStore.count(),
                    refreshing = refreshingValue.get(0) ,
                    errorMessage = null /* TODO */
                )
            }.catch { throwable ->
                // TODO: emit a UI error here. For now, we'll just rethrow
                throw throwable
            }.collect {
                Log.e("PILIHAN_REPO", "COUNT${pilihanStore.count()}")
                _state.value = it
            }
        }
    }

    private fun doRefresh(force: Boolean) {
        viewModelScope.launch {
            runCatching {
                refreshing.value = true
                pilihanRepository.refreshPilihan(force)
                provinsiRepository.refreshrovinsi(force)
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
    val mahasiswaMemilih: Int = 0,
    val refreshing: Boolean = false,
    val errorMessage: String? = null
)
