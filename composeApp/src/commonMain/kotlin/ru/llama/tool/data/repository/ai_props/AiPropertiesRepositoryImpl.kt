package ru.llama.tool.data.repository.ai_props

import llamacppdektoptool.composeapp.generated.resources.Res
import llamacppdektoptool.composeapp.generated.resources.model_is_not_exist_name
import ru.llama.tool.data.data_sources.llama_props_data_source.IAiPropertiesDataSource
import ru.llama.tool.domain.models.LLamaPropsDto
import ru.llama.tool.domain.models.UiText

class AiPropertiesRepositoryImpl(private val dataSource: IAiPropertiesDataSource) :
    IAiPropertiesRepository {
    override suspend fun getAiProperties(): LLamaPropsDto {
        val result = dataSource.getLLamaProperties()
        return LLamaPropsDto(modelName = result.model_path?.let { UiText.StringValue(it) }
            ?: UiText.StringRes(
                Res.string.model_is_not_exist_name
            ))
    }
}