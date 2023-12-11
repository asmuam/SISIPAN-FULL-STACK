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

package com.polstat.sisipan.ui.signinsignup

import UserRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.polstat.sisipan.Graph
import com.polstat.sisipan.api.AuthRequest
import com.polstat.sisipan.api.AuthResult
import com.polstat.sisipan.api.AuthService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SignUpViewModel(
) : ViewModel() {

    private val authService: AuthService = Graph.authService

    private val _authResult = MutableStateFlow<AuthResult?>(null)
    val authResult: StateFlow<AuthResult?> = _authResult

    fun signUp(email: String, password: String) {
        viewModelScope.launch {
            try {
                _authResult.value = null
                val response = authService.register(AuthRequest(email, password))

                if (response.httpStatusCode == 200) {
                    _authResult.value = AuthResult.SUCCESS
                } else {
                    _authResult.value = AuthResult.FAILURE
                }
            } catch (e: Exception) {
                _authResult.value = AuthResult.FAILURE
            }
        }
    }
}

class SignUpViewModelFactory() : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SignUpViewModel::class.java)) {
            return SignUpViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
