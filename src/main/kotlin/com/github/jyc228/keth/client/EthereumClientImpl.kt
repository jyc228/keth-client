package com.github.jyc228.keth.client

import com.github.jyc228.jsonrpc.JsonRpcClient
import com.github.jyc228.keth.client.contract.ContractApi
import com.github.jyc228.keth.client.contract.EthContractApi
import com.github.jyc228.keth.client.engin.EngineApi
import com.github.jyc228.keth.client.engin.EngineJsonRpcApi
import com.github.jyc228.keth.client.eth.EthApi
import com.github.jyc228.keth.client.eth.EthJsonRpcApi
import com.github.jyc228.keth.client.eth.SerializerConfig
import com.github.jyc228.keth.client.txpool.TxpoolApi
import com.github.jyc228.keth.client.txpool.TxpoolJsonRpcApi
import kotlin.time.Duration
import kotlinx.serialization.json.Json

class DefaultEthereumClient(
    private val client: JsonRpcClient,
    private val json: Json,
    private val serializerConfig: SerializerConfig,
    private val batchSize: UInt?,
) : EthereumClient {
    private val immediateCall = ImmediateJsonRpcClient(client, json)
    override val eth: EthApi = EthJsonRpcApi(immediateCall, serializerConfig)
    override val engin: EngineApi = EngineJsonRpcApi(immediateCall)
    override val txpool: TxpoolApi = TxpoolJsonRpcApi(immediateCall)
    override val contract = EthContractApi(eth)

    override suspend fun <R> batch(init: suspend EthereumClient.() -> List<ApiResult<R>>): List<ApiResult<R>> {
        return BatchEthereumClient(client, json, serializerConfig, batchSize).batch(init)
    }
}

class BatchEthereumClient(
    client: JsonRpcClient,
    json: Json,
    serializerConfig: SerializerConfig,
    batchSize: UInt?,
) : EthereumClient {
    private val batchCall = BatchJsonRpcClient(client, json, batchSize)
    override val eth: EthApi = EthJsonRpcApi(batchCall, serializerConfig)
    override val engin: EngineApi = EngineJsonRpcApi(batchCall)
    override val txpool: TxpoolApi = TxpoolJsonRpcApi(batchCall)
    override val contract: ContractApi = EthContractApi(eth)
    override suspend fun <R> batch(init: suspend EthereumClient.() -> List<ApiResult<R>>) =
        batchCall.execute(init(this))
}

class ScheduledBatchEthereumClient(
    client: JsonRpcClient,
    interval: Duration,
    json: Json,
    serializerConfig: SerializerConfig,
    batchSize: UInt?,
) : EthereumClient {
    private val scheduledCall = ScheduledJsonRpcClient(client, json, interval, batchSize)
    override val eth: EthApi = EthJsonRpcApi(scheduledCall, serializerConfig)
    override val engin: EngineApi = EngineJsonRpcApi(scheduledCall)
    override val txpool: TxpoolApi = TxpoolJsonRpcApi(scheduledCall)
    override val contract: ContractApi = EthContractApi(eth)
    override suspend fun <R> batch(init: suspend EthereumClient.() -> List<ApiResult<R>>) = init(this)
}
