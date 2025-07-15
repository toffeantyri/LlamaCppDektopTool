package ru.llama.tool.data.api.setting_http_client_provider

import ru.llama.tool.domain.models.AiProperties

interface ISettingHttpClientProvider {

    fun getBaseUrl(): String

    fun getRequestSetting(): AiProperties

}