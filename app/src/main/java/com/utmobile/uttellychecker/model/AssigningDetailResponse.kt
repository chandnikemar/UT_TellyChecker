package com.utmobile.uttellychecker.model

data class AssigningDetailResponse(
    val statusCode: Int,
    val errorMessage: String?,
    val exception: String?,
    val responseMessage: String,
    val responseObject: AssigningDetailData
)

data class AssigningDetailData(
    val currentDate: String?,                    // assuming it's a date/time string or null
    val shiftDetail: String?,
    val vrn: String,
    val bagQuantity: String,
    val materialType: String,
    val itemDescription: String,
    val packer: String?,
    val truckLoader: String?,
    val tranSeq: String?,
    val mrp: String,
    val productWeight: String,
    val loadingMilestoneTranCode: String?
)
