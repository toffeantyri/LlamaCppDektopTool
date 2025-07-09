package ru.llama.tool.data.api.models.llama_models

import kotlinx.serialization.Serializable

@Serializable
data class LLamaMessageDto(
    val cache_prompt: Boolean = true,
    val dry_allowed_length: Int = 2,
    val dry_base: Double = 1.75,
    val dry_multiplier: Int = 0,
    val dry_penalty_last_n: Int = -1,
    val dynatemp_exponent: Int = 1,
    val dynatemp_range: Int = 0,
    val frequency_penalty: Int = 0,
    val max_tokens: Int = -1,
    val messages: List<MessageRequest>,
    val min_p: Double = 0.05,
    val presence_penalty: Int = 0,
    val repeat_last_n: Int = 64,
    val repeat_penalty: Int = 1,
    val samplers: String = "edkypmxt",
    val stream: Boolean,
    val temperature: Double = 0.8,
    val timings_per_token: Boolean = false,
    val top_k: Int = 40,
    val top_p: Double = 0.95,
    val typical_p: Int = 1,
    val xtc_probability: Int = 0,
    val xtc_threshold: Double = 0.1
)