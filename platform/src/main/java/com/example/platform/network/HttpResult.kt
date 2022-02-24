package com.example.platform.network

import io.ktor.client.statement.*

sealed class HttpResult {

    data class Success(val response: HttpResponse) : HttpResult()
    data class Error(val error: HttpClient.Error) : HttpResult()
}