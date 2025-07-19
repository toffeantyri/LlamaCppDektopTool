package ru.llama.tool.data.api.setting_http_client_provider

import ru.llama.tool.domain.models.AiProperties

class SettingHttpClientProviderImpl : ISettingHttpClientProvider {

    override fun getBaseUrl(): String {
        return "http://127.0.0.1:8080/"
    }

    override fun getRequestSetting(): AiProperties {
        return AiProperties()
    }
}