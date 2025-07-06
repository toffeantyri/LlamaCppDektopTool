package ru.llama.tool.presentation.screen_b

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

interface ScreenBComponent {
    fun onPreviousScreenClicked()
}

@Composable
fun ScreenBContent(component: ScreenBComponent) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Screen B Content")
    }
} 