package ru.llama.tool.presentation.root.first_tab_root

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.pushNew
import com.arkivanov.decompose.value.Value
import kotlinx.serialization.Serializable
import ru.llama.tool.core.EMPTY
import ru.llama.tool.domain.models.AiDialogProperties
import ru.llama.tool.presentation.chat_screen.ChatComponentImpl

class FirstTabComponentImpl(
    componentContext: ComponentContext
) : FirstTabComponent, ComponentContext by componentContext {

    private val navigation = StackNavigation<Config>()

    private var currentChatId: Long? = null


    override val stack: Value<ChildStack<*, FirstTabComponent.Child>> =
        childStack(
            source = navigation,
            serializer = Config.serializer(),
            initialStack = {
                listOf(
                    Config.ChatContentConfig(null)
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

                        navigation.pop()
                        navigation.pushNew(
                            Config.ChatContentConfig(
                                chatId = currentChatId
                            )
                        )
                    }
                ))

        }

    @Serializable
    private sealed interface Config {
        @Serializable
        data class ChatContentConfig(
            val chatId: Long? = null,
            val chatName: String = EMPTY
        ) : Config
    }
}