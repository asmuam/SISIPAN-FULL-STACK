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

@file:OptIn(ExperimentalMaterialApi::class)

package com.polstat.sisipan.ui.home

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardDefaults.cardElevation
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.polstat.sisipan.Graph
import com.polstat.sisipan.R
import com.polstat.sisipan.data.Formasi
import com.polstat.sisipan.data.PilihanNested
import com.polstat.sisipan.data.UserRepository
import com.polstat.sisipan.ui.pilihan.createDummyFormasi
import com.polstat.sisipan.ui.pilihan.createDummyMahasiswa
import com.polstat.sisipan.ui.theme.MinContrastOfPrimaryVsSurface
import com.polstat.sisipan.util.DynamicThemePrimaryColorsFromImage
import com.polstat.sisipan.util.contrastAgainst
import com.polstat.sisipan.util.rememberDominantColorState
import com.polstat.sisipan.util.verticalGradientScrim
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


@Composable
fun Home(
    openDrawer: () -> Unit,
    viewModel: HomeViewModel = viewModel(),
    onAccount: () -> Unit,
    onAddPilihan: () -> Unit,
    onEditPilihan: (Long) -> Unit,
) {
    val viewState by viewModel.state.collectAsStateWithLifecycle()
    Log.i(
        "AccessHome",
        "ExpiresIn: ${UserRepository.expiresIn} time now: ${System.currentTimeMillis()}"
    )
    Log.i(
        "AccessHome",
        "idmhs: ${UserRepository.idMhs} time now: ${System.currentTimeMillis()}"
    )
    Log.i(
        "AccessHome",
        "id: ${UserRepository.id} time now: ${System.currentTimeMillis()}"
    )
    Log.i(
        "AccessHome",
        "role: ${UserRepository.role} time now: ${System.currentTimeMillis()}"
    )
    LaunchedEffect(viewState){
        viewModel.refresh(true)
    }
    Surface(Modifier.fillMaxSize()) {
        HomeContent(
            openDrawer,
            isRefreshing = viewState.refreshing,
            modifier = Modifier.fillMaxSize(),
            onAccount,
            viewState = viewState,
            doRefresh = { viewModel.refresh(force = true) },
            doPenempatan = {viewModel.penempatan() },
            onAddPilihan = { onAddPilihan() },
            onEditPilihan = { onEditPilihan(it.id) },
            memilih = viewState.memilih,
            pilihanSaya = viewState.pilihanSaya,
        )
    }
}

@Composable
fun HomeAppBar(
    openDrawer: () -> Unit,
    backgroundColor: Color,
    modifier: Modifier = Modifier,
    onAccount: () -> Unit,
) {
    TopAppBar(
        title = {
            Row {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = stringResource(R.string.app_name),
                    modifier = Modifier
                        .padding(start = 4.dp)
                        .heightIn(max = 24.dp)
                        .align(Alignment.CenterVertically)
                        .clickable { openDrawer() }
                )
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
                IconButton(
                    onClick = { onAccount() }
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = stringResource(R.string.cd_account)
                    )
                }
            }
        },
        modifier = modifier
    )
}


