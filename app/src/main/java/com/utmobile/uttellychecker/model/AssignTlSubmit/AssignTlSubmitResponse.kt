package com.utmobile.uttellychecker.model.AssignTlSubmit


data class AssignTlSubmitResponse(
    val statusCode: Int,
    val errorMessage: String?,
    val exception: String?,
    val responseMessage: String?,
    val responseObject: TLResponseData?
)

data class TLResponseData(
    val currentDate: String,
    val shiftDetail: String,
    val vrn: String,
    val bagQuantity: String,
    val typeOfBags: String,
    val itemDescription: String,
    val packer: String,
    val truckLoader: String,
    val tranSeq: String,
    val mrp: String?,
    val prnContentDetail:String?
)