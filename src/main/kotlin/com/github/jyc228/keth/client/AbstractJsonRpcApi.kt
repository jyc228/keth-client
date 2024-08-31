package com.github.jyc228.keth.client

import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.serializer

abstract class AbstractJsonRpcApi(val client: JsonRpcClientWrapper) {
    protected suspend inline operator fun <reified T> String.invoke(): ApiResult<T> {
        return client.send(this, JsonNull, serializer())
    }

    protected suspend inline operator fun <reified T, reified P1> String.invoke(p1: P1): ApiResult<T> {
        val inputs = listOf(Json.encodeToJsonElement(p1))
        return client.send(this, JsonArray(inputs), serializer())
    }

    protected suspend inline operator fun <reified T, reified P1, reified P2> String.invoke(
        p1: P1,
        p2: P2,
        serializer: KSerializer<T> = serializer()
    ): ApiResult<T> {
        val inputs = listOf(Json.encodeToJsonElement(p1), Json.encodeToJsonElement(p2))
        return client.send(this, JsonArray(inputs), serializer)
    }

    protected suspend inline operator fun <reified T, reified P1, reified P2, reified P3> String.invoke(
        p1: P1,
        p2: P2,
        p3: P3
    ): ApiResult<T> {
        val inputs = listOf(Json.encodeToJsonElement(p1), Json.encodeToJsonElement(p2), Json.encodeToJsonElement(p3))
        return client.send(this, JsonArray(inputs), serializer())
    }
}
