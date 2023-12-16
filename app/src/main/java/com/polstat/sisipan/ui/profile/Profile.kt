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

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.polstat.sisipan.R
import com.polstat.sisipan.data.Mahasiswa
import com.polstat.sisipan.data.Provinsi
import com.polstat.sisipan.ui.theme.SisipanTheme
import com.polstat.sisipan.util.DynamicThemePrimaryColorsFromImage
import com.polstat.sisipan.util.baselineHeight
import com.polstat.sisipan.util.verticalGradientScrim

@Composable
fun Profile(
    openDrawer: () -> Unit,
    viewModel: ProfileViewModel = viewModel(),
    onAccount: ()-> Unit,
    navigateBack:()-> Unit,
    ) {
    val viewState by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    Surface(Modifier.fillMaxSize()) {
        ProfileContent(
            openDrawer,
            isRefreshing = viewState.refreshing,
            modifier = Modifier.fillMaxSize(),
            profil = viewState.mahasiswa,
            email = viewState.email,
            provinsi =viewState.provinsi,
            onAccount,
            onEditProfilePhoto = {
                viewModel.editProfilePhoto(context)
            },
            navigateBack =navigateBack,
        )
    }
}

@Composable
fun ProfileAppBar(
    openDrawer: () -> Unit,
    backgroundColor: Color,
    modifier: Modifier = Modifier,
    onAccount: ()-> Unit,
    navigateBack:()-> Unit,
    ) {
    TopAppBar(
        title = {
                Row {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = stringResource(R.string.app_name),
                        modifier = Modifier
                            .padding(start = 4.dp)
                            .heightIn(max = 24.dp)
                            .clickable {navigateBack()}
                            .align(Alignment.CenterVertically)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Image(
                        painter = painterResource(R.drawable.logo),
                        contentDescription = null,
                        modifier = Modifier.clickable { openDrawer() }
                    )
                }
        },
        backgroundColor = backgroundColor,
        actions = {
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            }
        },
        modifier = modifier
    )
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ProfileContent(
    openDrawer: () -> Unit,
    isRefreshing: Boolean,
    modifier: Modifier = Modifier,
    profil: Mahasiswa?,
    email: String,
    provinsi: Provinsi?,
    onAccount: () -> Unit,
    onEditProfilePhoto: () -> Unit,
    navigateBack:()-> Unit,
    ) {
    Column(
        modifier = modifier.windowInsetsPadding(
            WindowInsets.systemBars.only(WindowInsetsSides.Horizontal)
        )
    ) {
        // Scrim dan AppBar
        DynamicThemePrimaryColorsFromImage {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalGradientScrim(
                        color = MaterialTheme.colors.primary.copy(alpha = 0.38f),
                        startYPercentage = 1f,
                        endYPercentage = 0f
                    )
            ) {
                // Draw a scrim over the status bar which matches the app bar
                Spacer(
                    Modifier
                        .background(MaterialTheme.colors.surface.copy(alpha = 0.87f))
                        .fillMaxWidth()
                        .windowInsetsTopHeight(WindowInsets.statusBars)
                )

                // AppBar dengan tombol navigasi dan ikon pengaturan
                ProfileAppBar(
                    openDrawer,
                    backgroundColor = MaterialTheme.colors.surface.copy(alpha = 0.87f),
                    modifier = Modifier.fillMaxWidth(),
                    onAccount,
                    navigateBack=navigateBack,
                )
            }
        }

        // Bagian tengah layout dengan foto profil, ikon kamera, dan data mahasiswa
        Box(
            modifier = Modifier
                .padding(16.dp)
                .background(Color.Transparent)
                .align(Alignment.CenterHorizontally)
        ) {
            // Foto profil (gantilah dengan gambar sesuai profil)
            Image(
                painter = painterResource(id = R.drawable.profil),
                contentDescription = null,
                modifier = Modifier
                    .clip(CircleShape)
                    .align(Alignment.Center)
                    .background(Color.Transparent)
            )

            // Ikon kamera untuk mengedit foto profil
            Icon(
                imageVector = Icons.Default.PhotoCamera,
                contentDescription = "Edit Profile Photo",
                modifier = Modifier
                    .clip(CircleShape)
                    .align(Alignment.BottomEnd)
                    .size(50.dp)
                    .background(Color.Blue)
                    .zIndex(2f) // Menempatkan ikon di atas gambar profil
                    .clickable { onEditProfilePhoto() } // Tambahkan fungsi klik untuk ikon kamera
                    .graphicsLayer(scaleY = 0.5f, scaleX = 0.5f) // Sesuaikan nilai scale sesuai keinginan Anda
            )
        }


        // Data mahasiswa dan email
        Box(
            modifier = Modifier
                .padding(16.dp)
                .background(MaterialTheme.colors.surface)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                if(profil!=null) {
                    NameAndEmail(profil,email)

                    ProfileProperty(stringResource(R.string.nama_mahasiswa), profil.name)
                    ProfileProperty(stringResource(R.string.nim_mahasiswa), profil.nim)
                    ProfileProperty(stringResource(R.string.prodi_mahasiswa), profil.prodi)
                    ProfileProperty(stringResource(R.string.ipk_mahasiswa), (profil.ipk).toString())
                    ProfileProperty(
                        stringResource(R.string.prov_mahasiswa),
                        provinsi?.namaProvinsi ?: "Not Available"
                    )
                }else{
                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        Text(
                            text = "ADMIN",
                            modifier = modifier,
                            style = androidx.compose.material3.MaterialTheme.typography.headlineSmall
                        )
                        Email(
                            email,
                            modifier = Modifier
                                .padding(bottom = 20.dp)
                                .baselineHeight(24.dp)
                        )
                    }
            }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        if (isRefreshing) {
            // TODO show a progress indicator or similar
        }
    }
}

