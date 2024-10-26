package com.github.jyc228.keth.client

import com.github.jyc228.jsonrpc.JsonRpcClient
import com.github.jyc228.keth.client.eth.Block
import com.github.jyc228.keth.client.eth.BlockHeader
import com.github.jyc228.keth.client.eth.RpcBlock
import com.github.jyc228.keth.client.eth.RpcBlockHeader
import com.github.jyc228.keth.client.eth.RpcTransaction
import com.github.jyc228.keth.client.eth.SerializerConfig
import com.github.jyc228.keth.client.eth.Transaction
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonBuilder

fun EthereumClient(url: String, initConfig: (EthereumClientConfig.() -> Unit)? = null): EthereumClient {
    val config = EthereumClientConfig().apply { initConfig?.invoke(this) }
    val json = Json {
        ignoreUnknownKeys = true
        classDiscriminator = ""
        config.json?.invoke(this)
    }
    if (config.interval.isPositive()) {
        return ScheduledBatchEthereumClient(
            client = JsonRpcClient(url, config.adminJwtSecret),
            interval = config.interval,
            json = json,
            serializerConfig = config.toSerializerConfig(),
            batchSize = config.batchSize,
        )
    }
    return DefaultEthereumClient(
        client = JsonRpcClient(url, config.adminJwtSecret),
        json = json,
        serializerConfig = config.toSerializerConfig(),
        batchSize = config.batchSize,
    )
}

data class EthereumClientConfig(
    var interval: Duration = 0.milliseconds,
    var batchSize: UInt? = 100u,
    var adminJwtSecret: String? = null,
    var json: (JsonBuilder.() -> Unit)? = null,
    var blockHeaderSerializer: KSerializer<out BlockHeader>? = null,
    var transactionSerializer: KSerializer<out Transaction>? = null,
    var blockWithTxHashesSerializer: KSerializer<out Block<Block.TransactionHash>>? = null,
    var blockWithTxObjectsSerializer: KSerializer<out Block<Block.TransactionObject>>? = null
)

private fun EthereumClientConfig.toSerializerConfig() = SerializerConfig(
    blockHeader = blockHeaderSerializer ?: RpcBlockHeader.serializer(),
    transaction = transactionSerializer ?: RpcTransaction.serializer(),
    blockWithTxHashes = blockWithTxHashesSerializer ?: RpcBlock.serializer(Block.TransactionHash.serializer()),
    blockWithTxObjects = blockWithTxObjectsSerializer ?: RpcBlock.serializer(Block.TransactionObject.serializer())
)