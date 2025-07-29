package ru.llama.tool.presentation.events

interface UiEvent {

    data object Initial : UiEvent

    data object CloseDrawer : UiEvent

}