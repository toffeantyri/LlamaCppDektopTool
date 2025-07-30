package ru.llama.tool.domain.use_cases

import kotlinx.coroutines.flow.Flow
import ru.llama.tool.domain.models.AIDialogChatDto

interface ChatInteractor {

    suspend fun saveChatToDb(chat: AIDialogChatDto): Long

    suspend fun deleteChatFromDb(chatId: Long)

    suspend fun getDialogChat(chatId: Long): Flow<AIDialogChatDto>

    suspend fun getAllChatsList(): List<AIDialogChatDto>

    suspend fun renameChat(id: Long, newChatName: String)

}