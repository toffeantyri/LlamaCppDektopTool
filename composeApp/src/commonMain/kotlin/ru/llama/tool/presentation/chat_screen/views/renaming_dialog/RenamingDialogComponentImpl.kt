package ru.llama.tool.presentation.chat_screen.views.renaming_dialog

class RenamingDialogComponentImpl(
    private val onDismissAction: () -> Unit,
    private val onSaveAction: (String) -> Unit,
    override val initialText: String
) : RenamingDialogComponent {

    override fun onDismiss() = onDismissAction()


    override fun onSave(value: String) {
        onSaveAction(value)
        onDismissAction()
    }
}