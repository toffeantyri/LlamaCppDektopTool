package ru.llama.tool.di

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.plugin
import io.ktor.client.plugins.sse.SSE
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module
import ru.llama.tool.data.api.ApiService
import ru.llama.tool.data.api.ApiServiceImpl
import ru.llama.tool.data.api.configureHttpClient
import ru.llama.tool.di.utils.DEFAULT_HTTP_CLIENT


val networkModule: (enableNetworkLogs: Boolean) -> Module =
    { enableNetworkLogs ->
        module {
            single<HttpClient>(named(DEFAULT_HTTP_CLIENT)) {
                getHttpClient(
                    configureHttpClient(enableNetworkLogs)
                ).apply {
                    plugin(SSE)
                }
            }

            single<ApiService> {
                ApiServiceImpl(
                    client = get(named(DEFAULT_HTTP_CLIENT)),
                    pref = get()
                )
            }

        }
    }

internal expect fun getHttpClient(
    configure: (HttpClientConfig<*>) -> Unit
): HttpClient
