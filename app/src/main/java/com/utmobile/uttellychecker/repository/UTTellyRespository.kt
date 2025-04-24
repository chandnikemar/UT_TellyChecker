package com.utmobile.uttellychecker.repository

import com.utmobile.uttellychecker.api.RetrofitInstance
import com.utmobile.uttellychecker.helper.Constants
import com.utmobile.uttellychecker.helper.SessionManager
import com.utmobile.uttellychecker.model.AssignTlSubmit.AssignTlSubmitRequest
import com.utmobile.uttellychecker.model.AssignTlSubmit.AssignTlSubmitResponse
import com.utmobile.uttellychecker.model.AssigningDetailResponse
import com.utmobile.uttellychecker.model.AssignTlSubmit.GetTLAssignVehicalDetailResponse
import com.utmobile.uttellychecker.model.GetTLAssignedVehicleDetailResponse
import com.utmobile.uttellychecker.model.LocationResponse
import com.utmobile.uttellychecker.model.login.LoginRequest
import retrofit2.Response
import retrofit2.http.Header

class UTTellyRespository {
    private lateinit var sessionManager: SessionManager
    suspend fun getAppDetails(
        baseUrl: String
    ) = RetrofitInstance.api(baseUrl).getAppDetails()


    suspend fun login(
        baseUrl: String,
        loginRequest: LoginRequest
    ) = RetrofitInstance.api(baseUrl).login(loginRequest)

    suspend fun getAssignedTLDetails(
        @Header(Constants.HTTP_HEADER_AUTHORIZATION)
        bearerToken: String,
        baseUrl: String
    ): Response<GetTLAssignedVehicleDetailResponse> {
        return RetrofitInstance.api(baseUrl).getAssignedVehicleDetails(bearerToken)
    }


    suspend fun getAssignTLDetails(
        @Header(Constants.HTTP_HEADER_AUTHORIZATION)
        bearerToken: String,
        baseUrl: String
    ): Response<GetTLAssignVehicalDetailResponse> {
        return RetrofitInstance.api(baseUrl).getAssignVehicleDetails(bearerToken)
    }


    suspend fun getTruckLoaderLocations(
        bearerToken: String,
        baseUrl: String,
        locationType: String,
        requestId: Int
    ): Response<LocationResponse> {
        return RetrofitInstance.api(baseUrl).getLocationListOnType(
            bearerToken,
            locationType,
            requestId
        )
    }

    suspend fun submitTLassignPrintSlip(
        bearerToken: String,
        baseUrl: String,
        request: AssignTlSubmitRequest
    ): Response<AssignTlSubmitResponse> {
        return RetrofitInstance.api(baseUrl).updateTLassignPrintSlip(
            bearerToken,
            request
        )
    }
    suspend fun reprintTellyCheckerSlip(
        token: String,
        baseUrl: String,
        request: AssignTlSubmitRequest
    ): Response<AssignTlSubmitResponse> {
        return RetrofitInstance.api(baseUrl).reprintTellyCheckerSlip(token, request)
    }
    suspend fun AassignedSubmitTellyChecker(
        token: String,
        baseUrl: String,
        request: AssignTlSubmitRequest
    ): Response<AssignTlSubmitResponse> {
        return RetrofitInstance.api(baseUrl).reAssignedTellyChecker(token, request)
    }

    suspend fun getVehicleTLAssigningDetails(
        token: String,
        baseUrl: String,
        elvCode: String
    ): Response<AssigningDetailResponse> {
        return RetrofitInstance.api(baseUrl).getAssigingDetail(token, elvCode)
    }


    suspend fun refreshTokenIfNeeded(baseUrl: String): String {
        val refreshToken = sessionManager.getRefreshToken() // Get refresh token from SessionManager


        if (refreshToken.isNullOrEmpty()) {
            throw Exception("Refresh token is missing or expired.")
        }

        return try {
            val response = RetrofitInstance.api(baseUrl).refreshToken(refreshToken)
            if (response.isSuccessful) {
                val jwtToken = response.body()?.jwtToken
                if (jwtToken != null) {
                    sessionManager.saveJwtToken(jwtToken)  // Save the new JWT token in the session manager
                    jwtToken
                } else {
                    throw Exception("Failed to get new JWT token")
                }
            } else {
                throw Exception("Refresh token request failed with status code: ${response.code()}")
            }
        } catch (e: Exception) {
            throw Exception("Error during token refresh: ${e.message}")
        }
    }
}