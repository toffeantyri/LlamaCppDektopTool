package ru.llama.tool.presentation.root.first_tab_root

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.value.Value
import kotlinx.serialization.Serializable
import ru.llama.tool.presentation.chat_screen.ChatComponentImpl

class FirstTabComponentImpl(
    componentContext: ComponentContext
) : FirstTabComponent, ComponentContext by componentContext {

    private val navigation = StackNavigation<Config>()

    override val stack: Value<ChildStack<*, FirstTabComponent.Child>> =
        childStack(
            source = navigation,
            serializer = Config.serializer(),
            initialStack = {
                listOf(
                    Config.ChatContentConfig
                )
            },
            handleBackButton = true,
            childFactory = ::child,
        )


    private fun child(config: Config, componentContext: ComponentContext): FirstTabComponent.Child =
        when (config) {
            is Config.ChatContentConfig -> FirstTabComponent.Child.ChatContentChild(
                ChatComponentImpl(
                    componentContext = componentContext,
                ))

        }

    @Serializable
    private sealed interface Config {
        @Serializable
        data object ChatContentConfig : Config
    }
}