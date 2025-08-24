package ru.llama.tool.di

import org.koin.core.module.Module
import org.koin.dsl.module
import ru.llama.tool.data.io.GGUFFileInfo
import ru.llama.tool.data.io.GGUFFileManager

actual val IOModule: Module = module {

    factory<GGUFFileManager> {
        object : GGUFFileManager {
            override fun getExistFiles(): List<GGUFFileInfo> {
                return emptyList()
            }

            override fun openFilePicker() {
                //todo
            }
        }
    }

}