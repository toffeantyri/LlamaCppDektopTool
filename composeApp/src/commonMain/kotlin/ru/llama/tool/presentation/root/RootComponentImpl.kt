package ru.llama.tool.presentation.root

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.value.Value
import kotlinx.serialization.Serializable
import ru.llama.tool.presentation.root.IRootComponent.Child
import ru.llama.tool.presentation.screen_a.ScreenAComponentImpl
import ru.llama.tool.presentation.screen_b.ScreenBComponentImpl

class RootComponentImpl(
    componentContext: ComponentContext,
    private val onExitAction: () -> Unit
) : IRootComponent, ComponentContext by componentContext {

    private val navigation = StackNavigation<Config>()

    override val stack: Value<ChildStack<*, Child>> =
        childStack(
            source = navigation,
            serializer = Config.serializer(),
            initialStack = { listOf(Config.ScreenA) },
            handleBackButton = true,
            childFactory = ::child,
        )

    private fun child(config: Config, componentContext: ComponentContext): Child =
        when (config) {
            Config.ScreenA -> Child.ScreenA(ScreenAComponentImpl(componentContext))
            Config.ScreenB -> Child.ScreenB(ScreenBComponentImpl(componentContext))
        }

    @Serializable
    private sealed interface Config {
        @Serializable
        data object ScreenA : Config

        @Serializable
        data object ScreenB : Config
    }
}