package ru.llama.tool.data.api

import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

private const val DEFAULT_REQUEST_TIMEOUT = 35_000L
private const val DEFAULT_TIMEOUT = 35_000L

fun configureHttpClient(
    enableNetworkLogs: Boolean,
): (HttpClientConfig<*>) -> Unit = { httpClientConfig ->
    with(httpClientConfig) {
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
            }
        }

        install(HttpTimeout) {
            requestTimeoutMillis = DEFAULT_REQUEST_TIMEOUT
            connectTimeoutMillis = DEFAULT_TIMEOUT
            socketTimeoutMillis = DEFAULT_TIMEOUT
        }

    }
}