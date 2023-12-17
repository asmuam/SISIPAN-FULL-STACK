package com.polstat.sisipan.ui.pilihan

import android.util.Log
import androidx.compose.foundation.background
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
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.polstat.sisipan.R
import com.polstat.sisipan.api.PilihanRequest
import com.polstat.sisipan.data.Formasi
import com.polstat.sisipan.data.Mahasiswa
import com.polstat.sisipan.data.PilihanNested
import com.polstat.sisipan.ui.AppViewModelProvider
import com.polstat.sisipan.ui.formasiDummy
import com.polstat.sisipan.ui.theme.MinContrastOfPrimaryVsSurface
import com.polstat.sisipan.util.DynamicThemePrimaryColorsFromImage
import com.polstat.sisipan.util.contrastAgainst
import com.polstat.sisipan.util.rememberDominantColorState
import com.polstat.sisipan.util.verticalGradientScrim
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

@Composable
@Preview
fun previewEditPilihan(
) {
    Surface(Modifier.fillMaxSize()) {
        EditPilihanContent(
            openDrawer = {},
            isRefreshing = false,
            onAccount = {},
            role = "Admin",
            navigateBack = {},
            canNavigateBack = true,
            onNavigateUp = {},
            editPilihanViewState =
            EditPilihanViewState(
                role = "",
                pilihanSaya = PilihanNested(
                    id = 0L,
                    mahasiswa = flowOf(Mahasiswa(0,"","","",0,0F)), // Anda perlu memberikan nilai Mahasiswa yang sesuai
                    pilihan1 = flowOf(Formasi(0,0,"","abc",0,0,0)), // Anda perlu memberikan nilai Formasi yang sesuai
                    pilihan2 = flowOf(Formasi(0,0,"","def",0,0,0)), // Anda perlu memberikan nilai Formasi yang sesuai
                    pilihan3 = flowOf(Formasi(0,0,"","ghi",0,0,0)), // Anda perlu memberikan nilai Formasi yang sesuai
                    pilihanSistem = flowOf(Formasi(0,0,"","",0,0,0)), // Anda perlu memberikan nilai Formasi yang sesuai
                    indeksPilihan1 = 0.0f,
                    indeksPilihan2 = 0.0f,
                    indeksPilihan3 = 0.0f,
                    ipk = 0.0f,
                    hasil = null
                ),
                formasiDummy,
                pilihanUiState = PilihanUiState(),
                refreshing = false,
                errorMessage = null
            ),
            onPilihanValueChange = {},
            onSaveClick = {},
        )
    }
}

@Composable
fun EditPilihan(
    openDrawer: () -> Unit,
    onAccount: () -> Unit,
    navigateBack: () -> Unit,
    canNavigateBack: Boolean = true,
    onNavigateUp: () -> Unit,
    viewModel: EditPilihanViewModel = viewModel(factory = AppViewModelProvider.Factory),

    ) {
    val viewState by viewModel.state.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()
    Surface(Modifier.fillMaxSize()) {
        EditPilihanContent(
            openDrawer,
            isRefreshing = viewState.refreshing,
            modifier = Modifier.fillMaxSize(),
            onAccount,
            role = viewState.role,
            navigateBack = navigateBack,
            onNavigateUp = onNavigateUp,
            canNavigateBack = canNavigateBack,
            editPilihanViewState = viewState,
            onPilihanValueChange = {viewModel.updateUiState(it)},
            onSaveClick = {
                // Note: If the user rotates the screen very fast, the operation may get cancelled
                // and the book may not be saved in the Database. This is because when config
                // change occurs, the Activity will be recreated and the rememberCoroutineScope will
                // be cancelled - since the scope is bound to composition.
                coroutineScope.launch {
                    viewModel.savePilihan()
                    navigateBack()
                }
            },
        )
    }
}

