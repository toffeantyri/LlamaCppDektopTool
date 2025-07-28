package ru.llama.tool.di

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import org.koin.dsl.module
import ru.llama.tool.core.io
import ru.llama.tool.data.room.MainDatabase
import ru.llama.tool.data.room.ai_chat_dao.AiChatDao
import ru.llama.tool.data.room.ai_properties_db.AiPropertiesDao
import ru.llama.tool.data.room.getDataBaseBuilder

val databaseModule = module {
    single<MainDatabase> {
        getDataBaseBuilder()
            //.addMigrations()
            .fallbackToDestructiveMigrationOnDowngrade(false)
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.io())
            .build()
    }


    single<AiPropertiesDao> {
        val db = get<MainDatabase>()
        db.getAiPropertiesDao()
    }

    single<AiChatDao> {
        val db = get<MainDatabase>()
        db.getAiChatDao()
    }

}