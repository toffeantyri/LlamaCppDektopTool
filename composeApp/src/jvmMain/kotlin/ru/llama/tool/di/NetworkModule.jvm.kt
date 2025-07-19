package ru.llama.tool.di

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.sse.SSE
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.userAgent
import kotlin.time.Duration.Companion.seconds

internal actual fun getHttpClient(
    configure: (HttpClientConfig<*>) -> Unit
): HttpClient {
    return HttpClient(OkHttp) {
        install(SSE) {
            reconnectionTime = 60.seconds
        }
        defaultRequest {
//            url(baseUrl)
            contentType(ContentType.Application.Json)
            userAgent("user-agent")
        }
        configure(this)
    }
}