package ru.llama.tool.data.api.models.llama_props_dto

import kotlinx.serialization.Serializable

@Serializable
data class DefaultGenerationSettings(
    val id: Int? = null,
    val id_task: Int? = null,
    val is_processing: Boolean? = null,
    val n_ctx: Int? = null,
    val next_token: NextToken? = null,
    val params: Params? = null,
    val prompt: String? = null,
    val speculative: Boolean? = null
)