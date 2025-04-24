package  com.utmobile.uttellychecker.model.appDetails

data class GetAppDetailsResponse(
    val errorMessage: Any,
    val exception: Any,
    val responseMessage: Any,
    val responseObject: ResponseObject,
    val statusCode: Int
)