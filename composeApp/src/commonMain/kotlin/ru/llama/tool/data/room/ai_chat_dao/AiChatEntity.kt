package ru.llama.tool.data.room.ai_chat_dao

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import kotlinx.serialization.json.Json
import ru.llama.tool.core.EMPTY
import ru.llama.tool.data.room.DBConst
import ru.llama.tool.domain.models.Message


@Entity(tableName = DBConst.CHAT_TABLE)
data class AiChatEntity(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Long = 0,
    @ColumnInfo(name = "message") val messages: List<Message>,
    @ColumnInfo(name = "chat_name") val chatName: String = EMPTY,
    @ColumnInfo(name = "date") val date: String
)


class ConverterMessage {
    @TypeConverter
    fun fromMessageList(value: List<Message>): String {
        return Json.encodeToString(value)
    }

    @TypeConverter
    fun toMessageList(value: String): List<Message> {
        return Json.decodeFromString(value)
    }
}