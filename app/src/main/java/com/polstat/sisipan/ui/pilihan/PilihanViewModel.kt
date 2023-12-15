package com.polstat.sisipan.ui.pilihan

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.polstat.sisipan.Graph
import com.polstat.sisipan.data.FormasiStore
import com.polstat.sisipan.data.MahasiswaStore
import com.polstat.sisipan.data.Pilihan
import com.polstat.sisipan.data.PilihanNested
import com.polstat.sisipan.data.PilihanRepository
import com.polstat.sisipan.data.PilihanStore
import com.polstat.sisipan.data.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class PilihanViewModel(
    private val userRepository: UserRepository = Graph.userRepository,
    private val pilihanRepository: PilihanRepository = Graph.pilihanRepository,
    private val pilihanStore: PilihanStore = Graph.pilihanStore,
    private val mahasiswaStore: MahasiswaStore = Graph.mahasiswaStore,
    private val formasiStore: FormasiStore = Graph.formasiStore,
) : ViewModel() {

    // Holds our view state which the UI collects via [state]
    private val _state = MutableStateFlow(PilihanViewState())

    private val refreshing = MutableStateFlow(false)

    val state: StateFlow<PilihanViewState>
        get() = _state

    init {
        viewModelScope.launch {
            // Combines the latest value from each of the flows, allowing us to generate a
            // view state instance which only contains the latest values.
            combine(
                pilihanStore.daftarPilihan(),
                refreshing
            ) { pilihanList, refreshing ->
                // Map PilihanList to PilihanNested
                val mappedPilihanList = pilihanList.map { pilihan ->
                    val mahasiswa = mahasiswaStore.getMahasiwa(pilihan.mahasiswaId)
                    val pilihan1 = formasiStore.formasiById(pilihan.pilihan1Id ?: 0)
                    val pilihan2 = formasiStore.formasiById(pilihan.pilihan2Id ?: 0)
                    val pilihan3 = formasiStore.formasiById(pilihan.pilihan3Id ?: 0)
                    val pilihanSistem = formasiStore.formasiById(pilihan.pilihanSistemId ?: 0)

                    PilihanNested(
                        id = pilihan.id,
                        mahasiswa = mahasiswa,
                        pilihan1 = pilihan1,
                        pilihan2 = pilihan2,
                        pilihan3 = pilihan3,
                        pilihanSistem = pilihanSistem,
                        indeksPilihan1 = pilihan.indeksPilihan1,
                        indeksPilihan2 = pilihan.indeksPilihan2,
                        indeksPilihan3 = pilihan.indeksPilihan3,
                        ipk = pilihan.ipk,
                        hasil = pilihan.hasil
                    )
                }

                PilihanViewState(
                    role = userRepository.role,
                    pilihanList = mappedPilihanList,
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

        refresh(force = false)
    }


    private fun refresh(force: Boolean) {
        viewModelScope.launch {
            try {
                refreshing.value = true
                pilihanRepository.refreshPilihan(force)
                // Handle the response
            } catch (e: Exception) {
                // Handle the error
                Log.e("PilihanViewModel", "Error refreshing pilihan", e)
            } finally {
                refreshing.value = false
            }
        }
    }


}

data class PilihanViewState(
    val role: String? = null,
    val pilihanList: List<PilihanNested> = emptyList(),
    val refreshing: Boolean = false,
    val errorMessage: String? = null
)

