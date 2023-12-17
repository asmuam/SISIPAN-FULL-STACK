package com.polstat.sisipan.ui.pilihan

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import com.polstat.sisipan.api.PilihanRequest
import com.polstat.sisipan.data.Formasi
import com.polstat.sisipan.data.Provinsi
import com.polstat.sisipan.ui.formasiDummy
import com.polstat.sisipan.ui.pilihan.AddPilihanViewModel
import com.polstat.sisipan.ui.pilihan.AddPilihanViewState
import com.polstat.sisipan.ui.theme.MinContrastOfPrimaryVsSurface
import com.polstat.sisipan.util.DynamicThemePrimaryColorsFromImage
import com.polstat.sisipan.util.contrastAgainst
import com.polstat.sisipan.util.rememberDominantColorState
import com.polstat.sisipan.util.verticalGradientScrim
import kotlinx.coroutines.launch

@Composable
@Preview
fun previewAddPilihan(
) {
    Surface(Modifier.fillMaxSize()) {
        AddPilihanContent(
            openDrawer = {},
            isRefreshing = false,
            onAccount = {},
            role = "Admin",
            navigateBack = {},
            canNavigateBack = true,
            onNavigateUp = {},
            addPilihanViewState =
            AddPilihanViewState(
                role = "",
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
fun AddPilihan(
    openDrawer: () -> Unit,
    onAccount: () -> Unit,
    navigateBack: () -> Unit,
    canNavigateBack: Boolean = true,
    onNavigateUp: () -> Unit,
    viewModel: AddPilihanViewModel = viewModel(),

    ) {
    val viewState by viewModel.state.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(viewModel){
        viewModel.refresh(true)
    }
    Log.i("DATA", "AddPilihan: ${viewState.pilihanUiState.pilihanDetails}")
    Surface(Modifier.fillMaxSize()) {
        AddPilihanContent(
            openDrawer,
            isRefreshing = viewState.refreshing,
            modifier = Modifier.fillMaxSize(),
            onAccount,
            role = viewState.role,
            navigateBack = navigateBack,
            onNavigateUp = onNavigateUp,
            canNavigateBack = canNavigateBack,
            addPilihanViewState = viewState,
            onPilihanValueChange = viewModel::updateUiState,
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
fun AddPilihanContent(
    openDrawer: () -> Unit,
    isRefreshing: Boolean,
    modifier: Modifier = Modifier,
    onAccount: () -> Unit,
    role: String,
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    canNavigateBack: Boolean,
    addPilihanViewState: AddPilihanViewState,
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

                    AddPilihanAppBar(
                        openDrawer,
                        backgroundColor = appBarColor,
                        modifier = Modifier.fillMaxWidth(),
                        onAccount,
                        title = stringResource(R.string.add_pilihan),
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
                        PilihanInputForm(
                            formasiList = addPilihanViewState.formasiList,
                            pilihanDetails = addPilihanViewState.pilihanUiState.pilihanDetails,
                            onValueChange = onPilihanValueChange,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Button(
                            onClick = onSaveClick,
                            enabled = addPilihanViewState.pilihanUiState.isEntryValid,
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
fun PilihanInputForm(
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

    CustomDropdown(
        items = formasiList,
        selectedItem = selectedFormasi1?.namaSatuanKerja.orEmpty(),
        onItemSelected = onFormasi1Selected,
        label = "Pilihan 1*"
    )
    CustomDropdown(
        items = formasiList,
        selectedItem = selectedFormasi2?.namaSatuanKerja.orEmpty(),
        onItemSelected = onFormasi2Selected,
        label = "Pilihan 2*"
    )
    CustomDropdown(
        items = formasiList,
        selectedItem = selectedFormasi3?.namaSatuanKerja.orEmpty(),
        onItemSelected = onFormasi3Selected,
        label = "Pilihan 3*"
    )

    if (enabled) {
        Text(
            text = stringResource(R.string.required_fields),
            modifier = Modifier.padding(start = dimensionResource(id = R.dimen.padding_medium))
        )
        Text(
            text = stringResource(R.string.differ),
            modifier = Modifier.padding(start = dimensionResource(id = R.dimen.padding_medium))
        )
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CustomDropdown(
    items: List<Formasi>,
    selectedItem: String,
    onItemSelected: (Formasi) -> Unit,
    label: String,
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

    Column() {
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
                    imeAction = ImeAction.Done
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .onGloballyPositioned { coordinates ->
                        // This value is used to assign to
                        // the DropDown the same width
                        mTextFieldSize = coordinates.size.toSize()
                    },
                label = { Text(label) },
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
                        inputValue = item.namaSatuanKerja
                    }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (item.id?.toInt() == 0) "Pilih Provinsi" else item.namaSatuanKerja,
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
fun AddPilihanAppBar(
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