package com.utmobile.uttellychecker.viewmodel.login


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.utmobile.uttellychecker.helper.Constants
import com.utmobile.uttellychecker.helper.Resource
import com.utmobile.uttellychecker.helper.Utils
import com.utmobile.uttellychecker.model.appDetails.GetAppDetailsResponse
import com.utmobile.uttellychecker.model.login.LoginRequest
import com.utmobile.uttellychecker.model.login.LoginResponse
import com.utmobile.uttellychecker.repository.UTTellyRespository
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response

class LoginViewModel (
    application: Application,
    private val utTellyRespository: UTTellyRespository
) : AndroidViewModel(application) {
    val loginMutableLiveData: MutableLiveData<Resource<LoginResponse>> = MutableLiveData()

    fun login(
        baseUrl: String,
        loginRequest: LoginRequest
    ) {
        viewModelScope.launch {
            safeAPICallDtmsLogin(baseUrl, loginRequest)
        }
    }

    private fun handleDtmsUserLoginResponse(response: Response<LoginResponse>): Resource<LoginResponse> {
        var errorMessage = ""
        if (response.isSuccessful) {
            response.body()?.let { Response ->
                return Resource.Success(Response)
            }
        } else if (response.errorBody() != null) {
            val errorObject = response.errorBody()?.let {
                JSONObject(it.charStream().readText())
            }
            errorObject?.let {
                errorMessage = it.getString(Constants.HTTP_ERROR_MESSAGE)
            }
        }
        return Resource.Error(errorMessage)
    }

    private suspend fun safeAPICallDtmsLogin(baseUrl: String, loginRequest: LoginRequest) {
        loginMutableLiveData.postValue(Resource.Loading())
        try {
            if (Utils.hasInternetConnection(getApplication())) {
                val response = utTellyRespository.login(baseUrl, loginRequest)
                loginMutableLiveData.postValue(handleDtmsUserLoginResponse(response))
            } else {
                loginMutableLiveData.postValue(Resource.Error(Constants.NO_INTERNET))
            }
        } catch (t: Throwable) {
            when (t) {
                is Exception -> {
                    loginMutableLiveData.postValue(Resource.Error("${t.message}"))
                }
                else -> loginMutableLiveData.postValue(Resource.Error(Constants.CONFIG_ERROR))
            }
        }
    }

    //getApp details
    val getAppDetailsMutableLiveData: MutableLiveData<Resource<GetAppDetailsResponse>> = MutableLiveData()

    fun getAppDetails(
        baseUrl: String
    ) = viewModelScope.launch {
        safeAPICallGetAppDetails(baseUrl)
    }

    private suspend fun safeAPICallGetAppDetails(baseUrl: String) {
        getAppDetailsMutableLiveData.postValue(Resource.Loading())
        try {
            if (Utils.hasInternetConnection(getApplication())) {
                val response = utTellyRespository.getAppDetails(baseUrl)
                getAppDetailsMutableLiveData.postValue(handleGetAppDetailsResponse(response))
            } else {
                getAppDetailsMutableLiveData.postValue(Resource.Error(Constants.NO_INTERNET))
            }
        } catch (t: Throwable) {
            when (t) {
                is Exception -> {
                    getAppDetailsMutableLiveData.postValue(Resource.Error("${t.message}"))

                }
                else -> getAppDetailsMutableLiveData.postValue(Resource.Error(Constants.CONFIG_ERROR))
            }
        }
    }


    private fun handleGetAppDetailsResponse(response: Response<GetAppDetailsResponse>): Resource<GetAppDetailsResponse> {
        var errorMessage = ""
        if (response.isSuccessful) {
            response.body()?.let { appDetailsResponse ->
                return Resource.Success(appDetailsResponse)
            }
        } else if (response.errorBody() != null) {
            val errorObject = response.errorBody()?.let {
                JSONObject(it.charStream().readText())
            }
            errorObject?.let {
                errorMessage = it.getString(Constants.HTTP_ERROR_MESSAGE)
            }
        }
        return Resource.Error(errorMessage)
    }

//    /////////changepasword Api
//    val changePasswordMutableLiveData: MutableLiveData<Resource<ChangePasswordResponse>> =
//        MutableLiveData()
//
//    fun changePassword(
//        token:String,
//        baseUrl: String,
//        changePasswordRequest: ChangePasswordRequest
//    ) {
//        viewModelScope.launch {
//            safeAPICallChangePasswordDetails(token,baseUrl, changePasswordRequest)
//        }
//    }
//
//    private suspend fun safeAPICallChangePasswordDetails(
//        token: String,
//        baseUrl: String,
//        changePasswordRequest: ChangePasswordRequest
//    ) {
//        changePasswordMutableLiveData.postValue(Resource.Loading())
//        try {
//            if (Utils.hasInternetConnection(getApplication())) {
//                val response = kymsRepository.changePassword(token,baseUrl, changePasswordRequest)
//                changePasswordMutableLiveData.postValue(handleChangePasswordResponse(response))
//            } else {
//                changePasswordMutableLiveData.postValue(Resource.Error(Constants.NO_INTERNET))
//            }
//        } catch (t: Throwable) {
//            when (t) {
//                is Exception -> {
//                    changePasswordMutableLiveData.postValue(Resource.Error("${t.message}"))
//                }
//
//                else -> changePasswordMutableLiveData.postValue(Resource.Error(Constants.CONFIG_ERROR))
//            }
//        }
//    }
//
//    private fun handleChangePasswordResponse(response: Response<ChangePasswordResponse>): Resource<ChangePasswordResponse> {
//        var errorMessage = ""
//        if (response.isSuccessful) {
//            response.body()?.let { response ->
//                return Resource.Success(response)
//            }
//        } else if (response.errorBody() != null) {
//            val errorObject = response.errorBody()?.let {
//                JSONObject(it.charStream().readText())
//            }
//            errorObject?.let {
//                errorMessage = it.getString(Constants.HTTP_ERROR_MESSAGE)
//            }
//        }
//        return Resource.Error(errorMessage)
//    }
}