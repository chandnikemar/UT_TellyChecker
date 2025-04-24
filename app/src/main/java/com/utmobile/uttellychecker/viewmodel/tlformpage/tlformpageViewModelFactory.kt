package com.utmobile.uttellychecker.viewmodel.tlformpage

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.utmobile.uttellychecker.repository.UTTellyRespository

class TlformpageViewModelFactory(
    private val application: Application,
    private val repository: UTTellyRespository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TLFormPageViewModel::class.java)) {
            return TLFormPageViewModel(application, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
