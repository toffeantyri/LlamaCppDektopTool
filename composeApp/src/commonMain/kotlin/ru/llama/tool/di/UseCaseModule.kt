package ru.llama.tool.di

import org.koin.dsl.module
import ru.llama.tool.domain.use_cases.ChatInteractor
import ru.llama.tool.domain.use_cases.ChatInteractorImpl
import ru.llama.tool.domain.use_cases.chat_property_interactor.ChatPropsInteractor
import ru.llama.tool.domain.use_cases.chat_property_interactor.ChatPropsInteractorImpl
import ru.llama.tool.domain.use_cases.llama_props_use_case.GetLlamaPropertiesUseCase
import ru.llama.tool.domain.use_cases.llama_props_use_case.GetLlamaPropertiesUseCaseImpl
import ru.llama.tool.domain.use_cases.messaging_use_case.SendChatRequestUseCase
import ru.llama.tool.domain.use_cases.messaging_use_case.SendChatRequestUseCaseImpl

val useCasesModule = module {

    factory<SendChatRequestUseCase> {
        SendChatRequestUseCaseImpl(llamaAiRepository = get())
    }

    factory<GetLlamaPropertiesUseCase> {
        GetLlamaPropertiesUseCaseImpl(repo = get())
    }

    factory<ChatPropsInteractor> {
        ChatPropsInteractorImpl(repo = get())
    }

    factory<ChatInteractor> {
        ChatInteractorImpl(repo = get())
    }

}