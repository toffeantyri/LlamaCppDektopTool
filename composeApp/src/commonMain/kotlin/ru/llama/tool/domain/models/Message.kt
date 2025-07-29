package ru.llama.tool.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class Message(
    val sender: EnumSender,
    val content: String,
    val id: Int,
    val error: String? = null,
    val dateTime: String
)