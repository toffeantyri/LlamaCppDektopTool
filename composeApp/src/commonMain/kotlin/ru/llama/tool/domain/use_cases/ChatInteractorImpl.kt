package ru.llama.tool.domain.use_cases

import kotlinx.coroutines.flow.Flow
import ru.llama.tool.data.repository.get_ai_dialog_props.ChatsRepository
import ru.llama.tool.domain.models.AIDialogChatDto

class ChatInteractorImpl(private val repo: ChatsRepository) : ChatInteractor {

    override suspend fun saveChatToDb(chat: AIDialogChatDto): Long {
        return repo.saveChatToDb(chat)
    }

    override suspend fun deleteChatFromDb(chatId: Long) {
        return repo.deleteChatFromDb(chatId)
    }

    override suspend fun getDialogChat(chatId: Long): Flow<AIDialogChatDto> {
        return repo.getDialogChat(chatId)
    }

    override suspend fun getAllChatsList(): List<AIDialogChatDto> {
        return repo.getAllChatsList()
    }

    override suspend fun renameChat(id: Long, newChatName: String) {
        return repo.renameChat(id, newChatName)
    }
}