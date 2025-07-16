package ru.llama.tool.data.room

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import ru.llama.tool.data.room.ai_properties_db.AiPropertiesDao
import ru.llama.tool.data.room.ai_properties_db.AiPropertiesEntity

const val MAIN_DB_NAME_FILE = "MainDatabase.db"

@Database(entities = [AiPropertiesEntity::class], version = 1)
@ConstructedBy(MainDatabaseConstructor::class)
abstract class MainDatabase : RoomDatabase() /*DB*/ {

    abstract fun getAiPropertiesDao(): AiPropertiesDao

//    override fun clearAllTables() {
//        super.clearAllTables()
//    }


}
//
//// FIXME: Added a hack to resolve below issue:
//// Class 'MainDatabase_Impl' is not abstract and does not implement abstract base class member 'clearAllTables'.
//interface DB {
//    fun clearAllTables() {}
//}

// The Room compiler generates the `actual` implementations.
@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object MainDatabaseConstructor : RoomDatabaseConstructor<MainDatabase> {
    override fun initialize(): MainDatabase
}


expect fun getDataBaseBuilder(): RoomDatabase.Builder<MainDatabase>

