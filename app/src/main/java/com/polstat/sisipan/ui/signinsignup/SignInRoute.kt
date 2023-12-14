/*
 * Copyright 2023 The Android Open Source Project
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
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.polstat.sisipan.Graph.userRepository
import com.polstat.sisipan.api.AuthResult


@Composable
fun SignInRoute(
    onLoginSuccess: () -> Unit,
    onNavUp: () -> Unit,
) {
    val signInViewModel: SignInViewModel = viewModel(factory = SignInViewModelFactory())
    val authResult by signInViewModel.authResult.collectAsState()

    LaunchedEffect(authResult) {
        if (authResult == AuthResult.SUCCESS) {
            onLoginSuccess()
        }
    }

    SignInScreen(
        onLoginSuccess = { email, password ->
            signInViewModel.signIn(email, password)
        },
        onNavUp = onNavUp,
        authResult = authResult
    )
}

