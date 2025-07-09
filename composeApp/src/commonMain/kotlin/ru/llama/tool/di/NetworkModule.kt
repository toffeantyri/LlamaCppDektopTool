package ru.llama.tool.di

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module
import ru.llama.tool.data.api.configureHttpClient


val networkModule: (enableNetworkLogs: Boolean) -> Module =
    { enableNetworkLogs ->
        module {
            single<HttpClient>(named(DEFAULT_HTTP_CLIENT)) {

                getHttpClient(
                    configureHttpClient(enableNetworkLogs)
                )
            }

        }
    }

internal expect fun getHttpClient(
    configure: (HttpClientConfig<*>) -> Unit
): HttpClient
