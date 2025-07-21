package ru.llama.tool.di

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.defaultRequest
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.userAgent
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import okhttp3.Protocol
import java.util.concurrent.TimeUnit

internal actual fun getHttpClient(
    configure: (HttpClientConfig<*>) -> Unit
): HttpClient {
    return HttpClient(OkHttp) {
        defaultRequest {
            contentType(ContentType.Application.Json)
            userAgent("jvm-agent")
        }

        engine {
            // Правильная настройка OkHttp клиента
            preconfigured = OkHttpClient.Builder()
                .retryOnConnectionFailure(true)
                .connectTimeout(90, TimeUnit.SECONDS)
                .readTimeout(0, TimeUnit.SECONDS) // Важно для SSE!
                .writeTimeout(0, TimeUnit.SECONDS)
                .connectionPool(ConnectionPool(5, 60, TimeUnit.SECONDS))
                .protocols(listOf(Protocol.HTTP_1_1)) // Явно указываем HTTP/1.1
                .build()
        }

        configure(this)
    }
}