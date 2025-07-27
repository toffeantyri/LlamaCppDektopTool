package ru.llama.tool.di

import org.koin.dsl.module
import ru.llama.tool.data.repository.ai_props.AiPropertiesRepositoryImpl
import ru.llama.tool.data.repository.ai_props.IAiPropertiesRepository
import ru.llama.tool.data.repository.get_ai_dialog_props.ChatPropsRepository
import ru.llama.tool.data.repository.get_ai_dialog_props.ChatPropsRepositoryImpl
import ru.llama.tool.data.repository.get_ai_dialog_props.ChatRepositoryImpl
import ru.llama.tool.data.repository.get_ai_dialog_props.ChatsRepository
import ru.llama.tool.data.repository.llama_repo.LlamaAiRepository
import ru.llama.tool.data.repository.llama_repo.LlamaAiRepositoryImpl

val repositoryModule = module {

    factory<LlamaAiRepository> {
        LlamaAiRepositoryImpl(llamaAiDataSource = get())
    }

    factory<IAiPropertiesRepository> {
        AiPropertiesRepositoryImpl(dataSource = get())
    }

    factory<ChatPropsRepository> {
        ChatPropsRepositoryImpl(propsDataSource = get(), pref = get())
    }

    factory<ChatsRepository> {
        ChatRepositoryImpl(chatsDataSource = get())
    }
}