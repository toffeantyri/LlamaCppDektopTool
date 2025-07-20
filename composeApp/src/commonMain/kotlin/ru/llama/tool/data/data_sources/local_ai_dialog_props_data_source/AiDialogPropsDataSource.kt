package ru.llama.tool.data.data_sources.local_ai_dialog_props_data_source

import kotlinx.coroutines.flow.Flow
import ru.llama.tool.data.room.ai_properties_db.AiPropertiesEntity

interface AiDialogPropsDataSource {

    suspend fun saveToDb(data: AiPropertiesEntity)

    suspend fun deleteFromDb(id: Int)

    suspend fun getDialogProperties(id: Int): Flow<AiPropertiesEntity?>


}