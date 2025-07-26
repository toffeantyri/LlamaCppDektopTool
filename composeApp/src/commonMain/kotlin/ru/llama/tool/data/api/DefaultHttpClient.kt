package ru.llama.tool.data.api

import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.client.plugins.sse.SSE
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.AttributeKey
import kotlinx.serialization.json.Json
import kotlin.time.Duration.Companion.seconds

private const val DEFAULT_REQUEST_TIMEOUT = 180_000L
private const val DEFAULT_TIMEOUT = 90_000L

fun configureHttpClient(
    enableNetworkLogs: Boolean,
): (HttpClientConfig<*>) -> Unit = { httpClientConfig ->
    with(httpClientConfig) {

        install(SSE) {
            reconnectionTime = 60.seconds
        }

        install(HttpTimeout) {
            requestTimeoutMillis = Long.MAX_VALUE
            connectTimeoutMillis = DEFAULT_TIMEOUT
            socketTimeoutMillis = Long.MAX_VALUE
        }

        expectSuccess = true
        install(ContentNegotiation) {
            json(json = Json {
                prettyPrint = true
                ignoreUnknownKeys = true
                explicitNulls = true
            })
        }
        if (enableNetworkLogs) {
            install(Logging) {
                logger = Logger.SIMPLE
                level = LogLevel.ALL

                filter {
                    !it.attributes.contains(AttributeKey<Boolean>(ApiService.DISABLE_LOG))
                }
            }
        }


    }
}