@Composable
fun EditPilihanContent(
    openDrawer: () -> Unit,
    isRefreshing: Boolean,
    modifier: Modifier = Modifier,
    onAccount: () -> Unit,
    role: String,
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    canNavigateBack: Boolean,
    editPilihanViewState: EditPilihanViewState,
    onPilihanValueChange: (PilihanRequest) -> Unit,
    onSaveClick: () -> Unit,
) {
    val surfaceColor = MaterialTheme.colors.surface
    val appBarColor = surfaceColor.copy(alpha = 0.87f)
    val dominantColorState = rememberDominantColorState { color ->
        // We want a color which has sufficient contrast against the surface color
        color.contrastAgainst(surfaceColor) >= MinContrastOfPrimaryVsSurface
    }
    Column(
        modifier = modifier.windowInsetsPadding(
            WindowInsets.systemBars.only(WindowInsetsSides.Horizontal)
        )
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

                    EditPilihanAppBar(
                        openDrawer,
                        backgroundColor = appBarColor,
                        modifier = Modifier.fillMaxWidth(),
                        onAccount,
                        title = stringResource(R.string.edit_pilihan),
                        canNavigateBack = canNavigateBack,
                        navigateUp = onNavigateUp
                    )
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
                        Log.i("TAG", "pilihanSaya: ${editPilihanViewState.pilihanSaya}")
                        Log.i("TAG", "pilihanDetails: ${editPilihanViewState.pilihanUiState.pilihanDetails}")

                        EditPilihanInputForm(
                            pilihanSaya =editPilihanViewState.pilihanSaya,
                            formasiList = editPilihanViewState.formasiList,
                            pilihanDetails = editPilihanViewState.pilihanUiState.pilihanDetails,
                            onValueChange = onPilihanValueChange,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Button(
                            onClick = onSaveClick,
                            enabled = editPilihanViewState.pilihanUiState.isEntryValid,
                            shape = androidx.compose.material3.MaterialTheme.shapes.small,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = stringResource(R.string.save_action))
                        }
                    }
                }
            }
        )
    }

}

@Composable
fun EditPilihanInputForm(
    pilihanSaya: PilihanNested,
    formasiList: List<Formasi>,
    pilihanDetails: PilihanRequest,
    onValueChange: (PilihanRequest) -> Unit = {},
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
) {
    // State untuk menyimpan item yang dipilih
    var selectedFormasi1 by remember { mutableStateOf<Formasi?>(null) }
    var selectedFormasi2 by remember { mutableStateOf<Formasi?>(null) }
    var selectedFormasi3 by remember { mutableStateOf<Formasi?>(null) }

    // Mengambil nilai namaSatuanKerja dari pilihanSaya
    val namaSatuanKerjaPilihan1 = pilihanSaya.pilihan1.collectAsState(initial = Formasi(0,0,"","",0,0,0)).value.namaSatuanKerja
    val namaSatuanKerjaPilihan2 = pilihanSaya.pilihan2.collectAsState(initial = Formasi(0,0,"","",0,0,0)).value.namaSatuanKerja
    val namaSatuanKerjaPilihan3 = pilihanSaya.pilihan3.collectAsState(initial = Formasi(0,0,"","",0,0,0)).value.namaSatuanKerja
    // Callback untuk mengubah item yang dipilih
    val onFormasi1Selected: (Formasi) -> Unit = { formasi ->
        selectedFormasi1 = formasi
        onValueChange(pilihanDetails.copy(pilihan1 = formasi.id))
    }
    val onFormasi2Selected: (Formasi) -> Unit = { formasi ->
        selectedFormasi2 = formasi
        onValueChange(pilihanDetails.copy(pilihan2 = formasi.id))
    }
    val onFormasi3Selected: (Formasi) -> Unit = { formasi ->
        selectedFormasi3 = formasi
        onValueChange(pilihanDetails.copy(pilihan3 = formasi.id))
    }
    Log.i("TAG", "selectedformasi: ${selectedFormasi1}")
    Log.i("TAG", "namaSatuanKerjaPilihan1: ${namaSatuanKerjaPilihan1}")

    CustomDropdown(
        items = formasiList,
        selectedItem = namaSatuanKerjaPilihan1,
        onItemSelected = onFormasi1Selected,
        label = "Pilihan 1*"
    )
    CustomDropdown(
        items = formasiList,
        selectedItem = namaSatuanKerjaPilihan2,
        onItemSelected = onFormasi2Selected,
        label = "Pilihan 2*"
    )
    CustomDropdown(
        items = formasiList,
        selectedItem = namaSatuanKerjaPilihan3,
        onItemSelected = onFormasi3Selected,
        label = "Pilihan 3*"
    )

    if (enabled) {
        Text(
            text = stringResource(R.string.required_fields),
            modifier = Modifier.padding(start = dimensionResource(id = R.dimen.padding_medium))
        )
    }
}



@Composable
fun EditPilihanAppBar(
    openDrawer: () -> Unit,
    backgroundColor: Color,
    modifier: Modifier = Modifier,
    onAccount: () -> Unit,
    title: String,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit = {}
) {
    val navController = rememberNavController()

    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        if (navController.previousBackStackEntry != null) {
                            navController.navigateUp()
                        } else {
                            navigateUp()
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = stringResource(R.string.cd_back),
                        modifier = Modifier
                            .padding(start = 4.dp)
                            .heightIn(max = 24.dp)
                            .align(Alignment.CenterVertically)
                    )
                }
                Text(text = title)
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
