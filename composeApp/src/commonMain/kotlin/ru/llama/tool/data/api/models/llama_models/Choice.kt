package ru.llama.tool.data.api.models.llama_models

import kotlinx.serialization.Serializable

@Serializable
data class Choice(
    val delta: Delta,
    val finish_reason: String? = null,
    val index: Int
)