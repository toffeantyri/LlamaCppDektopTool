package ru.llama.tool.domain.use_cases.get_ai_dialog

import kotlinx.coroutines.flow.Flow
import ru.llama.tool.data.repository.get_ai_dialog.GetAiDialogRepository
import ru.llama.tool.domain.models.AiDialogProperties

class GetAiDialogPropsUseCaseImpl(private val repo: GetAiDialogRepository) :
    GetAiDialogPropsUseCase {

    override suspend operator fun invoke(id: Int): Flow<AiDialogProperties> {
        return repo.getDialogProperties(id)
    }
}