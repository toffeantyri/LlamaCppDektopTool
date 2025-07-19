package ru.llama.tool.domain.models

sealed interface EnumSender {

    data object User : EnumSender
    data object AI : EnumSender
    data object System : EnumSender
    data class Error(val throwable: Throwable) : EnumSender

}