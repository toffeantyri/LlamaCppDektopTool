package ru.llama.tool.domain.models

import kotlinx.serialization.Serializable

@Serializable
sealed interface EnumSender {

    @Serializable
    data object User : EnumSender

    @Serializable
    data object AI : EnumSender

    @Serializable
    data object System : EnumSender

    @Serializable
    data class Error(val throwable: String) : EnumSender

}