package ru.llama.tool.presentation.chat_screen

interface IChatViewModel {

    fun onMessageSend()

    fun stopMessageGen()

    fun onMessageInputChanged(input: String)

}