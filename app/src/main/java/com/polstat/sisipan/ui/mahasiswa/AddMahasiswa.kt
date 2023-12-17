package com.polstat.sisipan.ui.mahasiswa

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.polstat.sisipan.data.Prodi
import com.polstat.sisipan.data.Provinsi
import com.polstat.sisipan.data.prodiList
import com.polstat.sisipan.ui.provDummy
import com.polstat.sisipan.ui.theme.MinContrastOfPrimaryVsSurface
import com.polstat.sisipan.util.DynamicThemePrimaryColorsFromImage
import com.polstat.sisipan.util.contrastAgainst
import com.polstat.sisipan.util.rememberDominantColorState
import com.polstat.sisipan.util.verticalGradientScrim
import kotlinx.coroutines.launch


@Composable
@Preview
fun AddMahasiswaContentPreview() {
    AddMahasiswaContent(
        openDrawer = {},
        isRefreshing = false,
        onAccount = {},
        role = "Admin",
        navigateBack = {},
        onNavigateUp = {},
        canNavigateBack = true,
        addMahasiswaViewState = AddMahasiswaViewState(
            refreshing = false,
            role = "Admin",
            provinsiList = provDummy + listOf(
                Provinsi(
                    id = 0,
                    kodeProvinsi = "",
                    namaProvinsi = "K/L/D/I"
                )
            ),
            mahasiswaUiState = MahasiswaUiState(mahasiswaDetails = MahasiswaDetails())
        ),
        onMahasiswaValueChange = {},
        onSaveClick = {}
    )
}

@Composable
fun AddMahasiswa(
    openDrawer: () -> Unit,
    onAccount: () -> Unit,
    navigateBack: () -> Unit,
    canNavigateBack: Boolean = true,
    onNavigateUp: () -> Unit,
    viewModel: AddMahasiswaViewModel = viewModel(),

    ) {
    val viewState by viewModel.state.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()

    Log.i("DATA", "AddMahasiswa: ${viewState.mahasiswaUiState.mahasiswaDetails}")
    Surface(Modifier.fillMaxSize()) {
        AddMahasiswaContent(
            openDrawer,
            isRefreshing = viewState.refreshing,
            modifier = Modifier.fillMaxSize(),
            onAccount,
            role = viewState.role,
            navigateBack = navigateBack,
            onNavigateUp = onNavigateUp,
            canNavigateBack = canNavigateBack,
            addMahasiswaViewState = viewState,
            onMahasiswaValueChange = viewModel::updateUiState,
            onSaveClick = {
                // Note: If the user rotates the screen very fast, the operation may get cancelled
                // and the book may not be saved in the Database. This is because when config
                // change occurs, the Activity will be recreated and the rememberCoroutineScope will
                // be cancelled - since the scope is bound to composition.
                coroutineScope.launch {
                    viewModel.saveMahasiswa()
                    navigateBack()
                }
            },
        )
    }
}

@Composable
fun AddMahasiswaContent(
    openDrawer: () -> Unit,
    isRefreshing: Boolean,
    modifier: Modifier = Modifier,
    onAccount: () -> Unit,
    role: String,
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    canNavigateBack: Boolean,
    addMahasiswaViewState: AddMahasiswaViewState,
    onMahasiswaValueChange: (MahasiswaDetails) -> Unit,
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

                    AddMahasiswaAppBar(
                        openDrawer,
                        backgroundColor = appBarColor,
                        modifier = Modifier.fillMaxWidth(),
                        onAccount,
                        title = stringResource(R.string.add_mahasiswa),
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
                        MahasiswaInputForm(
                            provinsiList = addMahasiswaViewState.provinsiList,
                            mahasiswaDetails = addMahasiswaViewState.mahasiswaUiState.mahasiswaDetails,
                            onValueChange = onMahasiswaValueChange,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Button(
                            onClick = onSaveClick,
                            enabled = addMahasiswaViewState.mahasiswaUiState.isEntryValid,
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
fun MahasiswaInputForm(
    provinsiList: List<Provinsi>,
    mahasiswaDetails: MahasiswaDetails,
    modifier: Modifier = Modifier,
    onValueChange: (MahasiswaDetails) -> Unit = {},
    enabled: Boolean = true
) {
    var selectedProvinsi by remember { mutableStateOf<Provinsi?>(null) }
    var selectedProdi by remember { mutableStateOf(Prodi.D4_KS.label) }
    val onProvinsiSelected: (Provinsi) -> Unit = { provinsi ->
        selectedProvinsi = provinsi
        onValueChange(mahasiswaDetails.copy(provinsi = provinsi.id?:0))
    }
    val onProdiSelected: (Prodi) -> Unit = { prodi ->
        selectedProdi = prodi.name
        // Jika perlu, panggil callback untuk memberi tahu ViewModel bahwa item dipilih
        onValueChange(mahasiswaDetails.copy(prodi = prodi.name))
    }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = mahasiswaDetails.name,
            onValueChange = { onValueChange(mahasiswaDetails.copy(name = it)) },
            label = { Text(stringResource(R.string.name)) },
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
            value = mahasiswaDetails.nim,
            onValueChange = { onValueChange(mahasiswaDetails.copy(nim = it)) },
            label = { Text(stringResource(R.string.nim_mahasiswa)) },
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
        com.polstat.sisipan.ui.formasi.CustomDropdown(
            items = provinsiList,
            selectedItem = selectedProvinsi?.namaProvinsi.orEmpty(),
            onItemSelected = onProvinsiSelected
        )
        CustomDropdown(
            items =prodiList,
            selectedItem = selectedProdi,
            onItemSelected = onProdiSelected,
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
    items: List<Prodi>,
    selectedItem: String,
    onItemSelected: (Prodi) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var inputValue by remember { mutableStateOf(selectedItem) }
    var mTextFieldSize by remember { mutableStateOf(Size.Zero) }

    // Inisialisasi inputValue menggunakan selectedItem
    LaunchedEffect(selectedItem) {
        inputValue = selectedItem
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
                    focusedContainerColor = MaterialTheme.colors.secondaryVariant,
                    unfocusedContainerColor = MaterialTheme.colors.secondaryVariant,
                    disabledContainerColor = MaterialTheme.colors.secondaryVariant,
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
                label = { Text("Program Studi*") },
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
                        inputValue = item.label
                    }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (item.id == "0") "Pilih Program Studi" else item.label,
                            fontWeight = if (item.id == "0") FontWeight.Bold else FontWeight.Normal,
                            color = if (item.id == "0") Color.Gray else Color.Black
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AddMahasiswaAppBar(
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