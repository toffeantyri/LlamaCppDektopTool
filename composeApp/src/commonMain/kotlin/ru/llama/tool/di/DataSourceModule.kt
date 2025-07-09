package ru.llama.tool.di

import org.koin.dsl.module
import ru.llama.tool.data.data_sources.LlamaAiDataSource
import ru.llama.tool.data.data_sources.LlamaAiDataSourceImpl

val dataSourceModule = module {

    factory<LlamaAiDataSource> {
        LlamaAiDataSourceImpl(api = get())
    }

}