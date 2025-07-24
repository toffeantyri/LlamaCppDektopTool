package ru.llama.tool.data.room.ai_chat_dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import ru.llama.tool.data.room.DBConst

@Dao
interface AiChatDao {

    @Upsert
    suspend fun insert(data: AiChatEntity): Long

    @Query("DELETE FROM ${DBConst.CHAT_TABLE} WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("SELECT * FROM ${DBConst.CHAT_TABLE} WHERE id = :id")
    suspend fun getDataBy(id: Long): AiChatEntity?

    @Query("SELECT * FROM ${DBConst.CHAT_TABLE}")
    suspend fun getAllChats(): List<AiChatEntity>

}