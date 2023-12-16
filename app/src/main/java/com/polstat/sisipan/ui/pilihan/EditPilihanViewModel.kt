package com.polstat.sisipan.ui.pilihan

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.polstat.sisipan.Graph
import com.polstat.sisipan.api.PilihanRequest
import com.polstat.sisipan.data.Formasi
import com.polstat.sisipan.data.FormasiStore
import com.polstat.sisipan.data.Mahasiswa
import com.polstat.sisipan.data.MahasiswaStore
import com.polstat.sisipan.data.Pilihan
import com.polstat.sisipan.data.PilihanNested
import com.polstat.sisipan.data.PilihanRepository
import com.polstat.sisipan.data.PilihanStore
import com.polstat.sisipan.data.UserRepository
import com.polstat.sisipan.ui.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

class EditPilihanViewModel(
    savedStateHandle: SavedStateHandle,
    private val userRepository: UserRepository = Graph.userRepository,
    private val pilihanRepository: PilihanRepository = Graph.pilihanRepository,
    private val pilihanStore: PilihanStore = Graph.pilihanStore,
    private val formasiStore: FormasiStore = Graph.formasiStore,
    private val mahasiswaStore: MahasiswaStore = Graph.mahasiswaStore,
) : ViewModel() {
    private val _state = MutableStateFlow(EditPilihanViewState())
    private val pilihanId: Long = checkNotNull(savedStateHandle[Screen.PilihanEdit.pilihanIdArg])
    private val refreshing = MutableStateFlow(true)

    val state: StateFlow<EditPilihanViewState>
        get() = _state

    fun updateUiState(pilihanDetails: PilihanRequest) {
        _state.value = _state.value.copy(
            pilihanUiState = _state.value.pilihanUiState.copy(
                pilihanDetails = pilihanDetails,
                isEntryValid = validateInput(pilihanDetails)
            )
        )
    }

    init {
        viewModelScope.launch {
            val pilihanSaya = pilihanStore.getById(pilihanId)
            combine(
                formasiStore.formasiBuka(),
                refreshing,
            ) { formasiList, refreshing ->
                val mahasiswa = mahasiswaStore.getMahasiswa(pilihanSaya.mahasiswa)
                val pilihan1 = formasiStore.formasiById(pilihanSaya.pilihan1 ?: 0)
                val pilihan2 = formasiStore.formasiById(pilihanSaya.pilihan2 ?: 0)
                val pilihan3 = formasiStore.formasiById(pilihanSaya.pilihan3 ?: 0)
                val pilihanSistem = formasiStore.formasiById(pilihanSaya.pilihanSistem ?: 0)
                val pilihanNestedSaya = PilihanNested(
                    id = pilihanId,
                    mahasiswa = mahasiswa,
                    pilihan1 = pilihan1,
                    pilihan2 = pilihan2,
                    pilihan3 = pilihan3,
                    pilihanSistem = pilihanSistem,
                    indeksPilihan1 = pilihanSaya.indeksPilihan1,
                    indeksPilihan2 = pilihanSaya.indeksPilihan2,
                    indeksPilihan3 = pilihanSaya.indeksPilihan3,
                    hasil = pilihanSaya.hasil,
                    ipk = pilihanSaya.ipk,
                )
                val pilihanSaya = PilihanRequest(
                    pilihanSaya.pilihan1 ?: 0,
                    pilihanSaya.pilihan2 ?: 0,
                    pilihanSaya.pilihan3 ?: 0,
                )
                EditPilihanViewState(
                    pilihanSaya = pilihanNestedSaya,
                    role = userRepository.role ?: "", //default ""
                    pilihanUiState = PilihanUiState(pilihanSaya,true),
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
        if (validateInput()) {
            Log.i("TAG", "savePilihan: ${state.value.pilihanSaya.id}")
            pilihanRepository.ubahPilihan(state.value.pilihanSaya.id, state.value.pilihanUiState.pilihanDetails)
        }
    }
}

data class EditPilihanViewState(
    val role: String = "",
    val pilihanSaya: PilihanNested = PilihanNested(
        id = 0L,
        mahasiswa = flowOf(
            Mahasiswa(
                0,
                "",
                "",
                "",
                0,
                0F
            )
        ), // Anda perlu memberikan nilai Mahasiswa yang sesuai
        pilihan1 = flowOf(
            Formasi(
                0,
                0,
                "",
                "",
                0,
                0,
                0
            )
        ), // Anda perlu memberikan nilai Formasi yang sesuai
        pilihan2 = flowOf(
            Formasi(
                0,
                0,
                "",
                "",
                0,
                0,
                0
            )
        ), // Anda perlu memberikan nilai Formasi yang sesuai
        pilihan3 = flowOf(
            Formasi(
                0,
                0,
                "",
                "",
                0,
                0,
                0
            )
        ), // Anda perlu memberikan nilai Formasi yang sesuai
        pilihanSistem = flowOf(
            Formasi(
                0,
                0,
                "",
                "",
                0,
                0,
                0
            )
        ), // Anda perlu memberikan nilai Formasi yang sesuai
        indeksPilihan1 = 0.0f,
        indeksPilihan2 = 0.0f,
        indeksPilihan3 = 0.0f,
        ipk = 0.0f,
        hasil = null
    ),
    val formasiList: List<Formasi> = emptyList(),
    val pilihanUiState: PilihanUiState = PilihanUiState(),
    val refreshing: Boolean = false,
    val errorMessage: String? = null
)

