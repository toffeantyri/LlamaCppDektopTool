package ru.llama.tool.domain.use_cases

import ru.llama.tool.data.repository.get_ai_dialog_props.ChatsRepository
import ru.llama.tool.domain.models.AIDialogChatDto

class ChatInteractorImpl(private val repo: ChatsRepository) : ChatInteractor {

    override suspend fun saveChatToDb(chat: AIDialogChatDto) {
        return repo.saveChatToDb(chat)
    }

    override suspend fun deleteChatFromDb(chatId: Int) {
        return repo.deleteChatFromDb(chatId)
    }

    override suspend fun getDialogChat(chatId: Int): AIDialogChatDto {
        return repo.getDialogChat(chatId)
    }

    override suspend fun getAllChatsList(): List<AIDialogChatDto> {
        return repo.getAllChatsList()
    }
}