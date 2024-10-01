package com.github.jyc228.keth.client.eth

import com.github.jyc228.keth.type.createEthSerializersModule
import io.kotest.matchers.resource.resourceAsString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.plus

val defaultJson = Json {
    ignoreUnknownKeys = true
    classDiscriminator = ""
    serializersModule += createEthSerializersModule(null)
}

inline fun <reified T> decodeJsonResource(path: String, json: Json = defaultJson): T {
    return json.decodeFromString<T>(resourceAsString(path))
}

inline fun <reified T> encodeToJson(v: T, json: Json = defaultJson): String {
    return json.encodeToString(v)
}