package ru.llama.tool.data.room.ai_properties_db

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import ru.llama.tool.data.room.DBConst

@Dao
interface AiPropertiesDao {

    @Upsert
    suspend fun insert(data: AiPropertiesEntity)

    @Query("DELETE FROM ${DBConst.PROPERTIES_TABLE} WHERE id = :id")
    suspend fun delete(id: Int)

    @Query("SELECT * FROM ${DBConst.PROPERTIES_TABLE} WHERE id = :id")
    fun getDataBy(id: Int): Flow<AiPropertiesEntity?>

}