package ru.llama.tool.domain.use_cases

import ru.llama.tool.domain.models.AIDialogChatDto

interface ChatInteractor {

    suspend fun saveChatToDb(chat: AIDialogChatDto)

    suspend fun deleteChatFromDb(chatId: Int)

    suspend fun getDialogChat(chatId: Int): AIDialogChatDto

    suspend fun getAllChatsList(): List<AIDialogChatDto>

}