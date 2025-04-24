package com.utmobile.uttellychecker.model

data class LocationResponse(
    val statusCode: Int,
    val errorMessage: String?,
    val exception: String?,
    val responseMessage: String?,
    val responseObject: List<Location>
)

data class Location(
    val locationCode: String,
    val locationName: String,
    val locationType: String,
    val parentLocationCode: String,
    val parentLocationName: String,
    val parentLocationType: String
)
