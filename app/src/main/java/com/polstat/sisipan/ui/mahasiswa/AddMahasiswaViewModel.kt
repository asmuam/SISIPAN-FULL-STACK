package com.polstat.sisipan.ui.mahasiswa

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.polstat.sisipan.Graph
import com.polstat.sisipan.data.Mahasiswa
import com.polstat.sisipan.data.MahasiswaRepository
import com.polstat.sisipan.data.MahasiswaStore
import com.polstat.sisipan.data.Provinsi
import com.polstat.sisipan.data.ProvinsiRepository
import com.polstat.sisipan.data.ProvinsiStore
import com.polstat.sisipan.data.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class AddMahasiswaViewModel(
    private val userRepository: UserRepository = Graph.userRepository,
    private val mahasiswaRepository: MahasiswaRepository = Graph.mahasiswaRepository,
    private val provinsiRepository: ProvinsiRepository = Graph.provinsiRepository,
    private val provinsiStore: ProvinsiStore = Graph.provinsiStore
) : ViewModel() {
    private val _state = MutableStateFlow(AddMahasiswaViewState())
    private val refreshing = MutableStateFlow(true)

    val state: StateFlow<AddMahasiswaViewState>
        get() = _state

    fun updateUiState(mahasiswaDetails: MahasiswaDetails) {
        Log.i("AddMahasiswaViewModel", "updateUiState: $mahasiswaDetails")
        _state.value = _state.value.copy(
            mahasiswaUiState = _state.value.mahasiswaUiState.copy(
                mahasiswaDetails = mahasiswaDetails,
                isEntryValid = validateInput(mahasiswaDetails)
            )
        )
    }

    init {
        Log.e("AddMahasiswaViewModel", "  start init ")
        viewModelScope.launch {
            combine(
                provinsiStore.getAll(),
                refreshing,
            ) { provList, refreshing ->
                Log.e("AddMahasiswaViewModel", "  start combine ")
                AddMahasiswaViewState(
                    role = userRepository.role ?: "",
                    provinsiList = provList,
                    refreshing = refreshing,
                    errorMessage = null /* TODO */
                )
            }.catch { throwable ->
                Log.e("AddMahasiswaViewModel", "Error in combine", throwable)
                throw throwable
            }.collect {
                Log.e("AddMahasiswaViewModel", "  update UI")
                _state.value = it
            }
        }
        refresh(false)
    }

    fun refresh(force: Boolean) {
        Log.e("AddMahasiswaViewModel", "  start refresh ")
        viewModelScope.launch {
            try {
                refreshing.value = true
                provinsiRepository.refreshProvinsi(force)
                mahasiswaRepository.refreshMahasiswa(force)
            } catch (e: Exception) {
                Log.e("AddMahasiswaViewModel", "Error refreshing MHS", e)
            } finally {
                Log.e("MahasiswaViewModel", "  done refresh ")
                refreshing.value = false
            }
        }
    }

    private fun validateInput(uiState: MahasiswaDetails = state.value.mahasiswaUiState.mahasiswaDetails): Boolean {
        return with(uiState) {
            provinsi != 0L && name.isNotBlank() && nim.isNotBlank() && ipk.isNotBlank() && prodi.isNotBlank()
        }
    }

    suspend fun saveMahasiswa() {
        if (validateInput()) {
            mahasiswaRepository.insertMahasiswa(state.value.mahasiswaUiState.mahasiswaDetails.toMahasiswa())
        }
    }
}


data class AddMahasiswaViewState(
    val role: String = "",
    val mahasiswaUiState: MahasiswaUiState = MahasiswaUiState(),
    val provinsiList: List<Provinsi> = emptyList(),
    val refreshing: Boolean = false,
    val errorMessage: String? = null
)

data class MahasiswaUiState(
    val mahasiswaDetails: MahasiswaDetails = MahasiswaDetails(),
    val isEntryValid: Boolean = false
)

data class MahasiswaDetails(
    val id: Long = 0,
    val nim: String = "",
    val name: String = "",
    val prodi: String = "",
    val provinsi: Long = 0,
    val ipk: String = "",
)

fun MahasiswaDetails.toMahasiswa(): Mahasiswa = Mahasiswa(
    id = id,
    provinsi = provinsi,
    nim = nim,
    name = name,
    prodi = prodi,
    ipk = ipk.toFloat()
)

fun Mahasiswa.toMahasiswaDetails(): MahasiswaDetails = MahasiswaDetails(
    id = id,
    provinsi = provinsi,
    nim = nim,
    name = name,
    prodi = prodi,
    ipk = ipk.toString(),
)

fun Mahasiswa.toMahasiswaUiState(isEntryValid: Boolean = false): MahasiswaUiState =
    MahasiswaUiState(
        mahasiswaDetails = this.toMahasiswaDetails(),
        isEntryValid = isEntryValid,
    )