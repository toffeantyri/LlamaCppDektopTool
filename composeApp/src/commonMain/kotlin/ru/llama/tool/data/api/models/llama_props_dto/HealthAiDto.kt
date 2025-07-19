package ru.llama.tool.data.api.models.llama_props_dto

import kotlinx.serialization.Serializable

@Serializable
data class HealthAiDto(
    val status: String? = null
)
