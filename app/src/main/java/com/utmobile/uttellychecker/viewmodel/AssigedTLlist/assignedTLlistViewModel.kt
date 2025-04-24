package com.utmobile.uttellychecker.viewmodel.AssigedTLlist

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.utmobile.uttellychecker.helper.Resource
import com.utmobile.uttellychecker.helper.Utils
import com.utmobile.uttellychecker.model.AssignTlSubmit.GetTLAssignVehicalDetailResponse
import com.utmobile.uttellychecker.model.GetTLAssignedVehicleDetailResponse
import com.utmobile.uttellychecker.model.VehicleDetail
import com.utmobile.uttellychecker.model.AssignTlSubmit.VehicleDetailAssign
import com.utmobile.uttellychecker.repository.UTTellyRespository
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response

class AssignedTLlistViewModel(
    application: Application,
    private val utTellyRespository: UTTellyRespository
) : AndroidViewModel(application) {

    val assignedTLLiveData: MutableLiveData<Resource<List<VehicleDetail>>> = MutableLiveData()

    val assignTLLiveData: MutableLiveData<Resource<List<VehicleDetailAssign>>> = MutableLiveData()

    // Function to call API for assigned vehicles
    fun getAssignedVehicles(
        token: String,
        baseUrl: String
    ) {
        viewModelScope.launch {
            safeAPICallGetAssignedVehicles(token, baseUrl)
        }
    }

    fun getAssignVehicles(
        token: String,
        baseUrl: String
    ) {
        viewModelScope.launch {
            safeAPICallGetAssignVehicles(token, baseUrl)
        }
    }

    // Function to safely call the API and handle loading/error states
    private suspend fun safeAPICallGetAssignedVehicles(
        token: String,
        baseUrl: String
    ) {
        assignedTLLiveData.postValue(Resource.Loading())
        try {
            if (Utils.hasInternetConnection(getApplication())) {
                val response = utTellyRespository.getAssignedTLDetails(token, baseUrl)
                assignedTLLiveData.postValue(handleGetAssignedTLResponse(response))
            } else {
                assignedTLLiveData.postValue(Resource.Error("No Internet Connection"))
            }
        } catch (t: Throwable) {
            assignedTLLiveData.postValue(Resource.Error("Error occurred: ${t.localizedMessage}"))
        }
    }

    // Function to handle API response
    private fun handleGetAssignedTLResponse(
        response: Response<GetTLAssignedVehicleDetailResponse>
    ): Resource<List<VehicleDetail>> {
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


    ///////
    private suspend fun safeAPICallGetAssignVehicles(
        token: String,
        baseUrl: String
    ) {
        assignTLLiveData.postValue(Resource.Loading())
        try {
            if (Utils.hasInternetConnection(getApplication())) {
                val response = utTellyRespository.getAssignTLDetails(token, baseUrl)
                assignTLLiveData.postValue(handleGetAssignTLResponse(response))
            } else {
                assignTLLiveData.postValue(Resource.Error("No Internet Connection"))
            }
        } catch (t: Throwable) {
            assignTLLiveData.postValue(Resource.Error("Error occurred: ${t.localizedMessage}"))
        }
    }

    // Function to handle API response
    private fun handleGetAssignTLResponse(
        response: Response<GetTLAssignVehicalDetailResponse>
    ): Resource<List<VehicleDetailAssign>> {
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


}
