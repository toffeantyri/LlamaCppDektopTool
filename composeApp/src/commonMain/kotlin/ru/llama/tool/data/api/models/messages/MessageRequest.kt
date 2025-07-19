package ru.llama.tool.data.api.models.messages

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class MessageRequest(

    @Transient val id: Int = -1,
    val content: String,
    val role: String,
)