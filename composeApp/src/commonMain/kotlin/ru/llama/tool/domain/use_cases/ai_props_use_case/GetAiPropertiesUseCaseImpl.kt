package ru.llama.tool.domain.use_cases.ai_props_use_case

import ru.llama.tool.data.repository.ai_props.IAiPropertiesRepository
import ru.llama.tool.domain.models.LLamaPropsDto

class GetAiPropertiesUseCaseImpl(private val repo: IAiPropertiesRepository) :
    GetAiPropertiesUseCase {

    override suspend fun invoke(): LLamaPropsDto {
        return repo.getAiProperties()
    }
}