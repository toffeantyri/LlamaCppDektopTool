package ru.llama.tool.data.api.models.llama_props_dto

import kotlinx.serialization.Serializable

@Serializable
data class LlamaProperties(
    val bos_token: String? = null,
    val build_info: String? = null,
    val chat_template: String? = null,
    val default_generation_settings: DefaultGenerationSettings? = null,
    val eos_token: String? = null,
    val modalities: Modalities? = null,
    val model_path: String? = null,
    val total_slots: Int? = null
)