package ru.llama.tool.domain.use_cases.llama_props_use_case

import ru.llama.tool.data.repository.ai_props.IAiPropertiesRepository
import ru.llama.tool.domain.models.LLamaPropsDto

class GetLlamaPropertiesUseCaseImpl(private val repo: IAiPropertiesRepository) :
    GetLlamaPropertiesUseCase {

    override suspend fun invoke(): LLamaPropsDto {
        return repo.getAiProperties()
    }
}