package ru.llama.tool.domain.use_cases

import ru.llama.tool.domain.models.AIDialogChatDto

interface ChatInteractor {

    suspend fun saveChatToDb(chat: AIDialogChatDto): Long

    suspend fun deleteChatFromDb(chatId: Long)

    suspend fun getDialogChat(chatId: Long): AIDialogChatDto

    suspend fun getAllChatsList(): List<AIDialogChatDto>

}