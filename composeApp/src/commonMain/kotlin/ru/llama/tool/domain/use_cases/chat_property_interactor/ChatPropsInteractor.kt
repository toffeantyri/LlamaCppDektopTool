package ru.llama.tool.domain.use_cases.chat_property_interactor

import kotlinx.coroutines.flow.Flow
import ru.llama.tool.domain.models.AiDialogProperties

interface ChatPropsInteractor {

    suspend fun getChatProperty(id: Long): Flow<AiDialogProperties>

    suspend fun deleteChatProperty(id: Long)

    suspend fun saveChatProperty(data: AiDialogProperties)

}