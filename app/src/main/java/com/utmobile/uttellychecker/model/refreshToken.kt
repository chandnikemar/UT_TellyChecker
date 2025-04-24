package com.utmobile.uttellychecker.model

class RefreshTokenResponse(
    val id: Number,
    val userName: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val mobileNumber: String,
    val cardNo: Any?,
    val roleName: String,
    val isVerified: Boolean,
    val jwtToken: String,
    val refreshToken: String,
    val userRFID: Any?,
    val allSuperVisors: Any?,
    val userAccess: List<UserAccess>
) {
    companion object {
        fun fromJson(json: Map<String, Any?>): RefreshTokenResponse {
            return RefreshTokenResponse(
                id = json["id"] as Number,
                userName = json["userName"] as String,
                firstName = json["firstName"] as String,
                lastName = json["lastName"] as String,
                email = json["email"] as String,
                mobileNumber = json["mobileNumber"] as String,
                cardNo = json["cardNo"],
                roleName = json["roleName"] as String,
                isVerified = json["isVerified"] as Boolean,
                jwtToken = json["jwtToken"] as String,
                refreshToken = json["refreshToken"] as String,
                userRFID = json["userRFID"],
                allSuperVisors = json["allSuperVisors"],
                userAccess = (json["userAccess"] as List<*>?)?.map {
                    UserAccess.fromJson(it as Map<String, Any?>)
                } ?: emptyList()
            )
        }
    }

    fun toJson(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "userName" to userName,
            "firstName" to firstName,
            "lastName" to lastName,
            "email" to email,
            "mobileNumber" to mobileNumber,
            "cardNo" to cardNo,
            "roleName" to roleName,
            "isVerified" to isVerified,
            "jwtToken" to jwtToken,
            "refreshToken" to refreshToken,
            "userRFID" to userRFID,
            "allSuperVisors" to allSuperVisors,
            "userAccess" to userAccess.map { it.toJson() }
        )
    }
}
class UserAccess(
    val screenCode: String,
    val canRead: Boolean,
    val canCreate: Boolean,
    val canUpdate: Boolean,
    val canDeactivate: Boolean
) {
    companion object {
        fun fromJson(json: Map<String, Any?>): UserAccess {
            return UserAccess(
                screenCode = json["screenCode"] as String,
                canRead = json["canRead"] as Boolean,
                canCreate = json["canCreate"] as Boolean,
                canUpdate = json["canUpdate"] as Boolean,
                canDeactivate = json["canDeactivate"] as Boolean
            )
        }
    }

    fun toJson(): Map<String, Any?> {
        return mapOf(
            "screenCode" to screenCode,
            "canRead" to canRead,
            "canCreate" to canCreate,
            "canUpdate" to canUpdate,
            "canDeactivate" to canDeactivate
        )
    }
}
class RefreshTokenRequest(
    val Token: String
)