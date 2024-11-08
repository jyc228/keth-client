package com.github.jyc228.keth.client

import com.github.jyc228.jsonrpc.JsonRpcClient
import com.github.jyc228.keth.client.contract.EthContractApi
import com.github.jyc228.keth.client.engin.EngineApi
import com.github.jyc228.keth.client.engin.EngineJsonRpcApi
import com.github.jyc228.keth.client.eth.EthApi
import com.github.jyc228.keth.client.eth.EthJsonRpcApi
import com.github.jyc228.keth.client.eth.SerializerConfig
import com.github.jyc228.keth.client.net.NetApi
import com.github.jyc228.keth.client.net.NetJsonRpcApi
import com.github.jyc228.keth.client.txpool.TxpoolApi
import com.github.jyc228.keth.client.txpool.TxpoolJsonRpcApi
import com.github.jyc228.keth.client.web3.Web3Api
import com.github.jyc228.keth.client.web3.Web3JsonRpcApi
import kotlin.time.Duration
import kotlinx.serialization.json.Json

abstract class AbstractEthereumClient(
    wrapper: JsonRpcClientWrapper,
    serializerConfig: SerializerConfig
) : EthereumClient {
    override val web3: Web3Api = Web3JsonRpcApi(wrapper)
    override val net: NetApi = NetJsonRpcApi(wrapper)
    override val eth: EthApi = EthJsonRpcApi(wrapper, serializerConfig)
    override val engin: EngineApi = EngineJsonRpcApi(wrapper)
    override val txpool: TxpoolApi = TxpoolJsonRpcApi(wrapper)
    override val contract = EthContractApi(eth)
}

class DefaultEthereumClient(
    private val client: JsonRpcClient,
    private val json: Json,
    private val serializerConfig: SerializerConfig,
    private val batchSize: UInt?,
) : AbstractEthereumClient(ImmediateJsonRpcClient(client, json), serializerConfig) {
    override suspend fun <R> batch(init: suspend EthereumClient.() -> List<ApiResult<R>>): List<ApiResult<R>> {
        return BatchEthereumClient(client, json, serializerConfig, batchSize).batch(init)
    }
}

class BatchEthereumClient(
    client: JsonRpcClient,
    json: Json,
    serializerConfig: SerializerConfig,
    batchSize: UInt?,
    private val wrapper: BatchJsonRpcClient = BatchJsonRpcClient(client, json, batchSize)
) : AbstractEthereumClient(wrapper, serializerConfig) {
    override suspend fun <R> batch(init: suspend EthereumClient.() -> List<ApiResult<R>>) = wrapper.execute(init(this))
}

class ScheduledBatchEthereumClient(
    client: JsonRpcClient,
    interval: Duration,
    json: Json,
    serializerConfig: SerializerConfig,
    batchSize: UInt?,
) : AbstractEthereumClient(ScheduledJsonRpcClient(client, json, interval, batchSize), serializerConfig) {
    override suspend fun <R> batch(init: suspend EthereumClient.() -> List<ApiResult<R>>) = init(this)
}
