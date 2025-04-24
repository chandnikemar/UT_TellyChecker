package com.utmobile.uttellychecker.model

data class GetTLAssignedVehicleDetailResponse(
    val statusCode: Int,
    val errorMessage: String?,
    val exception: String?,
    val responseMessage: String?,
    val responseObject: List<VehicleDetail>
)

data class VehicleDetail(
    val vrn: String,
    val vehicleTransactionId: Int,
    val vehicleTransactionCode: String,
    val driverName: String,
    val currentMilestone: String,
    val elvCode: String
)
