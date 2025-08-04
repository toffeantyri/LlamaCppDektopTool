package ru.llama.tool.data.repository.get_ai_dialog_props

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.llama.tool.core.EMPTY
import ru.llama.tool.data.data_sources.local_ai_dialog_chat_data_source.AiDialogChatDataSource
import ru.llama.tool.data.room.ai_chat_dao.AiChatEntity
import ru.llama.tool.domain.models.AIDialogChatDto
import ru.llama.tool.domain.models.AiDialogProperties
import ru.llama.tool.presentation.utils.getFormattedNowDate

class ChatRepositoryImpl(
    private val chatsDataSource: AiDialogChatDataSource,
) : ChatsRepository {

    override suspend fun saveChatToDb(chat: AIDialogChatDto): Long {
        val formattedDate = getFormattedNowDate()
        return chatsDataSource.saveToDbReturnId(
            AiChatEntity(
                id = chat.chatId,
                messages = chat.messages,
                chatName = chat.chatName,
                date = formattedDate
            )
        )
    }

    override suspend fun deleteChatFromDb(chatId: Long) {
        return chatsDataSource.deleteFromDb(chatId)
    }

    override suspend fun getDialogChat(chatId: Long): Flow<AIDialogChatDto> {
        val result = chatsDataSource.getDataBy(chatId)
        return result.map {
            AIDialogChatDto(
                chatId = it?.id ?: AiDialogProperties.DEFAULT_ID,
                chatName = it?.chatName ?: EMPTY,
                messages = it?.messages ?: emptyList(),
                date = it?.date ?: ""
            )
        }
    }

    override suspend fun getAllChatsList(): List<AIDialogChatDto> {
        return chatsDataSource.getAllChats().map {
            AIDialogChatDto(
                chatId = it.id,
                chatName = it.chatName,
                messages = it.messages,
                date = it.date
            )
        }
    }

    override suspend fun renameChat(id: Long, newChatName: String) {
        return chatsDataSource.renameChat(id, newChatName)
    }
}