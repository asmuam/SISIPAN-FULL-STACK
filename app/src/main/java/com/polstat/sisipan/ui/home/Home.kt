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
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ContentAlpha
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.polstat.sisipan.R
import com.polstat.sisipan.data.UserRepository
import com.polstat.sisipan.ui.theme.MinContrastOfPrimaryVsSurface
import com.polstat.sisipan.util.DynamicThemePrimaryColorsFromImage
import com.polstat.sisipan.util.contrastAgainst
import com.polstat.sisipan.util.rememberDominantColorState
import com.polstat.sisipan.util.verticalGradientScrim

@Composable
fun Home(
    openDrawer: () -> Unit,
    viewModel: HomeViewModel = viewModel(),
    onAccount: ()-> Unit,
) {
    val viewState by viewModel.state.collectAsStateWithLifecycle()
    Surface(Modifier.fillMaxSize()) {
        Log.i("AccessHome", "ExpiresIn: ${UserRepository.expiresIn} time now: ${System.currentTimeMillis()}")

        HomeContent(
            openDrawer,
            isRefreshing = viewState.refreshing,
            modifier = Modifier.fillMaxSize(),
            onAccount,
            viewState = viewState,
            doRefresh = { viewModel.refresh(force = true) },
        )
    }
}

@Composable
fun HomeAppBar(
    openDrawer: () -> Unit,
    backgroundColor: Color,
    modifier: Modifier = Modifier,
    onAccount: ()-> Unit,
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



@Composable
fun HomeContent(
    openDrawer: () -> Unit,
    isRefreshing: Boolean,
    modifier: Modifier = Modifier,
    onAccount: ()-> Unit,
    viewState: HomeViewState,
    doRefresh: ()-> Unit,
) {
    val state = rememberPullRefreshState(isRefreshing, doRefresh)
    Box(
        modifier = Modifier
            .pullRefresh(state)
            .verticalScroll(rememberScrollState())
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
                    Box(modifier = Modifier) {
                        Column {
                            Text(text = "INI HOME UNTUK ADMIN")
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "Mahasiswa yang telah memilih : ${viewState.mahasiswaMemilih}")
                        }

                    }
                }

                "MAHASISWA" -> {
                    Box(modifier = Modifier) {
                        Column {
                            Text(text = "INI HOME UNTUK MAHASISWA")
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "MEMILIH SEKARANG",
                                modifier = Modifier.clickable { /* Handle click event */ }
                            )
                        }

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

