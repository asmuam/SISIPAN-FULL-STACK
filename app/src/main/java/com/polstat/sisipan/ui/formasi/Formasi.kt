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

import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsEndWidth
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.polstat.sisipan.R
import com.polstat.sisipan.data.Formasi
import com.polstat.sisipan.ui.formasiDummy
import com.polstat.sisipan.ui.signinsignup.ErrorSnackbar
import com.polstat.sisipan.ui.theme.MinContrastOfPrimaryVsSurface
import com.polstat.sisipan.util.DynamicThemePrimaryColorsFromImage
import com.polstat.sisipan.util.contrastAgainst
import com.polstat.sisipan.util.rememberDominantColorState
import com.polstat.sisipan.util.verticalGradientScrim

@Preview
@Composable
fun FormasiPrev() {
    // Provide dummy data for the preview

    FormasiContent(
        openDrawer = {},
        formasiBukaList = formasiDummy.subList(1, 5),
        formasiTutupList = emptyList<Formasi>(),
        onFormasiClick = { /* Handle formasi item click */ },
        isRefreshing = false, // Set to true if you want to preview the refreshing state
        modifier = Modifier.fillMaxSize(),
        onAccount = {},
        role = "ADMIN", // Provide a dummy role for the preview
        navigateToAddFormasi = {},
        doRefresh = {  },
        )
}

@Composable
fun Formasi(
    openDrawer: () -> Unit,
    viewModel: FormasiViewModel = viewModel(),
    onAccount: () -> Unit,
    navigateToAddFormasi: () -> Unit,
) {
    val viewState by viewModel.state.collectAsStateWithLifecycle()

    Surface(Modifier.fillMaxSize()) {
        FormasiContent(
            openDrawer,
            formasiBukaList = viewState.formasiBukaList,
            formasiTutupList = viewState.formasiTutupList,
            onFormasiClick = { /* Handle formasi item click */ },
            isRefreshing = viewState.refreshing,
            modifier = Modifier.fillMaxSize(),
            onAccount,
            role = viewState.role,
            navigateToAddFormasi = navigateToAddFormasi,
            doRefresh = { viewModel.refresh(force = true) },
        )
    }
}

@Composable
fun FormasiAppBar(
    openDrawer: () -> Unit,
    backgroundColor: Color,
    modifier: Modifier = Modifier,
    onAccount: () -> Unit,
) {
    TopAppBar(
        title = {
            Row {
                Image(
                    painter = painterResource(R.drawable.ic_sisipan_logo),
                    contentDescription = null,
                    modifier = Modifier.clickable { openDrawer() }
                )
                Icon(
                    painter = painterResource(R.drawable.ic_text_logo),
                    contentDescription = stringResource(R.string.app_name),
                    modifier = Modifier
                        .padding(start = 4.dp)
                        .heightIn(max = 24.dp)
                )
            }
        },
        backgroundColor = backgroundColor,
        actions = {
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                IconButton(
                    onClick = { /* TODO: Open search */ }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = stringResource(R.string.cd_search)
                    )
                }
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

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun FormasiContent(
    openDrawer: () -> Unit,
    formasiBukaList: List<Formasi>,
    formasiTutupList: List<Formasi>,
    onFormasiClick: (Formasi) -> Unit,
    isRefreshing: Boolean,
    modifier: Modifier = Modifier,
    onAccount: () -> Unit,
    role: String,
    navigateToAddFormasi: () -> Unit,
    doRefresh: () -> Unit,
) {
    val state = rememberPullRefreshState(isRefreshing, doRefresh)
    val surfaceColor = MaterialTheme.colors.surface
    val appBarColor = surfaceColor.copy(alpha = 0.87f)
    val dominantColorState = rememberDominantColorState { color ->
        // We want a color which has sufficient contrast against the surface color
        color.contrastAgainst(surfaceColor) >= MinContrastOfPrimaryVsSurface
    }
    Box(
        modifier = modifier
            .windowInsetsPadding(
                WindowInsets.systemBars.only(WindowInsetsSides.Horizontal)
            )
            .windowInsetsPadding(
                WindowInsets.navigationBars.only(WindowInsetsSides.Bottom)
            )
            .pullRefresh(state = state)
            .verticalScroll(rememberScrollState())
    )
    {
        Scaffold(
            topBar = {
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
                            .background(appBarColor)
                            .fillMaxWidth()
                            .windowInsetsTopHeight(WindowInsets.statusBars)
                    )

                    FormasiAppBar(
                        openDrawer,
                        backgroundColor = appBarColor,
                        modifier = Modifier.fillMaxWidth(),
                        onAccount,
                    )
                }
            },
            floatingActionButton = {
                if (role.equals("ADMIN", ignoreCase = true)) {
                    FloatingActionButton(
                        onClick = {
                            navigateToAddFormasi()
                        },
                        modifier = Modifier
                            .padding(16.dp)
                            .animateContentSize()
                            .background(Color.Transparent)
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Add Formasi")
                    }
                }
            },
            content = { innerPadding ->
                Column(
                    modifier = modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                        .verticalGradientScrim(
                            color = MaterialTheme.colors.primary.copy(alpha = 0.38f),
                            startYPercentage = 1f,
                            endYPercentage = 0f
                        )
                ) {
                    // Konten utama dengan fungsi DynamicThemePrimaryColorsFromImage
                    DynamicThemePrimaryColorsFromImage(dominantColorState) {
                        // Konten lainnya seperti LazyColumn dan lainnya
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(formasiBukaList) { formasi ->
                                FormasiCard(
                                    formasi = formasi,
                                    onItemClick = onFormasiClick,
                                )
                            }

                            // Divider untuk memberikan garis pemisah
                            item {
                                Divider(
                                    color = Color.Black, // Warna garis pemisah
                                    thickness = 3.dp, // Ketebalan garis pemisah
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp) // Sesuaikan padding garis pemisah sesuai kebutuhan
                                )
                            }

                            items(formasiTutupList) { formasi ->
                                FormasiCard(
                                    formasi = formasi,
                                    onItemClick = onFormasiClick,
                                )
                            }
                        }
                    }
                }
            }
        )
    }

}


@Composable
fun FormasiCard(
    formasi: Formasi,
    onItemClick: (Formasi) -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemClick(formasi) }
    ) {
        // Isi card dengan informasi formasi
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(text = formasi.namaSatuanKerja, style = MaterialTheme.typography.h6)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("Kuota St: ")
                    }
                    append("${formasi.kuotaSt}")
                    append("   ")

                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("Kuota Ks: ")
                    }
                    append("${formasi.kuotaKs}")
                    append("   ")

                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("Kuota D3: ")
                    }
                    append("${formasi.kuotaD3}")
                },
                style = MaterialTheme.typography.body2,
                color = MaterialTheme.colors.onSurface
            )
            // Tambahkan informasi lainnya sesuai kebutuhan
        }
    }
}