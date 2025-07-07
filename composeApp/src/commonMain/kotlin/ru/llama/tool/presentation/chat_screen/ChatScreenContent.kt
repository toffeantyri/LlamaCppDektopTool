package ru.llama.tool.presentation.chat_screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import llamacppdektoptool.composeapp.generated.resources.Res
import llamacppdektoptool.composeapp.generated.resources.compose_multiplatform
import org.jetbrains.compose.resources.painterResource
import ru.llama.tool.getPlatform


@Composable
fun ChatScreenContent(component: ChatComponent) {
    Column(
        modifier = Modifier
            .safeContentPadding()
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        var showContent by remember { mutableStateOf(false) }
        Button(onClick = { showContent = !showContent }) {
            Text("Click me!")
        }
        AnimatedVisibility(showContent) {
            val greeting = remember { getPlatform() }
            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Image(painterResource(Res.drawable.compose_multiplatform), null)
                Text("Compose: $greeting")
            }
        }
    }
} 