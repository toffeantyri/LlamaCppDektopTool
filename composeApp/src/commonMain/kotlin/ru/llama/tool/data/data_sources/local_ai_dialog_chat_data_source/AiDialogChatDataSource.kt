package ru.llama.tool.data.data_sources.local_ai_dialog_chat_data_source

import ru.llama.tool.data.room.ai_chat_dao.AiChatEntity

interface AiDialogChatDataSource {

    suspend fun saveToDb(data: AiChatEntity)

    suspend fun deleteFromDb(id: Int)

    suspend fun getDataBy(id: Int): AiChatEntity?

    suspend fun getAllChats(): List<AiChatEntity>

}