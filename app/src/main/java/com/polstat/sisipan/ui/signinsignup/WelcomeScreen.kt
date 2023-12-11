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

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.polstat.sisipan.R
import com.polstat.sisipan.ui.theme.SisipanTheme
import com.polstat.sisipan.ui.theme.stronglyDeemphasizedAlpha
import com.polstat.sisipan.util.supportWideScreen

@Composable
fun WelcomeScreen(
    onNavigateToSignIn: () -> Unit,
    onNavigateToSignUp: () -> Unit,
) {
    var showBranding by rememberSaveable { mutableStateOf(true) }

    Scaffold(modifier = Modifier.supportWideScreen()) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            AnimatedVisibility(
                showBranding,
                Modifier.fillMaxWidth()
            ) {
                Branding()
            }

            SignInCreateAccount(
                onSignIn = onNavigateToSignIn,
                onSignUp = onNavigateToSignUp,
                onFocusChange = { focused -> showBranding = !focused },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            )
        }
    }
}

@Composable
private fun Branding(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.wrapContentHeight(align = Alignment.CenterVertically)
    ) {
        Logo(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(horizontal = 76.dp)
        )
        Text(
            text = stringResource(id = R.string.app_tagline),
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(top = 24.dp)
                .fillMaxWidth()
        )
    }
}

@Composable
private fun Logo(
    modifier: Modifier = Modifier,
    lightTheme: Boolean = LocalContentColor.current.luminance() < 0.5f,
) {
    val assetId = if (lightTheme) {
        R.drawable.ic_logo_light
    } else {
        R.drawable.ic_logo_dark
    }
    Image(
        painter = painterResource(id = assetId),
        modifier = modifier,
        contentDescription = null
    )
}

@Composable
private fun SignInCreateAccount(
    onSignIn: () -> Unit,
    onSignUp: () -> Unit,
    onFocusChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    // ... (bagian kode sebelumnya)

    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        // ... (kode lainnya)

        // Tombol "Register"
        Button(
            onClick = {
                    onSignUp() // Panggil fungsi onSignUp saat tombol "Register" diklik
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 28.dp, bottom = 3.dp)
        ) {
            Text(
                text = stringResource(id = R.string.user_register),
                style = MaterialTheme.typography.titleSmall
            )
        }

        // Tombol "Login"
        Button(
            onClick = {
                    onSignIn() // Panggil fungsi onSignIn saat tombol "Login" diklik
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 28.dp) // Ubah jarak dari atas dan bawah sesuai kebutuhan
        ) {
            Text(
                text = stringResource(id = R.string.user_login),
                style = MaterialTheme.typography.titleSmall
            )
        }
    }
}


@Preview(name = "Welcome light theme", uiMode = UI_MODE_NIGHT_YES)
@Preview(name = "Welcome dark theme", uiMode = UI_MODE_NIGHT_NO)
@Composable
fun WelcomeScreenPreview() {
    SisipanTheme {
        WelcomeScreen(
            onNavigateToSignIn = {},
            onNavigateToSignUp ={},
        )
    }
}
