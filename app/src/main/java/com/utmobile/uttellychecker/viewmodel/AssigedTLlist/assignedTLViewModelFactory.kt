package com.utmobile.uttellychecker.viewmodel.AssigedTLlist

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.utmobile.uttellychecker.repository.UTTellyRespository

class AssignedTLViewModelFactory (

    private val application: Application,
    private val utTellyRespository: UTTellyRespository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AssignedTLlistViewModel(application, utTellyRespository) as T
    }
}