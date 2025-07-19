package ru.llama.tool.data.repository.ai_props

import ru.llama.tool.domain.models.AiProperties

interface IAiPropertiesRepository {

    suspend fun getAiProperties(): AiProperties

}