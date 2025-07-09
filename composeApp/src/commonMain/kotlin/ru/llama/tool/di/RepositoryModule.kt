package ru.llama.tool.di

import org.koin.dsl.module
import ru.llama.tool.data.repository.LlamaAiRepository
import ru.llama.tool.data.repository.LlamaAiRepositoryImpl

val repositoryModule = module {

    factory<LlamaAiRepository> {
        LlamaAiRepositoryImpl(llamaAiDataSource = get())
    }
}