package ru.llama.tool.domain.use_cases.llama_props_use_case

import ru.llama.tool.domain.models.LLamaPropsDto

interface GetLlamaPropertiesUseCase {

    suspend operator fun invoke(): LLamaPropsDto

}