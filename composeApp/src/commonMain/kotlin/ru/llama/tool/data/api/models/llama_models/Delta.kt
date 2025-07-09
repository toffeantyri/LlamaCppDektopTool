package ru.llama.tool.data.api.models.llama_models

import kotlinx.serialization.Serializable

@Serializable
data class Delta(
    val content: String? = null,
    val role: String? = null
)