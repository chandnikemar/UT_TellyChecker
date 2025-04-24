package com.utmobile.uttellychecker.api


import com.utmobile.uttellychecker.helper.Constants
import com.utmobile.uttellychecker.model.AssignTlSubmit.AssignTlSubmitRequest
import com.utmobile.uttellychecker.model.AssignTlSubmit.AssignTlSubmitResponse
import com.utmobile.uttellychecker.model.AssigningDetailResponse
import com.utmobile.uttellychecker.model.AssignTlSubmit.GetTLAssignVehicalDetailResponse
import com.utmobile.uttellychecker.model.GetTLAssignedVehicleDetailResponse
import com.utmobile.uttellychecker.model.LocationResponse
import com.utmobile.uttellychecker.model.RefreshTokenResponse
import com.utmobile.uttellychecker.model.appDetails.GetAppDetailsResponse
import com.utmobile.uttellychecker.model.login.LoginRequest
import com.utmobile.uttellychecker.model.login.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface UTTELLYAPI {

    @GET(Constants.GET_APP_DETAILS)
    suspend fun getAppDetails(
    ): Response<GetAppDetailsResponse>


    @POST(Constants.LOGIN_URL)
    suspend fun login(
        @Body
        loginRequest: LoginRequest
    ): Response<LoginResponse>

    // Refresh token
    @POST(Constants.REFRESH_TOKEN_DATA)
    suspend fun refreshToken(
        @Body refreshToken: String
    ): Response<RefreshTokenResponse>


    // ASSigned List

    @GET(Constants.GET_TL_AssignedVehicleDetail)
    suspend fun getAssignedVehicleDetails(
        @Header(Constants.HTTP_HEADER_AUTHORIZATION) token: String,
    ): Response<GetTLAssignedVehicleDetailResponse>

    // Assign List
    @GET(Constants.GET_Vehicle_ListTo_AssignTL)
    suspend fun getAssignVehicleDetails(
        @Header(Constants.HTTP_HEADER_AUTHORIZATION) token: String,
    ): Response<GetTLAssignVehicalDetailResponse>


    @GET(Constants.GET_Location_list_on_Type)
    suspend fun getLocationListOnType(
        @Header(Constants.HTTP_HEADER_AUTHORIZATION) bearerToken: String,
        @Query("LocationType") locationType: String,
        @Query("RequestId") requestId: Int
    ): Response<LocationResponse>

    //Assign Submit
    @POST(Constants.GET_AssignTL_Submit)
    suspend fun updateTLassignPrintSlip(
        @Header(Constants.HTTP_HEADER_AUTHORIZATION) token: String,
        @Body request: AssignTlSubmitRequest
    ): Response<AssignTlSubmitResponse>


     // Assigned Submit

    @POST(Constants.POST_REPRINT_Assigned)
    suspend fun reprintTellyCheckerSlip(
        @Header(Constants.HTTP_HEADER_AUTHORIZATION) token: String,
        @Body request: AssignTlSubmitRequest
    ): Response<AssignTlSubmitResponse>


    @POST(Constants.POST_AssignedTL_Submit)
    suspend fun reAssignedTellyChecker(
        @Header(Constants.HTTP_HEADER_AUTHORIZATION) token: String,
        @Body request: AssignTlSubmitRequest
    ): Response<AssignTlSubmitResponse>


    @GET(Constants.GET_AssignTLDetail)
    suspend fun getAssigingDetail(
        @Header(Constants.HTTP_HEADER_AUTHORIZATION) bearerToken: String,
        @Query("elvCode") elvCode: String
    ): Response<AssigningDetailResponse>
}


