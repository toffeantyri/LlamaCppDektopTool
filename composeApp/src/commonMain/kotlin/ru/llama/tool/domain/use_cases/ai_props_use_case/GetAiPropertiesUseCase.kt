package ru.llama.tool.domain.use_cases.ai_props_use_case

import ru.llama.tool.domain.models.LLamaPropsDto

interface GetAiPropertiesUseCase {

    suspend operator fun invoke(): LLamaPropsDto

}