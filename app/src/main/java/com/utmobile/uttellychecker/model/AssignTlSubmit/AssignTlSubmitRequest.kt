package com.utmobile.uttellychecker.model.AssignTlSubmit

data class AssignTlSubmitRequest(
    val ElvCode: String? = null,
    val VRN: String? = null,
    val TLCode: String? = null,
    val Reprint: Boolean? = null
)