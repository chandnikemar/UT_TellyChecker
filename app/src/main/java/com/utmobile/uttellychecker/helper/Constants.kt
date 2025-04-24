package com.utmobile.uttellychecker.helper

object Constants {

    val LONGITUDE: String = "longitude"
    val LATITUDE: String = "latitude"
    val ModelCode: String = "modelcode"
    val ColorCode: String = "colorcode"
    val VinNo: String = "vinno"
    const val KEY_USER_ID = "id"
    const val LOGGEDIN = "loggedIn"
    const val IS_ADMIN = "isAdmin"
    const val USERNAME = "username"
    const val TOKEN = "token"
    const val USER_COORDINATES = "coordinates"
    const val NO_INTERNET = "No Internet Connection"
    const val NETWORK_FAILURE = "Network Failure"
    const val CONFIG_ERROR = "Please configure network details"
    const val INCOMPLETE_DETAILS = "Please fill the required details"
    const val EXCEPTION_ERROR = "No Data Found"
    const val HTTP_ERROR_MESSAGE = "message"
    const val HTTP_HEADER_AUTHORIZATION = "Authorization"
    const val SHARED_PREF = "mahindra_yard_shared_pref"
    const val SERVER_IP = "server_ip"

    const val KEY_PRINTER_MAC = "printerMac"

    //const val KEY_USERID = Constants.USER_ID
    const val KEY_IS_ADMIN = "isAdmin"
    const val ISFIRSTTIME = "is_first_time"
    const val SESSION_EXPIRE = "Session Expired ! Please relogin"
    const val KEY_USER_NAME = "userName"
    const val KEY_JWT_TOKEN = "jwtToken"
    const val KEY_IS_LOGGED_IN = "loggedIn"
    const val KEY_COORDINATE = "location_coordinates"
    const val SERVER_IP_SHARED = "192.168.1.105"
    const val KEY_HTTP = "http"
    const val KEY_SERVER_IP = "serverIp"
    const val GET = 1
    const val POST = 2
    const val HTTP_OK = 200
    const val HTTP_CREATED = 201
    const val HTTP_EXCEPTION = 202
    const val HTTP_UPDATED = 204
    const val HTTP_FOUND = 302
    const val HTTP_NOT_FOUND = 404
    const val HTTP_CONFLICT = 409
    const val HTTP_INTERNAL_SERVER_ERROR = 500
    const val HTTP_ERROR = 400

    const val REFRESH_TOKEN_DATA = "UserManagement/refreshtoken"
    const val DASHBOARD_DATA = "VehicleMilestone/GetVehicleMilestoneDashBoardData"
    const val GET_APP_DETAILS = "MobileApp/GetLatestApkVersion"
    const val DASHBOARD_GRAPH_DATA = "VehicleMilestone/GetDriverwiseVehicleParkedCount"
    const val ADD_LOCATIONS = "LocationMapping/AddLocations"
    const val ADD_DEALERLOCATIONS = "Dealer/AddDealer"
    const val GET_DEALERLOCATIONS = "Dealer/GetAllDealers"
    const val GENERATE_TOKEN = "UserManagement/authenticate"
    const val GET_PARENT_LOCATION = "LocationMapping/GetParentLocation"
    const val GET_YARD_LOCATION = "LocationMapping/GetYardLocation"
    const val LOGIN_URL = "UserManagement/authenticate"



    // const val BASE_URL = "http://192.168.1.23:5000/api/"
    //const val BASE_URL = "http://192.168.1.205:8011/service/api/"
    // const val BASE_URL = "http://103.240.90.141:5050/Service/api/"
   // const val BASE_URL_LOCAL = "http://103.240.90.141:5050/Service/api/"
    const val baseUrl = "http://192.168.1.205:8088/service/api/"
    const val GET_TL_AssignedVehicleDetail = "MobileApp/GetTLAssignedVehicleDetail"
    const val GET_Vehicle_ListTo_AssignTL = "MobileApp/GetVehicleListToAssignTL"
    const val  GET_Location_list_on_Type="MobileApp/GetLocationListOnType"
    const val  GET_AssignTL_Submit="MobileApp/UpdateTLAssignPrintSlip"
    const val GET_AssignTLDetail="MobileApp/GetVehicleTLAssigningDetailsAsync"

    //Assigned
    const val  POST_AssignedTL_Submit="MobileApp/UpdateTLReassignPrintSlip"
    const val POST_REPRINT_Assigned="MobileApp/ReprintTellyCheckerSlip"

    //change password
    const val CHANGE_PASSWORD = "UserManagement/change-password"


    const val LOCATION_ID = "locationId"

    //const val BASE_URL = "http://192.168.1.14:5000/api/"
    //const val BASE_URL = "http://103.240.90.141:7001/Service/api/"
    const val BASE_URL = "http://103.240.90.141:5050/Service/api/"
    //const val BASE_URL = "http://rfid-yard-lb-1652367993.ap-south-1.elb.amazonaws.com:82/api/"
    //const val BASE_URL = "http://rfid-yard-lb-1652367993.ap-south-1.elb.amazonaws.com:82/api/"

}