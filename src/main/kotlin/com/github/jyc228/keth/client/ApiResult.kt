package com.github.jyc228.keth.client

import com.github.jyc228.jsonrpc.JsonRpcError
import com.github.jyc228.jsonrpc.JsonRpcException
import com.github.jyc228.jsonrpc.JsonRpcRequest
import com.github.jyc228.jsonrpc.JsonRpcResponse
import kotlinx.coroutines.channels.Channel
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull

suspend fun <T> List<ApiResult<out T>>.awaitAllOrThrow(): List<T> = map { it.awaitOrThrow() }

fun <T> ApiResult(
    response: JsonRpcResponse,
    decode: (JsonElement) -> T
): ApiResult<T> {
    if (response.error != null) {
        return ApiResultFail(response.id, response.error)
    }
    if (response.result is JsonNull) return ApiResultSuccess(null as T)
    return ApiResultSuccess(decode(response.result))
}

interface ApiResult<T> {
    suspend fun awaitOrNull(): T?
    suspend fun awaitOrThrow(): T

    fun <R> map(transform: (T) -> R): ApiResult<R>
    fun onFailure(handleError: (Throwable) -> Unit): ApiResult<T>

    companion object
}

internal data class ApiResultSuccess<T>(val data: T) : ApiResult<T> {
    override suspend fun awaitOrNull(): T? = data
    override suspend fun awaitOrThrow(): T = data
    override fun <R> map(transform: (T) -> R): ApiResult<R> = ApiResultSuccess(transform(data))
    override fun onFailure(handleError: (Throwable) -> Unit): ApiResult<T> = this
    override fun toString(): String = data.toString()
}

@Suppress("UNCHECKED_CAST")
internal class ApiResultFail<T>(
    private val id: String,
    val error: JsonRpcError
) : ApiResult<T> {
    private fun exception() = JsonRpcException(id, error)
    override suspend fun awaitOrNull(): T? = null
    override suspend fun awaitOrThrow(): T = throw exception()
    override fun <R> map(transform: (T) -> R): ApiResult<R> = this as ApiResult<R>
    override fun onFailure(handleError: (Throwable) -> Unit): ApiResult<T> = apply { handleError(exception()) }
    override fun toString(): String = error.toString()
}

class DeferredApiResult<T>(
    val request: JsonRpcRequest,
    val onResponse: Channel<JsonRpcResponse>,
    val decode: suspend (JsonRpcResponse) -> ApiResult<T>
) : ApiResult<T> {
    override suspend fun awaitOrThrow() = decode(onResponse.receive()).awaitOrThrow()
    override suspend fun awaitOrNull() = decode(onResponse.receive()).awaitOrNull()
    override fun <R> map(transform: (T) -> R): ApiResult<R> =
        DeferredApiResult(request, onResponse) { decode(it).map(transform) }

    override fun onFailure(handleError: (Throwable) -> Unit): ApiResult<T> =
        DeferredApiResult(request, onResponse) { decode(it).onFailure(handleError) }
}
