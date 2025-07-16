package ru.llama.tool.data.room

import androidx.room.Room
import androidx.room.RoomDatabase
import ru.llama.tool.App

actual fun getDataBaseBuilder(): RoomDatabase.Builder<MainDatabase> {
    val appContext = App.appContext
    val dbFile = appContext.getDatabasePath(MAIN_DB_NAME_FILE)
    return Room.databaseBuilder<MainDatabase>(
        context = appContext,
        name = dbFile.absolutePath
    )
}
