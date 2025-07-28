package ru.llama.tool.data.repository.ai_props

import ru.llama.tool.domain.models.LLamaPropsDto

interface IAiPropertiesRepository {

    suspend fun getAiProperties(): LLamaPropsDto

}