package ru.llama.tool.domain.models

data class AIDialogChatDto(
    val chatId: Long,
    val chatName: String,
    val messages: List<Message>
)