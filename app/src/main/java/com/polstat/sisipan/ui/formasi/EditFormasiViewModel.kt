package com.polstat.sisipan.ui.formasi

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.polstat.sisipan.Graph
import com.polstat.sisipan.data.Formasi
import com.polstat.sisipan.data.FormasiStore
import com.polstat.sisipan.data.Mahasiswa
import com.polstat.sisipan.data.MahasiswaStore
import com.polstat.sisipan.data.FormasiRepository
import com.polstat.sisipan.data.Provinsi
import com.polstat.sisipan.data.ProvinsiStore
import com.polstat.sisipan.data.UserRepository
import com.polstat.sisipan.ui.Screen
import com.polstat.sisipan.ui.formasi.FormasiUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

class EditFormasiViewModel(
    savedStateHandle: SavedStateHandle,
    private val userRepository: UserRepository = Graph.userRepository,
    private val formasiRepository: FormasiRepository = Graph.formasiRepository,
    private val formasiStore: FormasiStore = Graph.formasiStore,
    private val provinsiStore: ProvinsiStore = Graph.provinsiStore,
) : ViewModel() {
    private val _state = MutableStateFlow(EditFormasiViewState())
    private val formasiId: Long = checkNotNull(savedStateHandle[Screen.EditFormasi.formasiIdArg])
    private val refreshing = MutableStateFlow(true)

    val state: StateFlow<EditFormasiViewState>
        get() = _state

    fun updateUiState(formasiDetails: FormasiDetails) {
        _state.value = _state.value.copy(
            formasiUiState = _state.value.formasiUiState.copy(
                formasiDetails = formasiDetails,
                isEntryValid = validateInput(formasiDetails)
            )
        )
    }

    init {
        viewModelScope.launch {
            val formasiData = formasiStore.findFormasiById(formasiId)
            combine(
                provinsiStore.getAll(),
                refreshing,
            ) { provList, refreshing ->
                EditFormasiViewState(
                    role = userRepository.role ?: "", //default ""
                    provinsiList = provList,
                    formasiUiState = FormasiUiState(formasiData.toFormasiDetails(), true),
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
            formasiRepository.ubahFormasi(
                state.value.formasiUiState.formasiDetails.id,
                state.value.formasiUiState.formasiDetails.toFormasi()
            )
        }
    }
}

data class EditFormasiViewState(
    val role: String = "",
    val formasiData: Formasi = Formasi(0L,0L,"","",0,0,0),
    val provinsiList: List<Provinsi> = emptyList(),
    val formasiUiState: FormasiUiState = FormasiUiState(),
    val refreshing: Boolean = false,
    val errorMessage: String? = null
)

