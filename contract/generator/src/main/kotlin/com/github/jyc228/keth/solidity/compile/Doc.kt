package com.github.jyc228.keth.solidity.compile

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class Doc(
    val kind: String = "",
    val methods: Map<String, JsonObject> = emptyMap(),
    val version: Int = 0,
    val notice: String = "",
)
