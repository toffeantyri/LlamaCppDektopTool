package ru.llama.tool.data.data_sources.local_ai_dialog_chat_data_source

import kotlinx.coroutines.flow.Flow
import ru.llama.tool.data.room.ai_chat_dao.AiChatEntity

interface AiDialogChatDataSource {

    suspend fun saveToDb(data: AiChatEntity): Long

    suspend fun deleteFromDb(id: Long)

    suspend fun getDataBy(id: Long): Flow<AiChatEntity?>

    suspend fun getAllChats(): List<AiChatEntity>

    suspend fun renameChat(id: Long, newChatName: String)

}