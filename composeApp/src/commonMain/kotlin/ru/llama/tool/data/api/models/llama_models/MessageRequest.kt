package ru.llama.tool.data.api.models.llama_models

import kotlinx.serialization.Serializable

@Serializable
data class MessageRequest(
    val id: Int,
    val content: String,
    val role: String
)