package ru.llama.tool.presentation.screen_a

import com.arkivanov.decompose.ComponentContext

class ScreenAComponentImpl(componentContext: ComponentContext) : ScreenAComponent,
    ComponentContext by componentContext {
    override fun onNextScreenClicked() {
        // Handle navigation to next screen
    }
} 