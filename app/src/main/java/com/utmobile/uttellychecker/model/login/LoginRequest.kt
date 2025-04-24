package com.utmobile.uttellychecker.model.login

data class LoginRequest(
    val DeviceId: String,
    val password: String,
    val userName: String
)
