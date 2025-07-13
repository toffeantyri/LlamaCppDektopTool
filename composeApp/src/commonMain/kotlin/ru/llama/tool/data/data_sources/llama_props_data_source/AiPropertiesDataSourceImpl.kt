package ru.llama.tool.data.data_sources.llama_props_data_source

import ru.llama.tool.data.api.ApiService
import ru.llama.tool.data.api.models.llama_props_dto.LlamaProperties

class AiPropertiesDataSourceImpl(private val api: ApiService) : IAiPropertiesDataSource {

    override suspend fun getLLamaProperties(): LlamaProperties {
        return api.getModelProperties()
    }
}