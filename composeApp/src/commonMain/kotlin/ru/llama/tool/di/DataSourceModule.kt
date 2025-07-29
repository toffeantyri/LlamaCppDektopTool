package ru.llama.tool.di

import org.koin.dsl.module
import ru.llama.tool.data.data_sources.llama_props_data_source.AiPropertiesDataSourceImpl
import ru.llama.tool.data.data_sources.llama_props_data_source.IAiPropertiesDataSource
import ru.llama.tool.data.data_sources.local_ai_dialog_chat_data_source.AiDialogChatDataSource
import ru.llama.tool.data.data_sources.local_ai_dialog_chat_data_source.AiDialogChatDataSourceImpl
import ru.llama.tool.data.data_sources.local_ai_dialog_props_data_source.AiDialogPropsDataSource
import ru.llama.tool.data.data_sources.local_ai_dialog_props_data_source.AiDialogPropsDataSourceImpl
import ru.llama.tool.data.data_sources.messaging_data_source.LlamaAiDataSource
import ru.llama.tool.data.data_sources.messaging_data_source.LlamaAiDataSourceImpl

val dataSourceModule = module {

    factory<LlamaAiDataSource> {
        LlamaAiDataSourceImpl(api = get())
    }

    factory<IAiPropertiesDataSource> {
        AiPropertiesDataSourceImpl(api = get())
    }

    factory<AiDialogPropsDataSource> {
        AiDialogPropsDataSourceImpl(dao = get())
    }

    factory<AiDialogChatDataSource> {
        AiDialogChatDataSourceImpl(dao = get())
    }

}