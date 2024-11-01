package com.github.jyc228.jsonrpc

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.sendSerialized
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.serialization.kotlinx.KotlinxWebsocketSerializationConverter
import io.ktor.websocket.WebSocketSession
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

internal class JsonRpcKtorWebSocketClient(
    private val url: String,
    private val http: HttpClient
) : JsonRpcClient, AutoCloseable {
    constructor(url: String) : this(url, webSocketClient())

    private val requestChannel = mutableMapOf<String, Channel<JsonRpcResponse>>()
    private val session = http.async { http.webSocketSession(url) { }.apply { handleIncoming() } }

    private fun WebSocketSession.handleIncoming() = launch {
        for (frame in incoming) {
            val json = frame.data.decodeToString().trim()
            if (json.startsWith("[") && json.endsWith("]")) {
                Json.decodeFromString<List<JsonRpcResponse>>(json).forEach { requestChannel[it.id]?.send(it) }
            } else {
                val result = Json.decodeFromString<JsonRpcResponse>(json)
                requestChannel[result.id]?.send(result)
            }
        }
    }

    override suspend fun send(request: JsonRpcRequest): JsonRpcResponse {
        val responseChannel = Channel<JsonRpcResponse>()
        requestChannel[request.id] = responseChannel
        session.await().sendSerialized(request)
        return responseChannel.receive().apply { requestChannel -= request.id }
    }

    override suspend fun sendBatch(requests: List<JsonRpcRequest>): List<JsonRpcResponse> {
        requests.forEach { requestChannel[it.id] = Channel() }
        session.await().sendSerialized(requests)
        return requests.map { requestChannel[it.id]!!.receive().apply { requestChannel -= it.id } }
    }

    companion object {
        private fun webSocketClient() = HttpClient(CIO) {
            install(WebSockets) {
                contentConverter = KotlinxWebsocketSerializationConverter(Json {
                    encodeDefaults = true
                })
            }
        }
    }

    override fun close() {
        session.cancel()
    }
}