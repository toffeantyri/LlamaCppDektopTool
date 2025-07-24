package ru.llama.tool.data.data_sources.local_ai_dialog_chat_data_source

import ru.llama.tool.data.room.ai_chat_dao.AiChatDao
import ru.llama.tool.data.room.ai_chat_dao.AiChatEntity

class AiDialogChatDataSourceImpl(private val dao: AiChatDao) : AiDialogChatDataSource {

    override suspend fun saveToDb(data: AiChatEntity) {
        println("saveToDb called with $data")
        return dao.insert(data)
    }

    override suspend fun deleteFromDb(id: Long) {
        return dao.delete(id)
    }

    override suspend fun getDataBy(id: Long): AiChatEntity? {
        return dao.getDataBy(id)
    }

    override suspend fun getAllChats(): List<AiChatEntity> {
        return dao.getAllChats()
    }
}