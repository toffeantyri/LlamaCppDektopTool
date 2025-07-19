package ru.llama.tool.data.api.models.llama_props_dto

import kotlinx.serialization.Serializable

@Serializable
data class NextToken(
    val has_new_line: Boolean? = null,
    val has_next_token: Boolean? = null,
    val n_decoded: Int? = null,
    val n_remain: Int? = null,
    val stopping_word: String? = null
)