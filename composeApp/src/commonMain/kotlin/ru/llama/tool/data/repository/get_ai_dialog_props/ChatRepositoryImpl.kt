package ru.llama.tool.data.repository.get_ai_dialog_props

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime
import ru.llama.tool.core.EMPTY
import ru.llama.tool.data.data_sources.local_ai_dialog_chat_data_source.AiDialogChatDataSource
import ru.llama.tool.data.room.ai_chat_dao.AiChatEntity
import ru.llama.tool.domain.models.AIDialogChatDto
import ru.llama.tool.domain.models.AiDialogProperties

class ChatRepositoryImpl(
    private val chatsDataSource: AiDialogChatDataSource,
) : ChatsRepository {

    override suspend fun saveChatToDb(chat: AIDialogChatDto): Long {
        val now = Clock.System.now()
        val localDateTime = now.toLocalDateTime(TimeZone.currentSystemDefault())
        val formattedDate = localDateTime.date.format(LocalDate.Format {
            dayOfMonth()
            char('.')
            monthNumber()
            char('.')
            year()
        })
        return chatsDataSource.saveToDb(
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

    override suspend fun getDialogChat(chatId: Long): AIDialogChatDto {
        val result = chatsDataSource.getDataBy(chatId)
        return AIDialogChatDto(
            chatId = result?.id ?: AiDialogProperties.DEFAULT_ID,
            chatName = result?.chatName ?: EMPTY,
            messages = result?.messages ?: emptyList(),
            date = result?.date ?: ""
        )
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
}