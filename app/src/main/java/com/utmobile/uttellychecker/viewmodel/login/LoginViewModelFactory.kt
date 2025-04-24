package com.utmobile.uttellychecker.viewmodel.login

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.utmobile.uttellychecker.repository.UTTellyRespository

class LoginViewModelFactory(
    private val application: Application,
    private val utTellyRespository: UTTellyRespository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return LoginViewModel(application, utTellyRespository) as T
    }
}