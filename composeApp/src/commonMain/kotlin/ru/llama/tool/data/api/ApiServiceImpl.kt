package ru.llama.tool.data.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.ContentType
import io.ktor.http.appendPathSegments
import io.ktor.http.contentType
import io.ktor.utils.io.readUTF8Line
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json
import ru.llama.tool.data.api.models.llama_models.LLamaMessageDto
import ru.llama.tool.data.api.models.llama_models.LlamaResponseDto
import ru.llama.tool.data.api.models.llama_props_dto.LlamaProperties
import ru.llama.tool.data.api.models.messages.MessageRequest
import ru.llama.tool.data.api.setting_http_client_provider.ISettingHttpClientProvider
import ru.llama.tool.domain.models.EnumSender
import ru.llama.tool.domain.models.Message

private val json = Json {
    prettyPrint = true
    ignoreUnknownKeys = true
    explicitNulls = true
}

class ApiServiceImpl(
    private val client: HttpClient,
    private val settingProvider: ISettingHttpClientProvider
) : ApiService {

    override suspend fun getModelProperties(): LlamaProperties {
        return client.get(settingProvider.getBaseUrl()) {
            url {
                appendPathSegments("props")
            }
            contentType(ContentType.Application.Json)
        }.body()
    }


    override suspend fun simpleRequestAi(messages: List<MessageRequest>): Flow<Message> = flow {
        val path = "v1/chat/completions"

        val response = client.post(settingProvider.getBaseUrl()) {
            url {
                appendPathSegments(path)
            }
            contentType(ContentType.Application.Json)
            setBody(
                LLamaMessageDto(
                    messages = messages,
                    stream = true
                )
            )
        }

        require(response.contentType()?.withoutParameters() == ContentType.Text.EventStream) {
            "Expected text/event-stream but got ${response.contentType()}"
        }

        val content = response.bodyAsChannel()
        val dataBuilder = StringBuilder()

        while (!content.isClosedForRead) {
            val line = content.readUTF8Line() ?: break

            if (line.startsWith("data:")) {
                val jsonString = line.substringAfter(":").trim()
                if (jsonString == "[DONE]") {
                    emit(
                        Message(
                            content = "",
                            sender = EnumSender.AI,
                            id = messages.last().id
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
                            id = messages.last().id
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
                    id = messages.last().id
                )
            )
        }


    }


}