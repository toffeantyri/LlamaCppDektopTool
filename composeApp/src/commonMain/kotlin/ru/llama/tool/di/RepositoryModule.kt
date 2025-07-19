package ru.llama.tool.di

import org.koin.dsl.module
import ru.llama.tool.data.repository.ai_props.AiPropertiesRepositoryImpl
import ru.llama.tool.data.repository.ai_props.IAiPropertiesRepository
import ru.llama.tool.data.repository.ai_repo.LlamaAiRepository
import ru.llama.tool.data.repository.ai_repo.LlamaAiRepositoryImpl
import ru.llama.tool.data.repository.get_ai_dialog.GetAiDialogRepository
import ru.llama.tool.data.repository.get_ai_dialog.GetAiDialogRepositoryImpl

val repositoryModule = module {

    factory<LlamaAiRepository> {
        LlamaAiRepositoryImpl(llamaAiDataSource = get())
    }

    factory<IAiPropertiesRepository> {
        AiPropertiesRepositoryImpl(dataSource = get())
    }

    factory<GetAiDialogRepository> {
        GetAiDialogRepositoryImpl(dao = get())
    }
}