package ru.llama.tool.di

import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.Module
import org.koin.dsl.module
import ru.llama.tool.data.io.GGUFFileManager
import ru.llama.tool.data.io.GGUFFileManagerImpl

actual val IOModule: Module = module {

    factory<GGUFFileManager> {
        GGUFFileManagerImpl(androidApplication().applicationContext)
    }

}