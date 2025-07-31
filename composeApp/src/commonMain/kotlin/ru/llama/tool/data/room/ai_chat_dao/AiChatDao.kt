package ru.llama.tool.data.room.ai_chat_dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.llama.tool.data.room.DBConst

@Dao
interface AiChatDao {

    //@Upsert
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(data: AiChatEntity): Long

    @Query("DELETE FROM ${DBConst.CHAT_TABLE} WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("SELECT * FROM ${DBConst.CHAT_TABLE} WHERE id = :id")
    fun getDataBy(id: Long): Flow<AiChatEntity?>

    @Query("SELECT * FROM ${DBConst.CHAT_TABLE}")
    suspend fun getAllChats(): List<AiChatEntity>

    @Query("UPDATE ${DBConst.CHAT_TABLE} SET chat_name = :newName WHERE id = :id")
    suspend fun renameChat(id: Long, newName: String)

}