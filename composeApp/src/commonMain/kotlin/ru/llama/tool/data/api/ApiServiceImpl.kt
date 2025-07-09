package ru.llama.tool.data.api

import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.utils.io.readRemaining
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.io.readByteArray
import kotlinx.serialization.json.Json
import ru.llama.tool.data.api.models.llama_models.LLamaMessageDto
import ru.llama.tool.data.api.models.llama_models.LlamaResponseDto
import ru.llama.tool.data.api.models.llama_models.MessageRequest

class ApiServiceImpl(
    private val client: HttpClient
) : ApiService {

    override suspend fun simpleRequestAi(message: MessageRequest): Flow<LlamaResponseDto> = flow {
        val url = "http://127.0.0.1:8080/v1/chat/completions"

        val response = client.post(url) {
            contentType(ContentType.Application.Json)
            setBody(
                LLamaMessageDto(
                    messages = listOf(message),
                    stream = true
                )
            )
        }


        val input = response.bodyAsChannel()
        val packet = input.readRemaining(4096)
        var partial = ""

        while (!packet.exhausted()) {
            val bytes = packet.readByteArray()
            val text = bytes.decodeToString()
            val combined = partial + text

            val lines = combined.split("\n")

            partial = lines.last()
            for (line in lines.dropLast(1)) {
                if (line.startsWith("{")) {
                    try {
                        val dto = Json.decodeFromString<LlamaResponseDto>(line)
                        emit(dto)
                        println(dto.choices[0].delta.content)
                    } catch (e: Exception) {
                        // игнорируем служебные строки
                    }
                }
            }
        }


        if (partial.isNotBlank()) {
            try {
                val dto = Json.decodeFromString<LlamaResponseDto>(partial)
                emit(dto)
                println(dto.choices[0].delta.content)
            } catch (e: Exception) {
                // игнорируем
            }
        }
    }
}