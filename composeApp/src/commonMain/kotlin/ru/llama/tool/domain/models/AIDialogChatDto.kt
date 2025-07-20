package ru.llama.tool.domain.models

data class AIDialogChatDto(
    val chatId: Int,
    val chatName: String,
    val messages: List<Message>
)