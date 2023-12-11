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

package ccom.polstat.sisipan.ui.signinsignup

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.polstat.sisipan.R
import com.polstat.sisipan.api.AuthResult
import com.polstat.sisipan.ui.signinsignup.ConfirmPasswordState
import com.polstat.sisipan.ui.signinsignup.Email
import com.polstat.sisipan.ui.signinsignup.EmailState
import com.polstat.sisipan.ui.signinsignup.ErrorSnackbar
import com.polstat.sisipan.ui.signinsignup.Password
import com.polstat.sisipan.ui.signinsignup.PasswordState
import com.polstat.sisipan.ui.signinsignup.SignInSignUpScreen
import com.polstat.sisipan.ui.signinsignup.SignInSignUpTopAppBar
import com.polstat.sisipan.ui.theme.SisipanTheme
import com.polstat.sisipan.ui.theme.stronglyDeemphasizedAlpha
import com.polstat.sisipan.util.supportWideScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SignUpScreen(
    onSignUpSubmitted: (email: String, password: String) -> Unit,
    onNavUp: () -> Unit,
    authResult : AuthResult?,
    onSignUpSuccess :()->Unit
    ) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current

    val snackbarErrorText = stringResource(id = R.string.feature_not_available)
    val snackbarActionLabel = stringResource(id = R.string.dismiss)

    LaunchedEffect(authResult) {
        keyboardController?.hide()
        Log.i("TAG", "SignUpScreen: $authResult")
        when (authResult) {
            AuthResult.FAILURE -> {
                scope.launch {
                    snackbarHostState.showSnackbar(
                        message = "Terjadi Kesalahan",
                        actionLabel = snackbarActionLabel
                    )
                }
            }
            AuthResult.SUCCESS -> {
                scope.launch {
                    snackbarHostState.showSnackbar(
                        message = "Registrasi berhasil! Redirecting...",
                        actionLabel = snackbarActionLabel
                    )
                }

                // Tunggu sebentar sebelum menavigasi
                delay(2000)

                // Eksekusi callback untuk menavigasi ke layar selanjutnya
                onSignUpSuccess()
            }
            else -> Unit
        }
    }
        Scaffold(
        topBar = {
            SignInSignUpTopAppBar(
                topAppBarText = stringResource(id = R.string.create_account),
                onNavUp = onNavUp,
            )
        },
        content = { contentPadding ->
            SignInSignUpScreen(
                contentPadding = contentPadding,
                modifier = Modifier.supportWideScreen()
            ) {
                Column {
                    SignUpContent(
                        onSignUpSubmitted = onSignUpSubmitted
                    )
                }
            }
        }
    )
    Box(modifier = Modifier.fillMaxSize()) {
        ErrorSnackbar(
            snackbarHostState = snackbarHostState,
            onDismiss = { snackbarHostState.currentSnackbarData?.dismiss() },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}


@Composable
fun SignUpContent(
    onSignUpSubmitted: (email: String, password: String) -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        val passwordFocusRequest = remember { FocusRequester() }
        val confirmationPasswordFocusRequest = remember { FocusRequester() }
        val emailState = remember { EmailState("") }
        Email(emailState, onImeAction = { passwordFocusRequest.requestFocus() })

        Spacer(modifier = Modifier.height(16.dp))
        val passwordState = remember { PasswordState() }
        Password(
            label = stringResource(id = R.string.password),
            passwordState = passwordState,
            imeAction = ImeAction.Next,
            onImeAction = { confirmationPasswordFocusRequest.requestFocus() },
            modifier = Modifier.focusRequester(passwordFocusRequest)
        )

        Spacer(modifier = Modifier.height(16.dp))
        val confirmPasswordState = remember { ConfirmPasswordState(passwordState = passwordState) }
        Password(
            label = stringResource(id = R.string.confirm_password),
            passwordState = confirmPasswordState,
            onImeAction = { onSignUpSubmitted(emailState.text, passwordState.text) },
            modifier = Modifier.focusRequester(confirmationPasswordFocusRequest)
        )

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(id = R.string.terms_and_conditions),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = stronglyDeemphasizedAlpha)
        )

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { onSignUpSubmitted(emailState.text, passwordState.text) },
            modifier = Modifier.fillMaxWidth(),
            enabled = emailState.isValid &&
                passwordState.isValid && confirmPasswordState.isValid
        ) {
            Text(text = stringResource(id = R.string.create_account))
        }
    }
}

@Preview(widthDp = 1024)
@Composable
fun SignUpPreview() {
    SisipanTheme {
        SignUpScreen(
            onSignUpSubmitted = { _, _ -> },
            onNavUp = {},
            authResult = null,
            onSignUpSuccess = {  -> },
            )
    }
}
