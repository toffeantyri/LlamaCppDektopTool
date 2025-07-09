package ru.llama.tool.domain.models

data class Message(
    val content: String,
    val sender: EnumSender
)