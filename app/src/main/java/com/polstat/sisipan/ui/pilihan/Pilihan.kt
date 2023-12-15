package com.polstat.sisipan.ui.pilihan

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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ContentAlpha
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.polstat.sisipan.R
import com.polstat.sisipan.data.Pilihan
import com.polstat.sisipan.data.PilihanNested
import com.polstat.sisipan.ui.theme.MinContrastOfPrimaryVsSurface
import com.polstat.sisipan.util.DynamicThemePrimaryColorsFromImage
import com.polstat.sisipan.util.contrastAgainst
import com.polstat.sisipan.util.rememberDominantColorState
import com.polstat.sisipan.util.verticalGradientScrim

@Composable
fun Pilihan(
    openDrawer: () -> Unit,
    viewModel: PilihanViewModel = viewModel(),
    onAccount: () -> Unit,
) {
    val viewState by viewModel.state.collectAsStateWithLifecycle()
    Surface(Modifier.fillMaxSize()) {
        PilihanContent(
            openDrawer,
            pilihanList = viewState.pilihanList,
            onPilihanClick = { /* Handle pilihan item click */ },
            isRefreshing = viewState.refreshing,
            modifier = Modifier.fillMaxSize(),
            onAccount,
            doRefresh = { viewModel.refresh(force = true) },
            )
    }
}

@Composable
fun PilihanAppBar(
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


@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
@Composable
fun PilihanContent(
    openDrawer: () -> Unit,
    pilihanList: List<PilihanNested>,
    onPilihanClick: (PilihanNested) -> Unit,
    isRefreshing: Boolean,
    modifier: Modifier = Modifier,
    onAccount: ()-> Unit,
    doRefresh: ()-> Unit,
) {
    val state = rememberPullRefreshState(isRefreshing, doRefresh)

    Box(
        modifier = modifier.windowInsetsPadding(
            WindowInsets.systemBars.only(WindowInsetsSides.Horizontal)
        )
            .pullRefresh(state)
            .verticalScroll(rememberScrollState())
    ) {
        // We dynamically theme this sub-section of the layout to match the selected
        // 'top podcast'

        val surfaceColor = MaterialTheme.colors.surface
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

                PilihanAppBar(
                    openDrawer,
                    backgroundColor = appBarColor,
                    modifier = Modifier.fillMaxWidth(),
                    onAccount,
                )

            }
        }
        Box(modifier = Modifier){
            Text(text = "INI DAFTAR PILIHAN")
        }
        LazyColumn {
            items(pilihanList) { pilihan ->
                PilihanCard(pilihan = pilihan, onItemClick = onPilihanClick)
            }
        }

        if (isRefreshing) {
            // TODO show a progress indicator or similar
        }
    }
}

@Composable
fun PilihanCard(pilihan: PilihanNested, onItemClick: (PilihanNested) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemClick(pilihan) }
    ) {
        // Isi card dengan inpilihan pilihan
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(text = "", style = MaterialTheme.typography.h6)
            // Tambahkan inpilihan lainnya sesuai kebutuhan
        }
    }
}
