package ru.llama.tool.presentation.screen_b

import com.arkivanov.decompose.ComponentContext

class ScreenBComponentImpl(componentContext: ComponentContext) : ScreenBComponent,
    ComponentContext by componentContext {
    override fun onPreviousScreenClicked() {
        // Handle navigation to previous screen
    }
} 