package ru.llama.tool.data.data_sources.llama_props_data_source

import ru.llama.tool.data.api.models.llama_props_dto.LlamaProperties

interface IAiPropertiesDataSource {

    suspend fun getLLamaProperties(): LlamaProperties

}