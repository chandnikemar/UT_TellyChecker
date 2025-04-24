package com.utmobile.uttellychecker.helper

import android.content.Context
import com.utmobile.uttellychecker.repository.UTTellyRespository
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.json.JSONObject

class TokenManager(context: Context) {

    private var token: String? = null
    private val mutex = Mutex() // For thread safety
    private val uttellyRepository = UTTellyRespository() // Passing the context to KYMSRepository


    // This function returns the current token, refreshing it if necessary
    suspend fun getToken(baseUrl: String): String? {
        mutex.withLock {
            if (token == null || isTokenExpired()) {
                refreshTokenIfNeeded(baseUrl)
            }
            return token
        }
    }

    // Refresh the token using the repository
    private suspend fun refreshTokenIfNeeded(baseUrl: String) {
        try {
            val newToken = uttellyRepository.refreshTokenIfNeeded(baseUrl, ) // Passing sessionManager

            // Store the new token
            token = newToken

            // Optionally print the new token for debugging
            println("Refreshed JWT Token: $token")
        } catch (e: Exception) {
            // Handle any errors that may occur during the token refresh
            println("Error refreshing token: ${e.message}, Cause: ${e.cause}")
        }
    }

    // This function checks if the token is expired based on its expiration time
    private fun isTokenExpired(): Boolean {
        token?.let {
            val parts = it.split(".")
            if (parts.size == 3) {
                // Decode JWT to get expiration time
                try {
                    val payload = String(android.util.Base64.decode(parts[1], android.util.Base64.URL_SAFE))
                    val json = JSONObject(payload)
                    val exp = json.optLong("exp", 0)
                    return System.currentTimeMillis() / 1000 >= exp // Compare expiration time in seconds
                } catch (e: Exception) {
                    // Handle any JSON or decoding errors
                    println("Error decoding JWT: ${e.message}")
                    return true
                }
            }
        }
        return true
    }
}

//// Global utility function to get a refreshed token
//suspend fun getRefreshedToken(context: Context, baseUrl: String): String? {
//
//    val kymsRepository = KYMSRepository() // Pass context to KYMSRepository
//
//    return try {
//        val newToken = kymsRepository.refreshTokenIfNeeded(baseUrl, ) // Pass sessionManager
//        println("Refreshed JWT Token: $newToken")
//        newToken
//    } catch (e: Exception) {
//        println("Error refreshing token: ${e.message}, Cause: ${e.cause}")
//        null
//    }
//}
