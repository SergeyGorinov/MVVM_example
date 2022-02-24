package com.example.platform.network

import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import java.net.SocketTimeoutException
import java.net.UnknownHostException

internal class AndroidHttpClient(private val client: io.ktor.client.HttpClient) : HttpClient {

    override suspend fun request(params: HttpClient.RequestParams): HttpResult {
        return try {
            val response: HttpResponse = client.request {
                method = HttpMethod.parse(params.method.name)
                headers {
                    params.headers.forEach {
                        append(it.name, it.value)
                    }
                }
                url(params.url)
                params.body?.let { body ->
                    this.body = body
                }
                params.timeout?.let { timeout ->
                    this.timeout {
                        requestTimeoutMillis = timeout.toLong()
                    }
                }
            }
            HttpResult.Success(response)
        } catch (e: Exception) {
            when (e) {
                is SocketTimeoutException -> HttpResult.Error(HttpClient.Error.Timeout)
                is UnknownHostException -> HttpResult.Error(HttpClient.Error.BadUrl(params.url))
                is ResponseException -> HttpResult.Error(HttpClient.Error.BadStatus(e.response.status.value))
                else -> HttpResult.Error(HttpClient.Error.NetworkError)
            }
        }
    }
}