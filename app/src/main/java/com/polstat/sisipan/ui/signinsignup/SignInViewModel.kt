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
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.polstat.sisipan.Graph
import com.polstat.sisipan.api.ApiClient.authService
import com.polstat.sisipan.api.AuthRequest
import com.polstat.sisipan.api.AuthResult
import com.polstat.sisipan.api.AuthService
import com.polstat.sisipan.api.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SignInViewModel : ViewModel() {

    private val authService: AuthService = Graph.authService
    private val userRepository: UserRepository = Graph.userRepository

    private val _authResult = MutableStateFlow<AuthResult?>(null)
    val authResult: StateFlow<AuthResult?> = _authResult

    fun signIn(email: String, password: String, onLoginSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                _authResult.value = null
                val response = authService.login(AuthRequest(email, password))

                if (response.httpStatusCode == 200) {
                    response.data?.let { data ->
                        userRepository.setAllUserData(
                            accessToken = data.accessToken ?: "",
                            role = data.role ?: "",
                            id = data.id ?: 0L,
                            email = data.email ?: "",
                            idMhs = data.idMhs ?: 0L
                        )
                    }
                    onLoginSuccess()
                } else {
                    _authResult.value = AuthResult.FAILURE
                }
            } catch (e: Exception) {
                _authResult.value = AuthResult.FAILURE
            }
        }
    }
}


class SignInViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SignInViewModel::class.java)) {
            return SignInViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
