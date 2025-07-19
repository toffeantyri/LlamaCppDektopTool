package ru.llama.tool.domain.use_cases.get_ai_dialog

import kotlinx.coroutines.flow.Flow
import ru.llama.tool.domain.models.AiDialogProperties

interface GetAiDialogPropsUseCase {

    suspend operator fun invoke(id: Int): Flow<AiDialogProperties>

}