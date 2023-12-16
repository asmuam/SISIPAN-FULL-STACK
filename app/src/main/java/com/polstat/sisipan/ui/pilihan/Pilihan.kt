package com.polstat.sisipan.ui.pilihan

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
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
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.polstat.sisipan.R
import com.polstat.sisipan.data.Formasi
import com.polstat.sisipan.data.Mahasiswa
import com.polstat.sisipan.data.PilihanNested
import com.polstat.sisipan.ui.theme.MinContrastOfPrimaryVsSurface
import com.polstat.sisipan.util.DynamicThemePrimaryColorsFromImage
import com.polstat.sisipan.util.contrastAgainst
import com.polstat.sisipan.util.rememberDominantColorState
import com.polstat.sisipan.util.verticalGradientScrim
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

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
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = stringResource(R.string.app_name),
                    modifier = Modifier
                        .padding(start = 4.dp)
                        .heightIn(max = 24.dp)
                        .align(Alignment.CenterVertically)
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
        modifier = modifier
            .windowInsetsPadding(
                WindowInsets.systemBars.only(WindowInsetsSides.Horizontal)
            )
            .windowInsetsPadding(
                WindowInsets.navigationBars.only(WindowInsetsSides.Bottom)
            )
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
                LazyColumn (
                    modifier = Modifier
                        .pullRefresh(state)
                ){
                    items(pilihanList) { pilihan ->
                        PilihanCard(pilihan = pilihan, onItemClick = onPilihanClick)
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

@Composable
fun PilihanCard(pilihan: PilihanNested, onItemClick: (PilihanNested) -> Unit) {
    val mahasiswaState by pilihan.mahasiswa.collectAsState(
        initial = Mahasiswa(
            0,
            "",
            "",
            "",
            0,
            0f
        )
    ) // Default Mahasiswa

    // Menggunakan Safe collectAsState untuk Formasi
    val pilihan1State by rememberUpdatedState(
        newValue = pilihan.pilihan1.collectAsState(
            initial = Formasi(
                0,
                0,
                "",
                "",
                0,
                0,
                0
            )
        ).value
    )
    val pilihan2State by rememberUpdatedState(
        newValue = pilihan.pilihan2.collectAsState(
            initial = Formasi(
                0,
                0,
                "",
                "",
                0,
                0,
                0
            )
        ).value
    )
    val pilihan3State by rememberUpdatedState(
        newValue = pilihan.pilihan3.collectAsState(
            initial = Formasi(
                0,
                0,
                "",
                "",
                0,
                0,
                0
            )
        ).value
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemClick(pilihan) }
            .padding(8.dp)
            .border(
                1.dp,
                MaterialTheme.colors.onSurface.copy(alpha = 0.1f),
                shape = androidx.compose.material3.MaterialTheme.shapes.large
            )
            .clip(MaterialTheme.shapes.large),
        colors = CardDefaults.cardColors(MaterialTheme.colors.surface),// Customize the corner radius
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
        ) {
            // Bagian Kiri (Nama, Nim, Prodi)
            Column(
                modifier = Modifier
                    .weight(1f)
            ) {
                Text(
                    text = mahasiswaState.name,
                    style = MaterialTheme.typography.h6,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colors.onSurface
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Tambahkan informasi lainnya seperti Nim dan Prodi
                Text(
                    text = "NIM: ${mahasiswaState.nim}",
                    style = MaterialTheme.typography.body2,
                    color = Color.Gray
                )

                Text(
                    text = "Prodi: ${mahasiswaState.prodi}",
                    style = MaterialTheme.typography.body2,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Bagian Kanan (Pilihan 1, Pilihan 2, Pilihan 3)
            Column(
                modifier = Modifier
            ) {
                Spacer(
                    modifier = Modifier.height(
                        4.dp
                    )
                )

                // Pilihan 1
                PilihanPriorityItem("Pilihan 1", pilihan1State)

                // Pilihan 2
                PilihanPriorityItem("Pilihan 2", pilihan2State)

                // Pilihan 3
                PilihanPriorityItem("Pilihan 3", pilihan3State)
            }
        }
    }
}
    @Composable
    fun PilihanPriorityItem(label: String, value: Formasi) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.caption,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value.namaSatuanKerja,
                style = MaterialTheme.typography.body2,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colors.onSurface
            )
        }
    }


@Preview
@Composable
fun PilihanPreview() {
    val dummy = createDummyPilihanList()
    val dummyPilihanList = remember {dummy}

    Surface(Modifier.fillMaxSize()) {
        PilihanContent(
            openDrawer = {},
            pilihanList = dummyPilihanList,
            onPilihanClick = {},
            isRefreshing = false,
            onAccount = {},
            doRefresh = {}
        )
    }
}

@Composable
private fun createDummyPilihanList(): List<PilihanNested> {
    return listOf(
        PilihanNested(
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
        ),
        PilihanNested(
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
        ),
        // Add more dummy data as needed
    )
}

@Composable
private fun createDummyMahasiswa(): Flow<Mahasiswa> {
    // Create a dummy Mahasiswa Flow
    return flow {
        emit(
            Mahasiswa(
                nim = "123456",
                name = "John Doe",
                prodi = "Computer Science",
                provinsiId = 1,
                ipk = 3.9f
            )

        )
    }
}

@Composable
private fun createDummyFormasi(): Flow<Formasi> {
    // Create a dummy Formasi Flow
    return flow {
        emit(
            Formasi(
                provinsi = 1,
                kodeSatker = "123ABC",
                namaSatuanKerja = "Dummy Satuan Kerja",
                kuotaSt = 10,
                kuotaKs = 15,
                kuotaD3 = 5
            )
        )
    }
}