@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HomeContent(
    openDrawer: () -> Unit,
    isRefreshing: Boolean,
    modifier: Modifier = Modifier,
    onAccount: () -> Unit,
    viewState: HomeViewState,
    doRefresh: () -> Unit,
    doPenempatan: () -> Unit,
    onAddPilihan: () -> Unit,
    onEditPilihan: (PilihanNested) -> Unit,
    memilih: Boolean,
    pilihanSaya: PilihanNested?,
) {
    val state = rememberPullRefreshState(isRefreshing, doRefresh)

    Box(
        modifier = Modifier
    ) {
        Column(
            modifier = modifier
                .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Horizontal))
        ) {
            // We dynamically theme this sub-section of the layout to match the selected
            // 'top podcast'

            val surfaceColor = MaterialTheme.colorScheme.surface
            val appBarColor = surfaceColor.copy(alpha = 0.87f)
            val dominantColorState = rememberDominantColorState { color ->
                // We want a color which has sufficient contrast against the surface color
                color.contrastAgainst(surfaceColor) >= MinContrastOfPrimaryVsSurface
            }

            DynamicThemePrimaryColorsFromImage(dominantColorState) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalGradientScrim(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.38f),
                            startYPercentage = 1f,
                            endYPercentage = 0f
                        )
                ) {
                    // Draw a scrim over the status bar which matches the app bar
                    Spacer(
                        Modifier
                            .background(appBarColor)
                            .fillMaxWidth()
                            .windowInsetsTopHeight(WindowInsets.statusBars)
                    )

                    HomeAppBar(
                        openDrawer,
                        backgroundColor = appBarColor,
                        modifier = Modifier.fillMaxWidth(),
                        onAccount,
                    )
                }
            }
            when (viewState.role) {
                "ADMIN" -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background)
                            .padding(16.dp)
                            .border(2.dp, Color.Gray, shape = RoundedCornerShape(16.dp))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                                .pullRefresh(state)
                                .verticalScroll(rememberScrollState())
                        ) {
                            // Header with timestamp
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "INI HOME UNTUK ADMIN",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                            }

                            // Content
                            Text(
                                text = "Mahasiswa yang telah memilih : ${viewState.mahasiswaMemilih}/${viewState.jmlhMhs}",
                                fontSize = 18.sp,
                                color = Color.Black
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Additional creative content
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(120.dp)
                                    .padding(16.dp)
                                    .background(MaterialTheme.colorScheme.background),
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(MaterialTheme.colorScheme.primary),
                                elevation = cardElevation(4.dp),
                                // Tentukan apakah card dapat diklik berdasarkan kondisi
                                enabled = (viewState.jmlhMhs == viewState.mahasiswaMemilih),
                                // Handle klik pada card hanya jika card dapat diklik
                                onClick = {
                                    if (viewState.jmlhMhs == viewState.mahasiswaMemilih) {
                                        doPenempatan()
                                    }
                                }
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.ic_jetnews),
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(40.dp)
                                    )
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Text(
                                        text = "Lakukan Penempatan",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }

                            Text(
                                text = "Last Updated: ${getCurrentTimestamp()}",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                        PullRefreshIndicator(
                            refreshing = isRefreshing,
                            state = state,
                            modifier = Modifier.align(Alignment.TopCenter)
                        )
                    }
                }

                "MAHASISWA" -> {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                                .pullRefresh(state = state)
                                .verticalScroll(rememberScrollState())
                        ) {
                            Text(
                                text = "INI HOME UNTUK MAHASISWA",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            if (memilih && pilihanSaya!=null) {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(60.dp)
                                        .clickable { onEditPilihan(pilihanSaya) },
                                    shape = RoundedCornerShape(8.dp), // Customize the corner radius
                                    elevation = cardElevation(4.dp), // Add elevation for a shadow effect
                                    colors = CardDefaults.cardColors(MaterialTheme.colorScheme.primary)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // You can customize the icon and text based on your design
                                        Icon(
                                            painter = painterResource(R.drawable.ic_jetnews),
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(40.dp)
                                        )
                                        Spacer(modifier = Modifier.width(16.dp))
                                        Text(
                                            text = "Ubah Pilihan",
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "PILIHAN SAAT INI:",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Card {
                                    Column {
                                        // Menggunakan Safe collectAsState untuk Formasi
                                        if (pilihanSaya != null) {
                                            val pilihan1State by rememberUpdatedState(
                                                newValue = pilihanSaya.pilihan1.collectAsState(
                                                    initial = Formasi(0, 0, "", "", 0, 0, 0)
                                                ).value
                                            )
                                            val pilihan2State by rememberUpdatedState(
                                                newValue = pilihanSaya.pilihan2.collectAsState(
                                                    initial = Formasi(0, 0, "", "", 0, 0, 0)
                                                ).value
                                            )
                                            val pilihan3State by rememberUpdatedState(
                                                newValue = pilihanSaya.pilihan3.collectAsState(
                                                    initial = Formasi(0, 0, "", "", 0, 0, 0)
                                                ).value
                                            )

                                            PreviewPilihanState(pilihanState = pilihan1State)
                                            PreviewPilihanState(pilihanState = pilihan2State)
                                            PreviewPilihanState(pilihanState = pilihan3State)
                                        }
                                    }
                                }
                            } else {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(60.dp)
                                        .clickable { onAddPilihan() },
                                    shape = RoundedCornerShape(8.dp), // Customize the corner radius
                                    elevation = cardElevation(4.dp), // Add elevation for a shadow effect
                                    colors = CardDefaults.cardColors(MaterialTheme.colorScheme.primary)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // You can customize the icon and text based on your design
                                        Icon(
                                            painter = painterResource(R.drawable.ic_jetnews),
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(40.dp)
                                        )
                                        Spacer(modifier = Modifier.width(16.dp))
                                        Text(
                                            text = "MEMILIH SEKARANG",
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                }

                            }

                        }
                        PullRefreshIndicator(
                            refreshing = isRefreshing,
                            state = state,
                            modifier = Modifier.align(Alignment.TopCenter)
                        )
                    }

                }
            }
        }
    }
}
@Composable
fun PreviewPilihanState(pilihanState: Formasi) {
    Text(
        text = pilihanState.namaSatuanKerja,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = Color.Black,
        modifier = Modifier.padding(4.dp)
    )
    Divider(color = Color.White, thickness = 2.dp)
}
@Composable
fun getCurrentTimestamp(): String {
    val currentDateTime = LocalDateTime.now()
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    return currentDateTime.format(formatter)
}

