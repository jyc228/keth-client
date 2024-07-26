package com.github.jyc228.keth.solidity.compile

import com.github.jyc228.keth.solidity.AbiItem
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

@Serializable
class Deployment(
    val address: String,
    val abi: List<AbiItem>,
    val args: List<String>,
    val bytecode: String,
    val deployedBytecode: String,
    val devdoc: Doc,
    val metadata: String,
    val numDeployments: Int,
    val receipt: Map<String, JsonElement>,
    val solcInputHash: String,
    val storageLayout: StorageLayout? = null,
    val transactionHash: String,
    val userdoc: Doc
) {
    val metadataObject: Metadata = Json { ignoreUnknownKeys = true }.decodeFromString(metadata)


    @Serializable
    data class StorageLayout(
        val storage: List<Storage>,
        val types: Map<String, JsonObject>
    )

    @Serializable
    data class Storage(
        val astId: Int,
        val contract: String,
        val label: String,
        val offset: Int,
        val slot: String,
        val type: String
    )
}
