package ru.llama.tool.data.repository.get_ai_dialog_props

import ru.llama.tool.core.EMPTY
import ru.llama.tool.data.data_sources.local_ai_dialog_chat_data_source.AiDialogChatDataSource
import ru.llama.tool.data.room.ai_chat_dao.AiChatEntity
import ru.llama.tool.domain.models.AIDialogChatDto
import ru.llama.tool.domain.models.AiDialogProperties

class ChatRepositoryImpl(
    private val chatsDataSource: AiDialogChatDataSource,
) : ChatsRepository {

    override suspend fun saveChatToDb(chat: AIDialogChatDto) {
        return chatsDataSource.saveToDb(
            AiChatEntity(
                id = chat.chatId,
                messages = chat.messages,
                chatName = chat.chatName
            )
        )
    }

    override suspend fun deleteChatFromDb(chatId: Int) {
        return chatsDataSource.deleteFromDb(chatId)
    }

    override suspend fun getDialogChat(chatId: Int): AIDialogChatDto {
        val result = chatsDataSource.getDataBy(chatId)
        return AIDialogChatDto(
            chatId = result?.id ?: AiDialogProperties.DEFAULT_ID,
            chatName = result?.chatName ?: EMPTY,
            messages = result?.messages ?: emptyList()
        )
    }

    override suspend fun getAllChatsList(): List<AIDialogChatDto> {
        return chatsDataSource.getAllChats().map {
            AIDialogChatDto(
                chatId = it.id,
                chatName = it.chatName,
                messages = it.messages
            )
        }
    }
}