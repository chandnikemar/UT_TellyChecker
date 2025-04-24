package com.utmobile.uttellychecker.viewmodel.tlformpage

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.utmobile.uttellychecker.helper.Resource
import com.utmobile.uttellychecker.helper.Utils
import com.utmobile.uttellychecker.model.AssignTlSubmit.AssignTlSubmitRequest
import com.utmobile.uttellychecker.model.AssignTlSubmit.AssignTlSubmitResponse
import com.utmobile.uttellychecker.model.AssigningDetailResponse
import com.utmobile.uttellychecker.model.Location
import com.utmobile.uttellychecker.model.LocationResponse
import com.utmobile.uttellychecker.repository.UTTellyRespository
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response

class TLFormPageViewModel(
    application: Application,
    private val utTellyRepository: UTTellyRespository
) : AndroidViewModel(application) {

    val locationsLiveData: MutableLiveData<Resource<List<Location>>> = MutableLiveData()
    val submitTLResponseLiveData: MutableLiveData<Resource<AssignTlSubmitResponse>> = MutableLiveData()
    val reprintResponseLiveData: MutableLiveData<Resource<AssignTlSubmitResponse>> = MutableLiveData()
    val reAssignedResponseLiveData: MutableLiveData<Resource<AssignTlSubmitResponse>> = MutableLiveData()
    val assignedDetailTLLiveData : MutableLiveData<Resource<List<AssigningDetailResponse>>> = MutableLiveData()
    fun getTruckLoaderLocations(
        token: String,
        baseUrl: String,
        locationType: String = "Truck Loader",
        requestId: Int = 1
    ) {
        viewModelScope.launch {
            safeAPICallGetLocations(token, baseUrl, locationType, requestId)
        }
    }

    fun getAssigningDetail(token: String, baseUrl: String, elvCode: String) {
        viewModelScope.launch {
            safeAPICallAssigningDetail(token, baseUrl, elvCode)
        }
    }
    fun submitTLassignPrintSlip(
        token: String,
        baseUrl: String,
        request: AssignTlSubmitRequest
    ) {
        viewModelScope.launch {
            safeSubmitTLassignPrintSlip(token, baseUrl, request)
        }
    }
    fun reAsssigedReassignPrintSlip(
        token: String,
        baseUrl: String,
        request: AssignTlSubmitRequest
    ) {
        viewModelScope.launch {
            safereAssignedPrintSlipp(token, baseUrl, request)
        }
    }
    fun reprintReassignPrintSlip(
        token: String,
        baseUrl: String,
        request: AssignTlSubmitRequest
    ) {
        viewModelScope.launch {
            safereprintReassignPrintSlipp(token, baseUrl, request)
        }
    }
    private suspend fun safeAPICallGetLocations(
        token: String,
        baseUrl: String,
        locationType: String,
        requestId: Int
    ) {
        locationsLiveData.postValue(Resource.Loading())
        try {
            if (Utils.hasInternetConnection(getApplication())) {
                val response = utTellyRepository.getTruckLoaderLocations(
                  token,
                   baseUrl,
                    locationType,
                   requestId
                )
                locationsLiveData.postValue(handleLocationResponse(response))
            } else {
                locationsLiveData.postValue(Resource.Error("No internet connection"))
            }
        } catch (t: Throwable) {
            locationsLiveData.postValue(Resource.Error("Error occurred: ${t.localizedMessage}"))
        }
    }

    private suspend fun safeAPICallAssigningDetail(
        token: String,
        baseUrl: String,
        elvCode: String
    ) {
        assignedDetailTLLiveData.postValue(Resource.Loading())
        try {
            if (Utils.hasInternetConnection(getApplication())) {
                val response = utTellyRepository.getVehicleTLAssigningDetails(token, baseUrl, elvCode)
                if (response.isSuccessful) {
                    assignedDetailTLLiveData.postValue(Resource.Success(listOf(response.body()!!)))
                } else {
                    // Log or handle the status code and message
                    Log.e("TLFormPageViewModel", "Error: ${response.code()} - ${response.message()}")
                    assignedDetailTLLiveData.postValue(Resource.Error("Error: ${response.message()}"))
                }
            } else {
                assignedDetailTLLiveData.postValue(Resource.Error("No internet connection"))
            }
        } catch (e: Exception) {
            assignedDetailTLLiveData.postValue(Resource.Error("Error occurred: ${e.localizedMessage}"))
        }
    }

    private suspend fun safeSubmitTLassignPrintSlip(
        token: String,
        baseUrl: String,
        request: AssignTlSubmitRequest
    ) {
        submitTLResponseLiveData.postValue(Resource.Loading())
        try {
            if (Utils.hasInternetConnection(getApplication())) {
                val response = utTellyRepository.submitTLassignPrintSlip(token, baseUrl, request)
                submitTLResponseLiveData.postValue(handleSubmitTLResponse(response))
            } else {
                submitTLResponseLiveData.postValue(Resource.Error("No internet connection"))
            }
        } catch (t: Throwable) {
            submitTLResponseLiveData.postValue(Resource.Error("Error occurred: ${t.localizedMessage}"))
        }
    }
    private suspend fun safereprintReassignPrintSlipp(
        token: String,
        baseUrl: String,
        request: AssignTlSubmitRequest
    ) {
        reprintResponseLiveData.postValue(Resource.Loading())
        try {
            if (Utils.hasInternetConnection(getApplication())) {
                val response = utTellyRepository.reprintTellyCheckerSlip(token, baseUrl, request)
                reprintResponseLiveData.postValue(handleSubmitTLResponse(response))
            } else {
                reprintResponseLiveData.postValue(Resource.Error("No internet connection"))
            }
        } catch (t: Throwable) {
            reprintResponseLiveData.postValue(Resource.Error("Error occurred: ${t.localizedMessage}"))
        }
    }

private suspend fun safereAssignedPrintSlipp(
    token: String,
    baseUrl: String,
    request: AssignTlSubmitRequest
) {
    reAssignedResponseLiveData.postValue(Resource.Loading())
    try {
        if (Utils.hasInternetConnection(getApplication())) {
            val response = utTellyRepository.AassignedSubmitTellyChecker(token, baseUrl, request)
            reAssignedResponseLiveData.postValue(handleSubmitTLResponse(response))
        } else {
            reAssignedResponseLiveData.postValue(Resource.Error("No internet connection"))
        }
    } catch (t: Throwable) {
        reAssignedResponseLiveData.postValue(Resource.Error("Error occurred: ${t.localizedMessage}"))
    }
}

    ///Location-----------------------------------------DropDown-------------------------------------------------------------------------------
    private fun handleSubmitTLResponse(
        response: Response<AssignTlSubmitResponse>
    ): Resource<AssignTlSubmitResponse> {
        var errorMessage = "Unknown error"
        if (response.isSuccessful) {
            response.body()?.let { responseBody ->
                return Resource.Success(responseBody)
            }
        } else if (response.errorBody() != null) {
            try {
                val errorObject = JSONObject(response.errorBody()!!.charStream().readText())
                errorMessage = errorObject.optString("errorMessage", "Unknown error")
            } catch (e: Exception) {
                errorMessage = "Error parsing error response"
            }
        }
        return Resource.Error(errorMessage)
    }
    private fun handleLocationResponse(
        response: Response<LocationResponse>
    ): Resource<List<Location>> {
        var errorMessage = "Unknown error"
        if (response.isSuccessful) {
            response.body()?.let { responseBody ->
                return Resource.Success(responseBody.responseObject)
            }
        } else if (response.errorBody() != null) {
            try {
                val errorObject = JSONObject(response.errorBody()!!.charStream().readText())
                errorMessage = errorObject.optString("errorMessage", "Unknown error")
            } catch (e: Exception) {
                errorMessage = "Error parsing error response"
            }
        }
        return Resource.Error(errorMessage)
    }

    private fun handleAssigningDetailResponse(
        response: Response<AssigningDetailResponse>
    ): Resource<List<AssigningDetailResponse>> {
        var errorMessage = "Unknown error"
        return if (response.isSuccessful) {
            response.body()?.let { responseBody ->
                // Convert single object to list
                Resource.Success(listOf(responseBody))
            } ?: Resource.Error("Empty response body")
        } else {
            if (response.errorBody() != null) {
                try {
                    val errorObject = JSONObject(response.errorBody()!!.charStream().readText())
                    errorMessage = errorObject.optString("errorMessage", "Unknown error")
                } catch (e: Exception) {
                    errorMessage = "Error parsing error response"
                }
            }
            Resource.Error(errorMessage)
        }
    }

}


