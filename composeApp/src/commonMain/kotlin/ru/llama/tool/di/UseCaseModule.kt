package ru.llama.tool.di

import org.koin.dsl.module
import ru.llama.tool.domain.SendChatRequestUseCase
import ru.llama.tool.domain.SendChatRequestUseCaseImpl

val useCasesModule = module {

    factory<SendChatRequestUseCase> {
        SendChatRequestUseCaseImpl(llamaAiRepository = get())
    }

}