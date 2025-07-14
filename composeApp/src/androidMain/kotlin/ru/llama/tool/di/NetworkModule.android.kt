package ru.llama.tool.di

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.defaultRequest
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.userAgent

internal actual fun getHttpClient(
    configure: (HttpClientConfig<*>) -> Unit
): HttpClient {
    return HttpClient(OkHttp) {
        defaultRequest {
            contentType(ContentType.Application.Json)
            userAgent("Android_KMP")
        }
        configure(this)
    }
}