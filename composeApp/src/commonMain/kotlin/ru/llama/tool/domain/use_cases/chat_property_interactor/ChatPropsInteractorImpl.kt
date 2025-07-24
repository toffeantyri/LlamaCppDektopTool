package ru.llama.tool.domain.use_cases.chat_property_interactor

import kotlinx.coroutines.flow.Flow
import ru.llama.tool.data.repository.get_ai_dialog_props.ChatPropsRepository
import ru.llama.tool.domain.models.AiDialogProperties

class ChatPropsInteractorImpl(private val repo: ChatPropsRepository) :
    ChatPropsInteractor {

    override suspend fun getChatProperty(id: Long): Flow<AiDialogProperties> {
        return repo.getDialogProperties(id)
    }

    override suspend fun deleteChatProperty(id: Long) {
        return repo.deletePropsFromDb(id)
    }

    override suspend fun saveChatProperty(data: AiDialogProperties) {
        return repo.savePropsToDb(data)
    }
}