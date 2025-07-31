package ru.llama.tool.data.data_sources.local_ai_dialog_chat_data_source

import kotlinx.coroutines.flow.Flow
import ru.llama.tool.data.room.ai_chat_dao.AiChatDao
import ru.llama.tool.data.room.ai_chat_dao.AiChatEntity

class AiDialogChatDataSourceImpl(private val dao: AiChatDao) : AiDialogChatDataSource {

    override suspend fun saveToDb(data: AiChatEntity): Long {
        val result = dao.insert(data)
        return if (result != -1L) result else throw Throwable("Saving $data error")
    }

    override suspend fun deleteFromDb(id: Long) {
        return dao.delete(id)
    }

    override suspend fun getDataBy(id: Long): Flow<AiChatEntity?> {
        return dao.getDataBy(id)
    }

    override suspend fun getAllChats(): List<AiChatEntity> {
        return dao.getAllChats()
    }

    override suspend fun renameChat(id: Long, newChatName: String) {
        return dao.renameChat(id, newChatName)
    }
}