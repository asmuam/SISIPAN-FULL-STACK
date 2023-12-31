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

import android.util.Log
import androidx.compose.ui.platform.LocalContext
import com.polstat.sisipan.data.UserRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.polstat.sisipan.Graph
import com.polstat.sisipan.api.AuthFetcher
import com.polstat.sisipan.api.AuthRequest
import com.polstat.sisipan.api.AuthResult
import com.polstat.sisipan.api.AuthService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SignInViewModel : ViewModel() {

    private val authService: AuthFetcher = Graph.authService
    private val userRepository: UserRepository = Graph.userRepository

    private val _authResult = MutableStateFlow<AuthResult?>(null)
    val authResult: StateFlow<AuthResult?> = _authResult

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            runCatching {
                _authResult.value = null
                val response = authService.login(AuthRequest(email, password))
                Log.i("TAG", "signIn DATA: ${response.data}")
                if (response.httpStatusCode == 200) {
                    response.data?.let { data ->
                        userRepository.setAllUserData(
                            accessToken = data.accessToken ?: "",
                            role = data.role ?: "",
                            id = data.id ?: 0L,
                            email = data.email ?: "",
                            idMhs = data.idMhs ?: 0L,
                            expiresIn = data.expiresIn ?: 0L
                        )
                        _authResult.value = AuthResult.SUCCESS
                        Log.i(
                            "TAG", "signIn: ${
                                userRepository.toString()
                            }"
                        )
                    }
                } else {
                    _authResult.value = AuthResult.FAILURE
                    Log.i(
                        "TAG", "signIn: ${
                            userRepository.toString()
                        }"
                    )
                }
            }.onFailure { e ->
                _authResult.value = AuthResult.FAILURE
                // Handle the exception here if needed
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
