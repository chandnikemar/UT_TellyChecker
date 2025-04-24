package com.utmobile.uttellychecker.model.AssignTlSubmit

data class GetTLAssignVehicalDetailResponse(
    val statusCode: Int,
    val errorMessage: String?,
    val exception: String?,
    val responseMessage: String?,
    val responseObject: List<VehicleDetailAssign>
)

// Data class for the vehicle details within the response object
data class VehicleDetailAssign(
    val vrn: String,
    val vehicleTransactionId: Int,
    val vehicleTransactionCode: String,
    val driverName: String,
    val currentMilestone: String,
    val elvCode: String
)