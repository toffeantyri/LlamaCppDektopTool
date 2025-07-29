package ru.llama.tool.presentation.root.first_tab_root

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.slide
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import ru.llama.tool.presentation.chat_screen.ChatScreenContent


@Composable
fun FirstTabContent(component: FirstTabComponent) {

    val childStack by component.stack.subscribeAsState()
    Scaffold(
        modifier = Modifier
    ) { innerPadding ->

        Children(
            stack = childStack,
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            animation = stackAnimation(animator = slide())
        ) {
            when (val child = it.instance) {
                is FirstTabComponent.Child.ChatContentChild -> ChatScreenContent(child.component)

            }
        }
    }
}
