package com.polstat.sisipan.ui.formasi

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.polstat.sisipan.R
import com.polstat.sisipan.data.Provinsi
import com.polstat.sisipan.ui.AppViewModelProvider
import com.polstat.sisipan.ui.theme.MinContrastOfPrimaryVsSurface
import com.polstat.sisipan.util.DynamicThemePrimaryColorsFromImage
import com.polstat.sisipan.util.contrastAgainst
import com.polstat.sisipan.util.rememberDominantColorState
import com.polstat.sisipan.util.verticalGradientScrim
import kotlinx.coroutines.launch

@Composable
fun EditFormasi(
    openDrawer: () -> Unit,
    onAccount: () -> Unit,
    navigateBack: () -> Unit,
    canNavigateBack: Boolean = true,
    onNavigateUp: () -> Unit,
    viewModel: EditFormasiViewModel = viewModel(factory = AppViewModelProvider.Factory),

    ) {
    val viewState by viewModel.state.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()
    Surface(Modifier.fillMaxSize()) {
        EditFormasiContent(
            openDrawer,
            isRefreshing = viewState.refreshing,
            modifier = Modifier.fillMaxSize(),
            onAccount,
            role = viewState.role,
            navigateBack = navigateBack,
            onNavigateUp = onNavigateUp,
            canNavigateBack = canNavigateBack,
            editFormasiViewState = viewState,
            onFormasiValueChange = { viewModel.updateUiState(it) },
            onSaveClick = {
                // Note: If the user rotates the screen very fast, the operation may get cancelled
                // and the book may not be saved in the Database. This is because when config
                // change occurs, the Activity will be recreated and the rememberCoroutineScope will
                // be cancelled - since the scope is bound to composition.
                coroutineScope.launch {
                    viewModel.saveFormasi()
                    navigateBack()
                }
            },
        )
    }
}

@Composable
fun EditFormasiContent(
    openDrawer: () -> Unit,
    isRefreshing: Boolean,
    modifier: Modifier = Modifier,
    onAccount: () -> Unit,
    role: String,
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    canNavigateBack: Boolean,
    editFormasiViewState: EditFormasiViewState,
    onFormasiValueChange: (FormasiDetails) -> Unit,
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

                    EditFormasiAppBar(
                        openDrawer,
                        backgroundColor = appBarColor,
                        modifier = Modifier.fillMaxWidth(),
                        onAccount,
                        title = stringResource(R.string.edit_formasi),
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
                        Log.i("TAG", "formasiData: ${editFormasiViewState.formasiUiState.formasiDetails}")
                        Log.i("TAG", "formasiData: ${editFormasiViewState.formasiUiState.formasiDetails}")

                        EditFormasiInputForm(
                            provinsiList = editFormasiViewState.provinsiList,
                            formasiData = editFormasiViewState.formasiUiState.formasiDetails,
                            onValueChange = onFormasiValueChange,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Button(
                            onClick = onSaveClick,
                            enabled = editFormasiViewState.formasiUiState.isEntryValid,
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
fun EditFormasiInputForm(
    provinsiList: List<Provinsi>,
    formasiData: FormasiDetails,
    onValueChange: (FormasiDetails) -> Unit = {},
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
) {
// State untuk menyimpan item yang dipilih
    var selectedProvinsi by remember { mutableStateOf<Provinsi?>(null) }
    // Callback untuk mengubah item yang dipilih
    val provinsiDefault = formasiData.provinsiId
    val selectedItem: String = provinsiList.find { it.id == provinsiDefault }?.namaProvinsi ?: "K/L/D/I"
    val onProvinsiSelected: (Provinsi) -> Unit = { provinsi ->
        selectedProvinsi = provinsi
        // Jika perlu, panggil callback untuk memberi tahu ViewModel bahwa item dipilih
        onValueChange(formasiData.copy(provinsiId = provinsi.id))
    }
    Log.i("TAG", "provinsiDefault: ${provinsiDefault}")
    Log.i("TAG", "selectedprovinsi: ${selectedProvinsi}")
    Log.i("TAG", "selecteditem: ${selectedItem}")


    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = formasiData.kodeSatker,
            onValueChange = { onValueChange(formasiData.copy(kodeSatker = it)) },
            label = { Text(stringResource(R.string.kode_satker_req)) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = androidx.compose.material3.MaterialTheme.colorScheme.secondaryContainer,
                unfocusedContainerColor = androidx.compose.material3.MaterialTheme.colorScheme.secondaryContainer,
                disabledContainerColor = androidx.compose.material3.MaterialTheme.colorScheme.secondaryContainer,
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = true
        )
        OutlinedTextField(
            value = formasiData.namaSatuanKerja,
            onValueChange = { onValueChange(formasiData.copy(namaSatuanKerja = it)) },
            label = { Text(stringResource(R.string.nama_satker_req)) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = androidx.compose.material3.MaterialTheme.colorScheme.secondaryContainer,
                unfocusedContainerColor = androidx.compose.material3.MaterialTheme.colorScheme.secondaryContainer,
                disabledContainerColor = androidx.compose.material3.MaterialTheme.colorScheme.secondaryContainer,
            ),
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = true
        )
        // Composable Dropdown
        CustomDropdown(
            items = listOf(
                Provinsi(
                    id = null,
                    kodeProvinsi = "",
                    namaProvinsi = "K/L/D/I"
                )
            ) + provinsiList,
            selectedItem =selectedItem,
            onItemSelected = onProvinsiSelected
        )
        OutlinedTextField(
            value = formasiData.kuotaKs,
            onValueChange = { onValueChange(formasiData.copy(kuotaKs = it)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            label = { Text(stringResource(R.string.kuota_ks_req)) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = androidx.compose.material3.MaterialTheme.colorScheme.secondaryContainer,
                unfocusedContainerColor = androidx.compose.material3.MaterialTheme.colorScheme.secondaryContainer,
                disabledContainerColor = androidx.compose.material3.MaterialTheme.colorScheme.secondaryContainer,
            ),
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = true
        )
        OutlinedTextField(
            value = formasiData.kuotaSt,
            onValueChange = { onValueChange(formasiData.copy(kuotaSt = it)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            label = { Text(stringResource(R.string.kuota_st_req)) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = androidx.compose.material3.MaterialTheme.colorScheme.secondaryContainer,
                unfocusedContainerColor = androidx.compose.material3.MaterialTheme.colorScheme.secondaryContainer,
                disabledContainerColor = androidx.compose.material3.MaterialTheme.colorScheme.secondaryContainer,
            ),
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = true
        )
        OutlinedTextField(
            value = formasiData.kuotaD3,
            onValueChange = { onValueChange(formasiData.copy(kuotaD3 = it)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            label = { Text(stringResource(R.string.kuota_d3_req)) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = androidx.compose.material3.MaterialTheme.colorScheme.secondaryContainer,
                unfocusedContainerColor = androidx.compose.material3.MaterialTheme.colorScheme.secondaryContainer,
                disabledContainerColor = androidx.compose.material3.MaterialTheme.colorScheme.secondaryContainer,
            ),
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = true
        )
        if (enabled) {
            Text(
                text = stringResource(R.string.required_fields),
                modifier = Modifier.padding(start = dimensionResource(id = R.dimen.padding_medium))
            )
        }
    }
}

@Composable
fun EditFormasiAppBar(
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
