package com.github.jyc228.keth.solidity.compile

import com.github.jyc228.keth.solidity.AbiItem
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class Metadata(
    val compiler: Compiler,
    val language: String,
    val output: Output,
    val settings: Settings,
    val sources: Map<String, JsonObject>,
    val version: Int
) {
    @Serializable
    data class Compiler(
        val version: String
    )

    @Serializable
    data class Output(
        val abi: List<AbiItem>,
        val devdoc: Doc,
        val userdoc: Doc
    )

    @Serializable
    data class Settings(
        val compilationTarget: Map<String, String>,
        val evmVersion: String,
        val libraries: JsonObject,
        val metadata: JsonObject,
        val optimizer: JsonObject,
        val remappings: List<String>
    )
}
