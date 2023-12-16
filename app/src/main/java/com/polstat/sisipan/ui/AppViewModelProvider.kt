package com.polstat.sisipan.ui

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.polstat.sisipan.ui.pilihan.EditPilihanViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
        // Initializer for BookEditViewModel
        initializer {
            EditPilihanViewModel(
                this.createSavedStateHandle(),
            )
        }
    }
}

/**
 * Extension function to queries for [Application] object and returns an instance of
 * [BookApplication].
 */
