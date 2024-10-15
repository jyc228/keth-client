package com.github.jyc228.keth.client

import com.github.jyc228.jsonrpc.JsonRpcClient
import com.github.jyc228.keth.client.eth.Block
import com.github.jyc228.keth.client.eth.BlockHeader
import com.github.jyc228.keth.client.eth.RpcBlock
import com.github.jyc228.keth.client.eth.RpcBlockHeader
import com.github.jyc228.keth.client.eth.RpcTransaction
import com.github.jyc228.keth.client.eth.SerializerConfig
import com.github.jyc228.keth.client.eth.Transaction
import com.github.jyc228.keth.client.eth.TransactionHashes
import com.github.jyc228.keth.client.eth.TransactionObjects
import com.github.jyc228.keth.solidity.AbiCodec
import com.github.jyc228.keth.type.Address
import com.github.jyc228.keth.type.createEthSerializersModule
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonBuilder
import kotlinx.serialization.modules.plus

data class EthereumClientConfig(
    var interval: Duration = 0.milliseconds,
    var adminJwtSecret: String? = null,
    var json: (JsonBuilder.() -> Unit)? = null,
    var blockHeaderSerializer: KSerializer<BlockHeader>? = null,
    var transactionSerializer: KSerializer<Transaction>? = null,
    var blockWithTxHashesSerializer: KSerializer<Block<TransactionHashes>>? = null,
    var blockWithTxObjectsSerializer: KSerializer<Block<TransactionObjects>>? = null
)

fun EthereumClient.Companion.fromRpcUrl(
    url: String,
    initConfig: (EthereumClientConfig.() -> Unit)? = null
): EthereumClient {
    val config = EthereumClientConfig().apply { initConfig?.invoke(this) }
    val serializerConfig = config.toSerializerConfig()
    val json = Json {
        ignoreUnknownKeys = true
        classDiscriminator = ""
        serializersModule += createEthSerializersModule(serializerConfig.transaction)
        config.json?.invoke(this)
    }
    val client = JsonRpcClient.from(url, config.adminJwtSecret)
    AbiCodec.registerPrimitiveTypeConverter("address") { Address.fromHexString(it.toString()) }
    if (config.interval.isPositive()) {
        return ScheduledBatchEthereumClient(client, config.interval, json, serializerConfig)
    }
    return DefaultEthereumClient(client, json, serializerConfig)
}

private fun EthereumClientConfig.toSerializerConfig() = SerializerConfig(
    blockHeader = blockHeaderSerializer ?: RpcBlockHeader.serializer() as KSerializer<BlockHeader>,
    transaction = transactionSerializer ?: RpcTransaction.serializer() as KSerializer<Transaction>,
    blockWithTxHashes = blockWithTxHashesSerializer
        ?: RpcBlock.serializer(TransactionHashes.serializer()) as KSerializer<Block<TransactionHashes>>,
    blockWithTxObjects = blockWithTxObjectsSerializer
        ?: RpcBlock.serializer(TransactionObjects.serializer()) as KSerializer<Block<TransactionObjects>>
)