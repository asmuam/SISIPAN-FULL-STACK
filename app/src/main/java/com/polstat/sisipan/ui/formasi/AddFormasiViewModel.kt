package com.polstat.sisipan.ui.formasi

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.polstat.sisipan.Graph
import com.polstat.sisipan.Graph.provinsiStore
import com.polstat.sisipan.data.Formasi
import com.polstat.sisipan.data.FormasiRepository
import com.polstat.sisipan.data.FormasiStore
import com.polstat.sisipan.data.Provinsi
import com.polstat.sisipan.data.ProvinsiStore
import com.polstat.sisipan.data.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class AddFormasiViewModel(
    private val userRepository: UserRepository = Graph.userRepository,
    private val formasiRepository: FormasiRepository = Graph.formasiRepository,
    private val formasiStore: FormasiStore = Graph.formasiStore,
    private val provinsiStore: ProvinsiStore = Graph.provinsiStore
) : ViewModel() {
    private val _state = MutableStateFlow(AddFormasiViewState())

    private val refreshing = MutableStateFlow(true)

    val state: StateFlow<AddFormasiViewState>
        get() = _state

    fun onProvinsiSelected(selectedProvinsi: Provinsi) {
        // Do something when a provinsi is selected, if needed
    }

    fun updateUiState(formasiDetails: FormasiDetails) {
        Log.i("DETAILS?", "updateUiState:${formasiDetails} ")

        _state.value = _state.value.copy(
            formasiUiState = _state.value.formasiUiState.copy(
                formasiDetails = formasiDetails,
                isEntryValid = validateInput(formasiDetails)
            )
        )
        Log.i("DATA?", "updateUiState:${state.value.formasiUiState.formasiDetails} ")
        Log.i("VALID?", "updateUiState:${state.value.formasiUiState.isEntryValid} ")
    }

    init {
        viewModelScope.launch {
            combine(
                provinsiStore.getAll(),
                refreshing,
            ) { provList, refreshing ->
                AddFormasiViewState(
                    role = userRepository.role ?: "", //default ""
                    provinsiList = provList,
                    refreshing = refreshing,
                    errorMessage = null /* TODO */
                )
            }.catch { throwable ->
                // TODO: emit a UI error here. For now, we'll just rethrow
                throw throwable
            }.collect {
                _state.value = it
            }
        }
    }


    private fun validateInput(uiState: FormasiDetails = state.value.formasiUiState.formasiDetails): Boolean {
        return with(uiState) {
            provinsiId != 0L && kodeSatker.isNotBlank() && namaSatuanKerja.isNotBlank()
        }
    }

    suspend fun saveFormasi() {
        if (validateInput()) {
            formasiRepository.insertFormasi(state.value.formasiUiState.formasiDetails.toFormasi())
        }
    }
}

data class AddFormasiViewState(
    val role: String = "",
    val formasiUiState:FormasiUiState = FormasiUiState(),
    val provinsiList: List<Provinsi> = emptyList(),
    val refreshing: Boolean = false,
    val errorMessage: String? = null
)

    data class FormasiUiState(
        val formasiDetails: FormasiDetails = FormasiDetails(),
        val isEntryValid: Boolean = false
    )
data class FormasiDetails(
    val id: Long = 0,
    val provinsiId: Long? = null,
    val kodeSatker: String = "",
    val namaSatuanKerja: String = "",
    val kuotaSt: String = "",
    val kuotaKs: String = "",
    val kuotaD3: String = "",
)

fun FormasiDetails.toFormasi(): Formasi = Formasi(
    id = id,
    provinsiId = if (provinsiId == 0L) null else provinsiId,
    kodeSatker = kodeSatker,
    namaSatuanKerja = namaSatuanKerja,
    kuotaSt = kuotaSt.toIntOrNull() ?: 0,
    kuotaKs = kuotaKs.toIntOrNull() ?: 0,
    kuotaD3 = kuotaD3.toIntOrNull() ?: 0,
)
fun Formasi.toFormasiDetails(): FormasiDetails = FormasiDetails(
    id = id,
    provinsiId = provinsiId,
    kodeSatker = kodeSatker,
    namaSatuanKerja = namaSatuanKerja,
    kuotaSt = kuotaSt.toString(),
    kuotaKs = kuotaKs.toString(),
    kuotaD3 = kuotaD3.toString(),
)
fun Formasi.toFormasiUiState(isEntryValid: Boolean = false): FormasiUiState = FormasiUiState(
    formasiDetails = this.toFormasiDetails(),
    isEntryValid = isEntryValid,
)