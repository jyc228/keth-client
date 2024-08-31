package com.github.jyc228.keth.client

import com.github.jyc228.jsonrpc.JsonRpcClient
import com.github.jyc228.keth.type.UnknownTransactionSerializer
import com.github.jyc228.keth.type.createEthSerializersModule
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonBuilder
import kotlinx.serialization.modules.plus

data class EthereumClientConfig(
    var interval: Duration = 0.milliseconds,
    var adminJwtSecret: String? = null,
    var json: (JsonBuilder.() -> Unit)? = null,
    var unknownTransactionSerializer: UnknownTransactionSerializer? = null
)

fun EthereumClient.Companion.fromRpcUrl(
    url: String,
    initConfig: (EthereumClientConfig.() -> Unit)? = null
): EthereumClient {
    val config = EthereumClientConfig().apply { initConfig?.invoke(this) }
    val json = Json {
        ignoreUnknownKeys = true
        classDiscriminator = ""
        serializersModule += createEthSerializersModule(config.unknownTransactionSerializer)
        config.json?.invoke(this)
    }
    if (config.interval.isPositive()) {
        return ScheduledBatchEthereumClient(JsonRpcClient.from(url, config.adminJwtSecret), config.interval, json)
    }
    return DefaultEthereumClient(JsonRpcClient.from(url, config.adminJwtSecret), json)
}