@Composable
fun ProfileProperty(label: String, value: String, isLink: Boolean = false) {
    Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)) {
        Divider()
        androidx.compose.material3.Text(
            text = label,
            modifier = Modifier.baselineHeight(24.dp),
            style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
            color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
        )
        val style = if (isLink) {
            androidx.compose.material3.MaterialTheme.typography.bodyLarge.copy(color = androidx.compose.material3.MaterialTheme.colorScheme.primary)
        } else {
            androidx.compose.material3.MaterialTheme.typography.bodyLarge
        }
        androidx.compose.material3.Text(
            text = value.takeIf { it.isNotBlank() } ?: "Not Available",
            modifier = Modifier.baselineHeight(24.dp),
            style = style
        )
    }
}
@Composable
private fun NameAndEmail(
    userData: Mahasiswa,
    email:String,
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Name(
            userData,
            modifier = Modifier.baselineHeight(32.dp)
        )
        Email(
            email,
            modifier = Modifier
                .padding(bottom = 20.dp)
                .baselineHeight(24.dp)
        )
    }
}

@Composable
private fun Name(userData: Mahasiswa, modifier: Modifier = Modifier) {
    Text(
        text = userData.name,
        modifier = modifier,
        style = androidx.compose.material3.MaterialTheme.typography.headlineSmall
    )
}

@Composable
private fun Email(email: String, modifier: Modifier = Modifier) {
    Text(
        text = email,
        modifier = modifier,
        style = androidx.compose.material3.MaterialTheme.typography.bodyLarge,
        color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
    )
}
@Preview
@Composable
fun ProfileContentPreview() {
    val fakeMahasiswa = Mahasiswa(
        name = "John Doe",
        nim = "123456789",
        prodi = "Computer Science",
        ipk = 3.4f,
        provinsi = 2,
    )

    SisipanTheme {
        ProfileContent(
            openDrawer = {},
            isRefreshing = false,
            modifier = Modifier.fillMaxSize(),
            profil = fakeMahasiswa,
            email = "john.doe@example.com",
            onAccount = {},
            onEditProfilePhoto = {},
            provinsi = Provinsi(1,"12","jkk"),
            navigateBack ={},
        )
    }
}
