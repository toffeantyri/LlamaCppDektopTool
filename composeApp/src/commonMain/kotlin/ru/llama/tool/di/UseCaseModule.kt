package ru.llama.tool.di

import org.koin.dsl.module
import ru.llama.tool.domain.use_cases.ai_props_use_case.GetAiPropertiesUseCase
import ru.llama.tool.domain.use_cases.ai_props_use_case.GetAiPropertiesUseCaseImpl
import ru.llama.tool.domain.use_cases.get_ai_dialog.GetAiDialogPropsUseCase
import ru.llama.tool.domain.use_cases.get_ai_dialog.GetAiDialogPropsUseCaseImpl
import ru.llama.tool.domain.use_cases.messaging_use_case.SendChatRequestUseCase
import ru.llama.tool.domain.use_cases.messaging_use_case.SendChatRequestUseCaseImpl

val useCasesModule = module {

    factory<SendChatRequestUseCase> {
        SendChatRequestUseCaseImpl(llamaAiRepository = get())
    }

    factory<GetAiPropertiesUseCase> {
        GetAiPropertiesUseCaseImpl(repo = get())
    }

    factory<GetAiDialogPropsUseCase> {
        GetAiDialogPropsUseCaseImpl(repo = get())
    }

}