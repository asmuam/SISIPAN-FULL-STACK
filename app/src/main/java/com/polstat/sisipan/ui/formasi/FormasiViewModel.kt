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

package com.polstat.sisipan.ui.formasi

import com.polstat.sisipan.data.UserRepository
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.polstat.sisipan.Graph
import com.polstat.sisipan.data.Formasi
import com.polstat.sisipan.data.FormasiRepository
import com.polstat.sisipan.data.FormasiStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class FormasiViewModel(
    private val userRepository: UserRepository = Graph.userRepository,
    private val formasiRepository: FormasiRepository = Graph.formasiRepository,
    private val formasiStore: FormasiStore = Graph.formasiStore
) : ViewModel() {

    // Holds our view state which the UI collects via [state]
    private val _state = MutableStateFlow(FormasiViewState())

    private val refreshing = MutableStateFlow(true)

    val state: StateFlow<FormasiViewState>
        get() = _state

    init {
        viewModelScope.launch {
            // Combines the latest value from each of the flows, allowing us to generate a
            // view state instance which only contains the latest values.
            combine(
                formasiStore.formasiBuka(),
                formasiStore.formasiTutup(),
                refreshing
            ) { formasiBukaList,formasiTutupList, refreshing ->
                FormasiViewState(
                    role = userRepository.role ?: "", //default ""
                    formasiBukaList = formasiBukaList,
                    formasiTutupList = formasiTutupList,
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
                formasiRepository.refreshFormasi(force)
                // Handle the response
            } catch (e: Exception) {
                // Handle the error
                Log.e("FormasiViewModel", "Error refreshing formasi", e)
            } finally {
                refreshing.value = false
            }
        }
    }

    fun initRefresh() {
        Log.i("TAG", "initRefresh: execute")
        refresh(force = true)
    }
}

data class FormasiViewState(
    val role: String = "",
    val formasiBukaList: List<Formasi> = emptyList(),
    val formasiTutupList: List<Formasi> = emptyList(),
    val refreshing: Boolean = false,
    val errorMessage: String? = null
)

