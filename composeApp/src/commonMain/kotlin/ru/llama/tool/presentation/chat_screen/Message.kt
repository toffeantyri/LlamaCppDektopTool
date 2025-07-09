package ru.llama.tool.presentation.chat_screen

data class Message(
    val content: String,
    val sender: Sender
)

enum class Sender {
    User,
    AI
} 