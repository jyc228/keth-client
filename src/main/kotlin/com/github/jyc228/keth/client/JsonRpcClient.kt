package com.github.jyc228.keth.client

import com.github.jyc228.jsonrpc.JsonRpcRequest
import com.github.jyc228.jsonrpc.KtorJsonRpcClient
import java.util.concurrent.atomic.AtomicLong
import kotlin.math.min
import kotlin.time.Duration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

sealed class JsonRpcClient {
    abstract suspend fun <T> send(
        method: String,
        params: JsonElement,
        resultSerializer: KSerializer<T>
    ): ApiResult<T>

    abstract fun toImmediateClient(): JsonRpcClient
}

class ImmediateJsonRpcClient(private val client: KtorJsonRpcClient, private val json: Json) : JsonRpcClient() {
    override suspend fun <T> send(
        method: String,
        params: JsonElement,
        resultSerializer: KSerializer<T>
    ): ApiResult<T> {
        val request = JsonRpcRequest(params, method, method)
        return ApiResult(client.send(request)) { json.decodeFromJsonElement(resultSerializer, it) }
    }

    override fun toImmediateClient(): JsonRpcClient = this
}

sealed class DeferredJsonRpcClient(protected val client: KtorJsonRpcClient, private val json: Json) : JsonRpcClient() {
    private val idGenerator = AtomicLong()

    override suspend fun <T> send(
        method: String,
        params: JsonElement,
        resultSerializer: KSerializer<T>
    ): DeferredApiResult<T> {
        val request = JsonRpcRequest(params, method, "$method::${idGenerator.getAndIncrement()}")
        return DeferredApiResult(request, Channel(1)) { response ->
            ApiResult(response) { json.decodeFromJsonElement(resultSerializer, it) }
        }
    }

    protected suspend fun executeAndSendResult(calls: List<DeferredApiResult<*>>) {
        val response = client.sendBatch(calls.map { it.request })
        calls.onEachIndexed { index, call ->
            call.onResponse.send(when (call.request.id == response[index].id) {
                true -> response[index]
                false -> response.first { call.request.id == it.id }
            })
        }
    }

    override fun toImmediateClient(): JsonRpcClient = ImmediateJsonRpcClient(client, json)
}

class BatchJsonRpcClient(client: KtorJsonRpcClient, json: Json) : DeferredJsonRpcClient(client, json) {
    @Suppress("UNCHECKED_CAST")
    suspend fun <T> execute(calls: List<ApiResult<T>>): List<ApiResult<T>> {
        executeAndSendResult(calls as List<DeferredApiResult<T>>)
        return calls
    }
}

class ScheduledJsonRpcClient(
    client: KtorJsonRpcClient,
    json: Json,
    private val interval: Duration,
    private val maxBatchSize: Int = 999
) : DeferredJsonRpcClient(client, json) {
    private val calls = mutableListOf<DeferredApiResult<*>>()
    private val mutex = Mutex()
    private val job = CoroutineScope(Dispatchers.IO).launch {
        while (isActive) {
            delay(interval)
            val calls = collectCalls().takeIf { it.isNotEmpty() } ?: continue
            launch { executeAndSendResult(calls) }
        }
    }

    override suspend fun <T> send(
        method: String,
        params: JsonElement,
        resultSerializer: KSerializer<T>
    ): DeferredApiResult<T> {
        return super.send(method, params, resultSerializer).also { mutex.withLock { calls += it } }
    }

    private suspend fun collectCalls() = mutex.withLock {
        val size = min(calls.size, maxBatchSize)
        buildList(size) { repeat(size) { add(calls.removeFirst()) } }
    }
}
