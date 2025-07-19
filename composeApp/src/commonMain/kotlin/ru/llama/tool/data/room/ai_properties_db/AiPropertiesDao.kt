package ru.llama.tool.data.room.ai_properties_db

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import ru.llama.tool.data.room.DBConst

@Dao
interface AiPropertiesDao {

    @Upsert
    suspend fun insert(data: AiPropertiesEntity)

    @Query("SELECT * FROM ${DBConst.PROPERTIES_TABLE} WHERE id = :id")
    suspend fun getDataBy(id: Int): AiPropertiesEntity

}