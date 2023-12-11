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

import UserRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.polstat.sisipan.Graph
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FormasiViewModel(
    private val userRepository:UserRepository = Graph.userRepository
) : ViewModel() {

    // Holds our view state which the UI collects via [state]
    private val _state = MutableStateFlow(FormasiViewState())

    private val refreshing = MutableStateFlow(false)

    val state: StateFlow<FormasiViewState>
        get() = _state

    init {
        _state.value = _state.value.copy(role = userRepository.getRole())

        viewModelScope.launch {
        }

        refresh(force = false)
    }

    private fun refresh(force: Boolean) {
        viewModelScope.launch {
            runCatching {
                refreshing.value = true
            }
            // TODO: look at result of runCatching and show any errors

            refreshing.value = false
        }
    }

}

data class FormasiViewState(
    val role: String? = null,
    val refreshing: Boolean = false,
    val errorMessage: String? = null
)
