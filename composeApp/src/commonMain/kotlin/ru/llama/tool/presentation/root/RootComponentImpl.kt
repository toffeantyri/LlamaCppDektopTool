package ru.llama.tool.presentation.root

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.bringToFront
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.instancekeeper.getOrCreate
import kotlinx.serialization.Serializable
import ru.llama.tool.presentation.root.IRootComponent.Child
import ru.llama.tool.presentation.screen_a.ScreenAComponentImpl
import ru.llama.tool.presentation.screen_b.SettingsComponent
import ru.llama.tool.presentation.screen_b.SettingsComponentImpl
import ru.llama.tool.presentation.screen_b.SettingsState

class RootComponentImpl(
    componentContext: ComponentContext,
) : IRootComponent, ComponentContext by componentContext {

    private val navigation = StackNavigation<Config>()
    private val _selectedIndex = MutableValue(0)
    private val settingsComponent: SettingsComponent = instanceKeeper.getOrCreate {
        SettingsComponentImpl(
            componentContext.childContext("settings")
        )
    }
    override val selectedIndex: Value<Int> = _selectedIndex
    override val appSettingState: Value<SettingsState> = settingsComponent.state

    override val stack: Value<ChildStack<*, Child>> =
        childStack(
            source = navigation,
            serializer = Config.serializer(),
            initialStack = { listOf(Config.ScreenA) },
            handleBackButton = true,
            childFactory = ::child,
        )

    override fun onChatTabClicked() {
        _selectedIndex.value = 0
        navigation.bringToFront(Config.ScreenA)
    }

    override fun onSettingsTabClicked() {
        _selectedIndex.value = 1
        navigation.bringToFront(Config.SettingScreenConfig)
    }


    private fun child(config: Config, componentContext: ComponentContext): Child =
        when (config) {
            Config.ScreenA -> Child.ScreenA(ScreenAComponentImpl(componentContext))
            Config.SettingScreenConfig -> Child.SettingScreenChild(settingsComponent)
        }

    @Serializable
    private sealed interface Config {
        @Serializable
        data object ScreenA : Config

        @Serializable
        data object SettingScreenConfig : Config
    }
}