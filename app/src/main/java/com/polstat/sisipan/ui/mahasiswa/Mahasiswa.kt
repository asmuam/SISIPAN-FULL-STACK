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

package com.polstat.sisipan.ui.mahasiswa

import android.util.Log
import androidx.compose.animation.animateContentSize
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
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ContentAlpha
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.polstat.sisipan.R
import com.polstat.sisipan.data.Mahasiswa
import com.polstat.sisipan.data.Provinsi
import com.polstat.sisipan.data.UserRepository.role
import com.polstat.sisipan.ui.createDummyMhs
import com.polstat.sisipan.ui.theme.MinContrastOfPrimaryVsSurface
import com.polstat.sisipan.ui.theme.SisipanTheme
import com.polstat.sisipan.util.DynamicThemePrimaryColorsFromImage
import com.polstat.sisipan.util.baselineHeight
import com.polstat.sisipan.util.contrastAgainst
import com.polstat.sisipan.util.rememberDominantColorState
import com.polstat.sisipan.util.verticalGradientScrim
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

@Composable
fun Mahasiswa(
    openDrawer: () -> Unit,
    viewModel: MahasiswaViewModel = viewModel(),
    onAccount: ()-> Unit,
    navigateToAddMahasiswa: () -> Unit,
    ) {
    val viewState by viewModel.state.collectAsStateWithLifecycle()

    Surface(Modifier.fillMaxSize()) {
        MahasiswaContent(
            openDrawer,
            isRefreshing = viewState.refreshing,
            modifier = Modifier.fillMaxSize(),
            onAccount,
            doRefresh = { viewModel.refresh(force = true) },
            mahasiswaList = viewState.mahasiswa,
            navigateToAddMahasiswa =navigateToAddMahasiswa,
            )
    }
}

@Composable
fun MahasiswaAppBar(
    openDrawer: () -> Unit,
    backgroundColor: Color,
    modifier: Modifier = Modifier,
    onAccount: ()-> Unit,
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
                    onClick = { /* TODO: Open search */ }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = stringResource(R.string.cd_search)
                    )
                }
                IconButton(
                    onClick = { onAccount }
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


@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
@Composable
fun MahasiswaContent(
    openDrawer: () -> Unit,
    isRefreshing: Boolean,
    modifier: Modifier = Modifier,
    onAccount: () -> Unit,
    doRefresh: () -> Unit,
    mahasiswaList:List<MahasiswaCollection>,
    navigateToAddMahasiswa: () -> Unit,
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
    ) {
        Scaffold (
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
                            .background(MaterialTheme.colors.surface.copy(alpha = 0.87f))
                            .fillMaxWidth()
                            .windowInsetsTopHeight(WindowInsets.statusBars)
                    )

                    // AppBar dengan tombol navigasi dan ikon pengaturan
                    MahasiswaAppBar(
                        openDrawer,
                        backgroundColor = MaterialTheme.colors.surface.copy(alpha = 0.87f),
                        modifier = Modifier.fillMaxWidth(),
                        onAccount,
                    )
                }
            },
            floatingActionButton = {
                if (role.equals("ADMIN", ignoreCase = true)) {
                FloatingActionButton(
                    onClick = {
                        navigateToAddMahasiswa()
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
                // Scrim dan AppBar
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
                    DynamicThemePrimaryColorsFromImage(dominantColorState) {
                        // LazyColumn dengan MahasiswaCard
                        LazyColumn(
                            modifier = Modifier
                                .pullRefresh(state = state)
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            items(mahasiswaList) { mahasiswa ->
                                MahasiswaCard(mahasiswa)
                            }
                        }
                    }
                }
            }
        )
        // PullRefreshIndicator
        PullRefreshIndicator(
            refreshing = isRefreshing,
            state = state,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}

@Composable
fun MahasiswaCard(userData: MahasiswaCollection) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
    ) {
        Column {
            NameAndNIM(userData)
            MahasiswaProperty("Program Studi", userData.prodi)
            MahasiswaProperty("Asal Daerah", userData.provinsi.namaProvinsi)
            MahasiswaProperty("IPK", userData.ipk.toString())
        }
    }
}
@Composable
fun MahasiswaProperty(label: String, value: Any, isLink: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp, 2.dp)
    ) {
        // Label
        Text(
            text = "$label:",
            style = MaterialTheme.typography.body1,
            color = MaterialTheme.colors.onSurface,
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically)
        )

        // Value
        when (value) {
            is String -> {
                Text(
                    text = value.takeIf { it.isNotBlank() } ?: "Not Available",
                    style = MaterialTheme.typography.body2,
                    modifier = Modifier
                        .weight(1f)
                        .align(Alignment.CenterVertically)
                )
            }
            is Flow<*> -> {
                val flowValue by value.collectAsState(initial = null)
                when (flowValue) {
                    is Provinsi -> {
                        Text(
                            text = (flowValue as Provinsi).namaProvinsi,
                            style = MaterialTheme.typography.body2,
                            modifier = Modifier
                                .weight(1f)
                                .align(Alignment.CenterVertically)
                        )
                    }
                    else -> {
                        Text(
                            text = "Not Available",
                            style = MaterialTheme.typography.body2,
                            modifier = Modifier
                                .weight(1f)
                                .align(Alignment.CenterVertically)
                        )
                    }
                }
            }
            else -> {
                Text(
                    text = "Not Available",
                    style = MaterialTheme.typography.body2,
                    modifier = Modifier
                        .weight(1f)
                        .align(Alignment.CenterVertically)
                )
            }
        }
    }
}


@Composable
private fun NameAndNIM(
    userData: MahasiswaCollection,
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Name(userData)
        Divider(
            color = Color.Black,
            thickness = 3.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 1.dp)
        )
        NIM(userData, modifier = Modifier.padding(bottom = 4.dp))
    }

}

@Composable
private fun Name(userData: MahasiswaCollection, modifier: Modifier = Modifier) {
    Text(
        text = userData.name,
        modifier = modifier,
        style = androidx.compose.material3.MaterialTheme.typography.headlineSmall
    )
}

@Composable
private fun NIM(userData: MahasiswaCollection, modifier: Modifier = Modifier) {
    Text(
        text = userData.nim,
        modifier = modifier,
        style = androidx.compose.material3.MaterialTheme.typography.bodyLarge,
        color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
    )
}
@Preview
@Composable
fun MahasiswaContentPreview() {
    val dummy = createDummyMhs()
    val dummyMhsList = remember {dummy}

    MahasiswaContent(
        openDrawer = {},
        isRefreshing = false,
        onAccount = {},
        doRefresh = {},
        mahasiswaList = dummyMhsList,
        navigateToAddMahasiswa = {},
        )
}