package ru.llama.tool.presentation.root

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.bringToFront
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.replaceCurrent
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.instancekeeper.getOrCreate
import kotlinx.serialization.Serializable
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import ru.llama.tool.data.preferences.preferances.IAppPreferences
import ru.llama.tool.domain.models.AiDialogProperties
import ru.llama.tool.presentation.chat_screen.ChatComponentImpl
import ru.llama.tool.presentation.root.IRootComponent.Child
import ru.llama.tool.presentation.setting_screen.SettingComponent
import ru.llama.tool.presentation.setting_screen.SettingsComponentImpl
import ru.llama.tool.presentation.setting_screen.SettingsState
import ru.llama.tool.presentation.utils.componentCoroutineScope

class RootComponentImpl(
    componentContext: ComponentContext,
) : IRootComponent, ComponentContext by componentContext, KoinComponent {

    private val rootCoroutineScope = componentContext.componentCoroutineScope()

    private val preferences: IAppPreferences = get<IAppPreferences>()

    private val navigation = StackNavigation<Config>()
    private val _selectedIndex = MutableValue(0)

    private val settingsComponent: SettingComponent = instanceKeeper.getOrCreate {
        SettingsComponentImpl(
            componentContext = componentContext.childContext("settings"),
            parentCoroutineScope = rootCoroutineScope,
            preferences = preferences
        )
    }
    override val selectedIndex: Value<Int> = _selectedIndex
    override val appSettingState: Value<SettingsState> = settingsComponent.state

    private var currentChatId: Int? = null

    override val stack: Value<ChildStack<*, Child>> =
        childStack(
            source = navigation,
            serializer = Config.serializer(),
            initialStack = { listOf(Config.ChatContentConfig(null)) },
            handleBackButton = true,
            childFactory = ::child,
        )

    override fun onChatTabClicked() {
        _selectedIndex.value = 0
        navigation.bringToFront(Config.ChatContentConfig(currentChatId))
    }

    override fun onSettingsTabClicked() {
        _selectedIndex.value = 1
        navigation.bringToFront(Config.SettingContentConfig)
    }


    private fun child(config: Config, componentContext: ComponentContext): Child =
        when (config) {
            is Config.ChatContentConfig -> Child.ChatContentChild(
                ChatComponentImpl(
                    componentContext = componentContext,
                    chatId = config.chatId,
                    changeCurrentChatId = { newChatId ->
                        currentChatId = newChatId
                    },
                    createNewChat = {
                        println("createNewChat")
                        currentChatId =
                            if (currentChatId == AiDialogProperties.DEFAULT_ID) (AiDialogProperties.DEFAULT_ID - 1)
                            else AiDialogProperties.DEFAULT_ID

                        navigation.replaceCurrent(Config.ChatContentConfig(currentChatId))
                    }
                )
            )

            Config.SettingContentConfig -> Child.SettingContentChild(settingsComponent)
        }

    @Serializable
    private sealed interface Config {
        @Serializable
        data class ChatContentConfig(val chatId: Int? = null) : Config

        @Serializable
        data object SettingContentConfig : Config
    }
}