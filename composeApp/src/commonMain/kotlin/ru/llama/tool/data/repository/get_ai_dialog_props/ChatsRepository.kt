package ru.llama.tool.data.repository.get_ai_dialog_props

import ru.llama.tool.domain.models.AIDialogChatDto

interface ChatsRepository {

    suspend fun saveChatToDb(chat: AIDialogChatDto)

    suspend fun deleteChatFromDb(chatId: Int)

    suspend fun getDialogChat(chatId: Int): AIDialogChatDto

    suspend fun getAllChatsList(): List<AIDialogChatDto>

}