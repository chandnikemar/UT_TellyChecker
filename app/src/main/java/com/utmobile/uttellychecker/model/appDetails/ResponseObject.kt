package com.utmobile.uttellychecker.model.appDetails

data class ResponseObject(
    val apkFileUrl: String,
    val apkVersion: Int,
    val apkVersionDisplayString: String,
    val isMandatory: Boolean
)