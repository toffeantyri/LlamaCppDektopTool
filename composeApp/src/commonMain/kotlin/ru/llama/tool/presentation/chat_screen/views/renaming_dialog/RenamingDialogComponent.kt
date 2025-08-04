package ru.llama.tool.presentation.chat_screen.views.renaming_dialog

interface RenamingDialogComponent {

    val initialText: String

    fun onDismiss()

    fun onSave(value: String)

}