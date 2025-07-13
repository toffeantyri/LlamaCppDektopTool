package ru.llama.tool.di

import org.koin.dsl.module
import ru.llama.tool.data.repository.ai_props.AiPropertiesRepositoryImpl
import ru.llama.tool.data.repository.ai_props.IAiPropertiesRepository
import ru.llama.tool.data.repository.ai_repo.LlamaAiRepository
import ru.llama.tool.data.repository.ai_repo.LlamaAiRepositoryImpl

val repositoryModule = module {

    factory<LlamaAiRepository> {
        LlamaAiRepositoryImpl(llamaAiDataSource = get())
    }

    factory<IAiPropertiesRepository> {
        AiPropertiesRepositoryImpl(dataSource = get())
    }
}