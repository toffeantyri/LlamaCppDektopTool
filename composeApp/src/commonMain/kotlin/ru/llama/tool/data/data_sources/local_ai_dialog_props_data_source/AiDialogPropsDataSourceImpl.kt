package ru.llama.tool.data.data_sources.local_ai_dialog_props_data_source

import kotlinx.coroutines.flow.Flow
import ru.llama.tool.data.room.ai_properties_db.AiPropertiesDao
import ru.llama.tool.data.room.ai_properties_db.AiPropertiesEntity

class AiDialogPropsDataSourceImpl(private val dao: AiPropertiesDao) : AiDialogPropsDataSource {
    override suspend fun saveToDb(data: AiPropertiesEntity) {
        return dao.insert(data)
    }

    override suspend fun deleteFromDb(id: Int) {
        return dao.delete(id)
    }

    override suspend fun getDialogProperties(id: Int): Flow<AiPropertiesEntity?> {
        return dao.getDataBy(id)
    }
}