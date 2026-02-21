package com.dnfapps.arrmatey.arr.api.client

import com.dnfapps.arrmatey.client.NetworkResult
import com.dnfapps.arrmatey.client.safeGet
import io.ktor.client.HttpClient
import io.ktor.client.request.header

class GenericClient(
    httpClientFactory: HttpClientFactory
) {

    private val httpClient: HttpClient = httpClientFactory.createGeneric()

    suspend fun test(endpoint: String, apiKey: String, apiBase: String): Boolean {
        try {
            val response = httpClient.safeGet<Any>("${endpoint.trimEnd('/')}/$apiBase/system/status") {
                header("X-Api-Key", apiKey)
            }
            return response is NetworkResult.Success
        } catch (e: Exception) {
            return false
        }
    }
}
