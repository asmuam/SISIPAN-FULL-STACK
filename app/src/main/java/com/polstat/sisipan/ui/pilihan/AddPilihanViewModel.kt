package com.polstat.sisipan.ui.pilihan

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.polstat.sisipan.Graph
import com.polstat.sisipan.api.PilihanRequest
import com.polstat.sisipan.data.Formasi
import com.polstat.sisipan.data.FormasiStore
import com.polstat.sisipan.data.Pilihan
import com.polstat.sisipan.data.PilihanRepository
import com.polstat.sisipan.data.PilihanStore
import com.polstat.sisipan.data.Provinsi
import com.polstat.sisipan.data.ProvinsiStore
import com.polstat.sisipan.data.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class AddPilihanViewModel(
    private val userRepository: UserRepository = Graph.userRepository,
    private val pilihanRepository: PilihanRepository = Graph.pilihanRepository,
    private val pilihanStore: PilihanStore = Graph.pilihanStore,
    private val formasiStore: FormasiStore = Graph.formasiStore
) : ViewModel() {
    private val _state = MutableStateFlow(AddPilihanViewState())

    private val refreshing = MutableStateFlow(true)

    val state: StateFlow<AddPilihanViewState>
        get() = _state

    fun updateUiState(pilihanDetails: PilihanRequest) {
        Log.i("DETAILS?", "updateUiState:${pilihanDetails} ")

        _state.value = _state.value.copy(
            pilihanUiState = _state.value.pilihanUiState.copy(
                pilihanDetails = pilihanDetails,
                isEntryValid = validateInput(pilihanDetails)
            )
        )
        Log.i("DATA?", "updateUiState:${state.value.pilihanUiState.pilihanDetails} ")
        Log.i("VALID?", "updateUiState:${state.value.pilihanUiState.isEntryValid} ")
    }

    init {
        viewModelScope.launch {
            combine(
                formasiStore.formasiBuka(),
                refreshing,
            ) { formasiList, refreshing ->
                AddPilihanViewState(
                    role = userRepository.role ?: "", //default ""
                    formasiList = formasiList,
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


    private fun validateInput(uiState: PilihanRequest = state.value.pilihanUiState.pilihanDetails): Boolean {
        return with(uiState) {
            pilihan1 != 0L && pilihan2 != 0L && pilihan3 != 0L
        }
    }

    suspend fun savePilihan() {
        val id = userRepository.idMhs
        if (validateInput() && id!=null) {
            pilihanRepository.insertPilihan(id ,state.value.pilihanUiState.pilihanDetails)
        }
    }
}

data class AddPilihanViewState(
    val role: String = "",
    val formasiList:List<Formasi> = emptyList(),
    val pilihanUiState:PilihanUiState = PilihanUiState(),
    val refreshing: Boolean = false,
    val errorMessage: String? = null
)

data class PilihanUiState(
    val pilihanDetails: PilihanRequest = PilihanRequest(0L,0L,0L),
    val isEntryValid: Boolean = false
)
