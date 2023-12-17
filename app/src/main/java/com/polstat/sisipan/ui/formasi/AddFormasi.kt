package com.polstat.sisipan.ui.formasi

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ContentAlpha
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.polstat.sisipan.R
import com.polstat.sisipan.data.Provinsi
import com.polstat.sisipan.ui.provDummy
import com.polstat.sisipan.ui.theme.MinContrastOfPrimaryVsSurface
import com.polstat.sisipan.util.DynamicThemePrimaryColorsFromImage
import com.polstat.sisipan.util.contrastAgainst
import com.polstat.sisipan.util.rememberDominantColorState
import com.polstat.sisipan.util.verticalGradientScrim
import kotlinx.coroutines.launch


@Composable
@Preview
fun AddFormasiContentPreview() {
    AddFormasiContent(
        openDrawer = {},
        isRefreshing = false,
        onAccount = {},
        role = "Admin",
        navigateBack = {},
        onNavigateUp = {},
        canNavigateBack = true,
        addFormasiViewState = AddFormasiViewState(
            refreshing = false,
            role = "Admin",
            provinsiList = provDummy + listOf(
                Provinsi(
                    id = 0,
                    kodeProvinsi = "",
                    namaProvinsi = "K/L/D/I"
                )
            ),
            formasiUiState = FormasiUiState(formasiDetails = FormasiDetails())
        ),
        onFormasiValueChange = {},
        onSaveClick = {}
    )
}

@Composable
fun AddFormasi(
    openDrawer: () -> Unit,
    onAccount: () -> Unit,
    navigateBack: () -> Unit,
    canNavigateBack: Boolean = true,
    onNavigateUp: () -> Unit,
    viewModel: AddFormasiViewModel = viewModel(),

    ) {
    val viewState by viewModel.state.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()

    Log.i("DATA", "AddFormasi: ${viewState.formasiUiState.formasiDetails}")
    Surface(Modifier.fillMaxSize()) {
        AddFormasiContent(
            openDrawer,
            isRefreshing = viewState.refreshing,
            modifier = Modifier.fillMaxSize(),
            onAccount,
            role = viewState.role,
            navigateBack = navigateBack,
            onNavigateUp = onNavigateUp,
            canNavigateBack = canNavigateBack,
            addFormasiViewState = viewState,
            onFormasiValueChange = viewModel::updateUiState,
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
fun AddFormasiContent(
    openDrawer: () -> Unit,
    isRefreshing: Boolean,
    modifier: Modifier = Modifier,
    onAccount: () -> Unit,
    role: String,
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    canNavigateBack: Boolean,
    addFormasiViewState: AddFormasiViewState,
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

                    AddFormasiAppBar(
                        openDrawer,
                        backgroundColor = appBarColor,
                        modifier = Modifier.fillMaxWidth(),
                        onAccount,
                        title = stringResource(R.string.add_formasi),
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
                        FormasiInputForm(
                            provinsiList = addFormasiViewState.provinsiList,
                            formasiDetails = addFormasiViewState.formasiUiState.formasiDetails,
                            onValueChange = onFormasiValueChange,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Button(
                            onClick = onSaveClick,
                            enabled = addFormasiViewState.formasiUiState.isEntryValid,
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
fun FormasiInputForm(
    provinsiList: List<Provinsi>,
    formasiDetails: FormasiDetails,
    modifier: Modifier = Modifier,
    onValueChange: (FormasiDetails) -> Unit = {},
    enabled: Boolean = true
) {
    // State untuk menyimpan item yang dipilih
    var selectedProvinsi by remember { mutableStateOf<Provinsi?>(null) }
    // Callback untuk mengubah item yang dipilih
    val onProvinsiSelected: (Provinsi) -> Unit = { provinsi ->
        selectedProvinsi = provinsi
        // Jika perlu, panggil callback untuk memberi tahu ViewModel bahwa item dipilih
        onValueChange(formasiDetails.copy(provinsiId = provinsi.id))
    }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = formasiDetails.kodeSatker,
            onValueChange = { onValueChange(formasiDetails.copy(kodeSatker = it)) },
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
            value = formasiDetails.namaSatuanKerja,
            onValueChange = { onValueChange(formasiDetails.copy(namaSatuanKerja = it)) },
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
            selectedItem = selectedProvinsi?.namaProvinsi.orEmpty(),
            onItemSelected = onProvinsiSelected
        )
        OutlinedTextField(
            value = formasiDetails.kuotaKs,
            onValueChange = { onValueChange(formasiDetails.copy(kuotaKs = it)) },
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
            value = formasiDetails.kuotaSt,
            onValueChange = { onValueChange(formasiDetails.copy(kuotaSt = it)) },
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
            value = formasiDetails.kuotaD3,
            onValueChange = { onValueChange(formasiDetails.copy(kuotaD3 = it)) },
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

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CustomDropdown(
    items: List<Provinsi>,
    selectedItem: String,
    onItemSelected: (Provinsi) -> Unit
) {
    Log.i("TAG", "selectedItem: ${selectedItem}")
    var expanded by remember { mutableStateOf(false) }
    var inputValue by remember { mutableStateOf(selectedItem) }
    var mTextFieldSize by remember { mutableStateOf(Size.Zero) }
    Log.i("TAG", "inputValue: ${inputValue}")

    // Inisialisasi inputValue menggunakan selectedItem
    LaunchedEffect(selectedItem) {
        inputValue = selectedItem
        Log.i("TAG", "inputValueLaunch: ${inputValue}")

    }
    // Up Icon when expanded and down icon when collapsed
    val icon = if (expanded)
        Icons.Filled.KeyboardArrowUp
    else
        Icons.Filled.KeyboardArrowDown

    Column {
        Row(
            modifier = Modifier.fillMaxWidth()
                .clickable { expanded = !expanded },
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedTextField(
                value = inputValue,
                onValueChange = {
                    // Handle value change if needed
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = androidx.compose.material3.MaterialTheme.colorScheme.secondaryContainer,
                    unfocusedContainerColor = androidx.compose.material3.MaterialTheme.colorScheme.secondaryContainer,
                    disabledContainerColor = androidx.compose.material3.MaterialTheme.colorScheme.secondaryContainer,
                ),
                textStyle = LocalTextStyle.current.copy(color = Color.Black),
                keyboardOptions = KeyboardOptions.Default.copy(
                    capitalization = KeyboardCapitalization.Sentences,
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.None
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .onGloballyPositioned { coordinates ->
                        // This value is used to assign to
                        // the DropDown the same width
                        mTextFieldSize = coordinates.size.toSize()
                    },
                label = { Text("Provinsi/K/L/D/I*") },
                trailingIcon = {
                    Icon(icon, "contentDescription")
                },
                enabled = false // Make the TextField read-only
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .width(with(LocalDensity.current) { mTextFieldSize.width.toDp() })
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    onClick = {
                        onItemSelected(item)
                        expanded = false
                        inputValue = item.namaProvinsi
                    }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (item.id?.toInt() == 0) "Pilih Provinsi" else item.namaProvinsi,
                            fontWeight = if (item.id?.toInt() == 0) FontWeight.Bold else FontWeight.Normal,
                            color = if (item.id?.toInt() == 0) Color.Gray else Color.Black
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AddFormasiAppBar(
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