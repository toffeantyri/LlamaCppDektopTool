package ru.llama.tool.presentation.chat_screen

import ru.llama.tool.domain.models.AiDialogProperties

interface IChatViewModel {

    fun onRepeatMessageSend()
    fun onMessageSend()

    fun saveProperties(newProp: AiDialogProperties)
    fun stopMessageGen()

    fun onMessageInputChanged(input: String)

}