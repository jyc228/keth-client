package com.github.jyc228.jsonrpc

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull

// https://www.jsonrpc.org/specification#request_object
@Serializable
data class JsonRpcRequest(
    val params: JsonElement,
    val method: String,
    val id: String,
    val jsonrpc: String = "2.0"
)

// https://www.jsonrpc.org/specification#response_object
@Serializable
data class JsonRpcResponse(
    val result: JsonElement = JsonNull,
    val error: JsonRpcError? = null,
    val id: String,
    val jsonrpc: String = "2.0"
)

@Serializable
data class JsonRpcError(
    val code: Int,
    val message: String,
    val data: JsonElement = JsonNull
)

data class JsonRpcException(
    val id: String,
    val error: JsonRpcError
) : RuntimeException("[$id] ${error.message} (${error.code})")
