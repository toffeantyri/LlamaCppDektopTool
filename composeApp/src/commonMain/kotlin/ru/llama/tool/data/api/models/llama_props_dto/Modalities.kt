package ru.llama.tool.data.api.models.llama_props_dto

import kotlinx.serialization.Serializable

@Serializable
data class Modalities(
    val audio: Boolean? = null,
    val vision: Boolean? = null
)