package ru.llama.tool.data.api.setting_http_client_provider

class SettingHttpClientProviderImpl() : ISettingHttpClientProvider {

    override fun getBaseUrl(): String {
        return "http://127.0.0.1:8080/"
    }

}