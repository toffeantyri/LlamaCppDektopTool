package ru.llama.tool.data.room

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import ru.llama.tool.data.room.ai_chat_dao.AiChatDao
import ru.llama.tool.data.room.ai_chat_dao.AiChatEntity
import ru.llama.tool.data.room.ai_chat_dao.ConverterMessage
import ru.llama.tool.data.room.ai_properties_db.AiPropertiesDao
import ru.llama.tool.data.room.ai_properties_db.AiPropertiesEntity

const val MAIN_DB_NAME_FILE = "MainDatabase.db"

@Database(entities = [AiPropertiesEntity::class, AiChatEntity::class], version = 1)
@TypeConverters(ConverterMessage::class)
@ConstructedBy(MainDatabaseConstructor::class)
abstract class MainDatabase : RoomDatabase() {

    abstract fun getAiPropertiesDao(): AiPropertiesDao

    abstract fun getAiChatDao(): AiChatDao

}

// The Room compiler generates the `actual` implementations.
@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object MainDatabaseConstructor : RoomDatabaseConstructor<MainDatabase> {
    override fun initialize(): MainDatabase
}


expect fun getDataBaseBuilder(): RoomDatabase.Builder<MainDatabase>

