package com.example.platform.network

interface HttpClient {

    suspend fun request(params: RequestParams): HttpResult

    data class Header(
        val name: String,
        val value: String
    )

    data class RequestParams(
        val method: Method,
        val headers: List<Header> = listOf(),
        val url: String,
        val body: String? = null,
        val timeout: Float? = null
    )

    sealed class Error {
        object Timeout : Error()
        data class BadUrl(val url: String) : Error()
        object NetworkError : Error()
        data class BadStatus(val status: Int) : Error()
        data class BadBody(val body: String) : Error()
    }


    enum class Method {
        GET,
        POST,
        PUT,
        DELETE,
        PATCH,
        OPTIONS
    }
}