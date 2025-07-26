package ru.llama.tool.presentation.events

sealed interface UpEventChat {

    data object CreateNewDialog : UpEventChat

    data class SelectDialogBy(val chatId: Long) : UpEventChat


}