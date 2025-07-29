package ru.llama.tool.data.repository.get_ai_dialog_props

import kotlinx.coroutines.flow.Flow
import ru.llama.tool.domain.models.AIDialogChatDto

interface ChatsRepository {

    suspend fun saveChatToDb(chat: AIDialogChatDto): Long

    suspend fun deleteChatFromDb(chatId: Long)

    suspend fun getDialogChat(chatId: Long): Flow<AIDialogChatDto>

    suspend fun getAllChatsList(): List<AIDialogChatDto>

}