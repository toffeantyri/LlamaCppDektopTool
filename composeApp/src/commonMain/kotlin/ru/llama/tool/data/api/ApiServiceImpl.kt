package ru.llama.tool.data.api

import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.utils.io.readUTF8Line
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.yield
import kotlinx.serialization.json.Json
import ru.llama.tool.data.api.models.llama_models.LLamaMessageDto
import ru.llama.tool.data.api.models.llama_models.LlamaResponseDto
import ru.llama.tool.data.api.models.llama_models.MessageRequest
import ru.llama.tool.domain.models.EnumSender
import ru.llama.tool.domain.models.Message

private val json = Json {
    prettyPrint = true
    ignoreUnknownKeys = true
    explicitNulls = true
}

class ApiServiceImpl(
    private val client: HttpClient
) : ApiService {


    suspend fun fullEmit(message: MessageRequest): Flow<Message> = flow {
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

        require(response.contentType()?.withoutParameters() == ContentType.Text.EventStream) {
            "Expected text/event-stream but got ${response.contentType()}"
        }

        val content = response.bodyAsChannel()

        var dataBuilder = StringBuilder()
        var counter = 0

        while (!content.isClosedForRead) {
            val line = content.readUTF8Line() ?: continue

            if (line.startsWith("data:")) {
                val jsonString = line.substringAfter(":").trim()
                if (jsonString == "[DONE]") {
                    emit(
                        Message(
                            content = "",
                            sender = EnumSender.AI,
                            id = message.id
                        )
                    )
                } else {
                    val data =
                        json.decodeFromString<LlamaResponseDto>(jsonString).choices[0].delta.content
                            ?: ""
                    dataBuilder.clear()
                    dataBuilder.append(data)
                }
            } else if (line.isBlank() && dataBuilder.isNotEmpty()) {
                emit(
                    Message(
                        content = dataBuilder.toString(),
                        sender = EnumSender.AI,
                        id = message.id
                    )
                )
                dataBuilder.clear()
                yield()
            }
        }

        if (dataBuilder.isNotEmpty()) {
            emit(
                Message(
                    content = dataBuilder.toString(),
                    sender = EnumSender.AI,
                    id = message.id
                )
            )
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun simpleRequestAi(message: MessageRequest): Flow<Message> = flow {
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

        require(response.contentType()?.withoutParameters() == ContentType.Text.EventStream) {
            "Expected text/event-stream but got ${response.contentType()}"
        }

        val content = response.bodyAsChannel()
        var dataBuilder = StringBuilder()
        var counter = 0

        while (!content.isClosedForRead) {
            val line = content.readUTF8Line() ?: break

            if (line.startsWith("data:")) {
                val jsonString = line.substringAfter(":").trim()
                if (jsonString == "[DONE]") {
                    emit(
                        Message(
                            content = "",
                            sender = EnumSender.AI,
                            id = message.id
                        )
                    )
                } else {
                    val data =
                        json.decodeFromString<LlamaResponseDto>(jsonString).choices[0].delta.content
                            ?: ""
                    dataBuilder.clear()
                    dataBuilder.append(data)
                    emit(
                        Message(
                            content = data,
                            sender = EnumSender.AI,
                            id = message.id
                        )
                    )
                }
            }
            delay(10)
        }

        if (dataBuilder.isNotEmpty()) {
            emit(
                Message(
                    content = dataBuilder.toString(),
                    sender = EnumSender.AI,
                    id = message.id
                )
            )
        }


    }


}