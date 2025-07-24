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
import ru.llama.tool.core.EMPTY
import ru.llama.tool.data.preferences.preferances.IAppPreferences
import ru.llama.tool.domain.models.AiDialogProperties
import ru.llama.tool.presentation.chat_screen.ChatComponentImpl
import ru.llama.tool.presentation.root.IRootComponent.Child
import ru.llama.tool.presentation.setting_screen.SettingComponent
import ru.llama.tool.presentation.setting_screen.SettingsComponentImpl
import ru.llama.tool.presentation.setting_screen.SettingsState
import ru.llama.tool.presentation.utils.componentCoroutineScope
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
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

    private var currentChatId: Long? = null


    override val stack: Value<ChildStack<*, Child>> =
        childStack(
            source = navigation,
            serializer = Config.serializer(),
            initialStack = {
                listOf(
                    Config.ChatContentConfig(
                        null,
                        configUuid = Uuid.random().toString()
                    )
                )
            },
            handleBackButton = true,
            childFactory = ::child,
        )
    override fun onChatTabClicked() {
        _selectedIndex.value = 0
        navigation.bringToFront(
            Config.ChatContentConfig(
                chatId = currentChatId,
                configUuid = Uuid.random().toString()
            )
        )
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
                    chatName = config.chatName,
                    changeCurrentChatId = { newChatId ->
                        currentChatId = newChatId
                    },
                    createNewChat = {
                        println("OLD value $currentChatId")
                        currentChatId = when (currentChatId) {
                            AiDialogProperties.DEFAULT_ID -> null
                            null -> AiDialogProperties.DEFAULT_ID
                            else -> null
                        }
                        println("NEW value $currentChatId")

                        navigation.replaceCurrent(
                            Config.ChatContentConfig(
                                chatId = currentChatId,
                                configUuid = Uuid.random().toString()
                            )
                        )
                    }
                )
            )

            Config.SettingContentConfig -> Child.SettingContentChild(settingsComponent)
        }

    @Serializable
    private sealed interface Config {
        @Serializable
        data class ChatContentConfig(
            val chatId: Long? = null,
            val chatName: String = EMPTY,
            val configUuid: String
        ) :
            Config

        @Serializable
        data object SettingContentConfig : Config
    }
}