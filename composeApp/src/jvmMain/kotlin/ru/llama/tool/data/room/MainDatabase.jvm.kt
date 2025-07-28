package ru.llama.tool.data.room

import androidx.room.Room
import androidx.room.RoomDatabase
import java.io.File

actual fun getDataBaseBuilder(): RoomDatabase.Builder<MainDatabase> {
    val dbFile = File("${System.getProperty("java.io.tmpdir")}/00LlamaTool", MAIN_DB_NAME_FILE)
    return Room.databaseBuilder<MainDatabase>(
        name = dbFile.absolutePath
    )
}