@Composable
fun HomeAdminPreview() {
    MaterialTheme {
        Surface(Modifier.fillMaxSize()) {
            HomeContent(
                openDrawer = {},
                isRefreshing = false,
                modifier = Modifier.fillMaxSize(),
                onAccount = {},
                viewState = HomeViewState(role = "ADMIN", mahasiswaMemilih = 2, jmlhMhs = 40),
                doRefresh = {},
                doPenempatan = { },
                onAddPilihan = {},
                onEditPilihan = {},
                memilih = false,
                pilihanSaya = null
            )
        }
    }
}

@Preview(name = "Home Preview - ADMIN")
@Composable
fun HomeAdminPreviewPreview() {
    MaterialTheme {
        HomeAdminPreview()
    }
}

@Composable
fun HomeMahasiswaPreview(memilih: Boolean, pilihanSaya:PilihanNested?) {
    MaterialTheme {
        Surface(Modifier.fillMaxSize()) {
            HomeContent(
                openDrawer = {},
                isRefreshing = false,
                modifier = Modifier.fillMaxSize(),
                onAccount = {},
                viewState = HomeViewState(role = "MAHASISWA", mahasiswaMemilih = 21, jmlhMhs = 44),
                doRefresh = {},
                doPenempatan = { },
                onAddPilihan = {},
                onEditPilihan = {},
                memilih = memilih,
                pilihanSaya = pilihanSaya
            )
        }
    }
}

@Preview(name = "Belum memilih", group = "Mahasiswa")
@Composable
fun HomeMahasiswaPreviewPreview() {
    MaterialTheme {
        HomeMahasiswaPreview(false,null)
    }
}

@Preview(name = "Sudah memilih", group = "Mahasiswa")
@Composable
fun HomeMahasiswaDarkPreview() {
    val pilihanSaya = PilihanNested(
        id = 1,
        mahasiswa = createDummyMahasiswa(),
        pilihan1 = createDummyFormasi(),
        pilihan2 = createDummyFormasi(),
        pilihan3 = createDummyFormasi(),
        pilihanSistem = createDummyFormasi(),
        indeksPilihan1 = 3.5f,
        indeksPilihan2 = 4.0f,
        indeksPilihan3 = 3.8f,
        ipk = 3.9f,
        hasil = "Accepted"
    )
    MaterialTheme {
        HomeMahasiswaPreview(true,pilihanSaya)
    }
}

