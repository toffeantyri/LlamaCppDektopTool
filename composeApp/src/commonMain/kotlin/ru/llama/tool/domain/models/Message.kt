package ru.llama.tool.domain.models

data class Message(
    val sender: EnumSender,
    val content: String,
    val id: Int
)