package ru.llama.tool.data.api.models.llama_models

import kotlinx.serialization.Serializable

@Serializable
data class LlamaResponseDto(
    val choices: List<Choice>,
    val created: Int,
    val id: String,
    val model: String,
    val `object`: String,
    val system_fingerprint: String
)