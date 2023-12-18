package com.polstat.sisipan.ui.pilihan

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.polstat.sisipan.Graph
import com.polstat.sisipan.api.PilihanRequest
import com.polstat.sisipan.data.Formasi
import com.polstat.sisipan.data.FormasiRepository
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
    private val formasiRepository: FormasiRepository = Graph.formasiRepository,
    private val formasiStore: FormasiStore = Graph.formasiStore
) : ViewModel() {
    private val _state = MutableStateFlow(AddPilihanViewState())
    private val refreshing = MutableStateFlow(true)

    val state: StateFlow<AddPilihanViewState>
        get() = _state

    fun updateUiState(pilihanDetails: PilihanRequest) {
        Log.i("AddPilihanViewModel", "updateUiState: $pilihanDetails")
        _state.value = _state.value.copy(
            pilihanUiState = _state.value.pilihanUiState.copy(
                pilihanDetails = pilihanDetails,
                isEntryValid = validateInput(pilihanDetails)
            )
        )
        Log.i("AddPilihanViewModel", "updateUiState: ${state.value.pilihanUiState.pilihanDetails}")
        Log.i("AddPilihanViewModel", "updateUiState: ${state.value.pilihanUiState.isEntryValid}")
    }

    init {
        Log.i("AddPilihanVM", "init start ")
        viewModelScope.launch {
            combine(
                formasiStore.formasiBuka(),
                refreshing,
            ) { formasiList, refreshing ->
                AddPilihanViewState(
                    role = userRepository.role ?: "",
                    formasiList = formasiList,
                    refreshing = refreshing,
                    errorMessage = null /* TODO */
                )
            }.catch { throwable ->
                Log.i("AddPilihanVM", "combine ")
                throw throwable
            }.collect {
                _state.value = it
                Log.i("AddPilihanVM", "update ui ")
            }
        }
        refresh(false)
    }

    fun refresh(force: Boolean) {
        Log.i("AddPilihanVM", "refresh start ")
        viewModelScope.launch {
            try {
                refreshing.value = true
                formasiRepository.refreshFormasi(force)
            } catch (e: Exception) {
                Log.e("AddPilihanViewModel", "Error refreshing MHS", e)
            } finally {
                refreshing.value = false
                Log.i("AddPilihanVM", "refresh done ")

            }
        }
    }

    private fun validateInput(uiState: PilihanRequest = state.value.pilihanUiState.pilihanDetails): Boolean {
        return with(uiState) {
            pilihan1 != 0L && pilihan2 != 0L && pilihan3 != 0L && pilihan1 != pilihan2 && pilihan1 != pilihan3 && pilihan2 != pilihan3
        }
    }

    suspend fun savePilihan() {
        val id = userRepository.idMhs
        if (validateInput() && id != null) {
            viewModelScope.launch {
                pilihanRepository.insertPilihan(id, state.value.pilihanUiState.pilihanDetails)
            }
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
