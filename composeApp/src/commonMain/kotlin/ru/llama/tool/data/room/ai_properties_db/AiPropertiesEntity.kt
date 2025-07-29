package ru.llama.tool.data.room.ai_properties_db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.llama.tool.data.room.DBConst

@Entity(tableName = DBConst.PROPERTIES_TABLE)
data class AiPropertiesEntity(
    @PrimaryKey(autoGenerate = false) @ColumnInfo(name = "id") val id: Long,
    @ColumnInfo(name = "system_prompt") val systemPrompt: String,
    @ColumnInfo(name = "temperature") val temperature: Double,
    @ColumnInfo(name = "max_tokens") val maxTokens: Int,
    @ColumnInfo(name = "top_p") val topP: Double
)