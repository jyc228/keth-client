package com.github.jyc228.keth.client

import com.github.jyc228.keth.client.contract.ContractApi
import com.github.jyc228.keth.client.engin.EngineApi
import com.github.jyc228.keth.client.eth.EthApi
import com.github.jyc228.keth.client.net.NetApi
import com.github.jyc228.keth.client.txpool.TxpoolApi
import com.github.jyc228.keth.client.web3.Web3Api

/**
 * The main entry point for interacting with the network. This class is thread-safe and is designed to be used one instance per node.
 *
 * You can create an instance of this class using the `EthereumClient(url)` function. The `url` parameter is the only required parameter and supports both HTTP and WebSocket protocols.
 * Other configurations can be set through callback parameters. For available options, see [EthereumClientConfig].
 * For detailed examples, refer to the `EthereumClient()` documentation.
 *
 * Each field corresponds to an RPC method namespace prefixed to the method, e.g., `eth_blockNumber -> eth.blockNumber()`.
 *
 * The [contract] field is an exception and exists to interact with the implementations of the automatically generated [com.github.jyc228.keth.client.contract.Contract].
 * For more details, refer to [ContractApi].
 *
 * All RPC call results are wrapped in [ApiResult]. If an error occurs during the RPC call, an exception is not thrown; instead, an [ApiResult] is returned with a failure status.
 * However, if an error occurs before or after the RPC call during preprocessing or postprocessing, an exception is thrown.
 *
 * Generally, each method corresponds to one RPC call. However, you can batch requests into a single call using the [batch] function. For more details, refer to the [batch] function documentation.
 */
interface EthereumClient {
    val web3: Web3Api
    val net: NetApi
    val eth: EthApi
    val engin: EngineApi
    val txpool: TxpoolApi
    val contract: ContractApi

    /**
     * Performs a batch request. Batch requests can be segmented based on the [EthereumClientConfig.batchSize] value.
     * If you need to use different RPCs in a batch request, you can use the [com.github.jyc228.keth.client.batch] extension functions.
     *
     * You must use the [EthereumClient] instance bound to `this`.
     * ```kotlin
     * val client = EthereumClient("https://... or wss://...")
     * client.eth.getHeaders(1uL..10uL) // 10 RPC calls
     * client.batch { eth.getHeaders(1uL..10uL) } // 1 RPC call
     * client.batch({ eth.blockNumber() }, { eth.gasPrice() })
     * // Do not use it like this: client.batch { client.eth.getHeaders(1uL..10uL) }
     * ```
     */
    suspend fun <R> batch(init: suspend EthereumClient.() -> List<ApiResult<R>>): List<ApiResult<R>>

    companion object
}

suspend fun <R1, R2> EthereumClient.batch(
    e1: BatchElement<R1>,
    e2: BatchElement<R2>,
): Pair<ApiResult<R1>, ApiResult<R2>> = batch(e1, e2) { r1, r2 -> r1 to r2 }

suspend fun <R1, R2, RESULT> EthereumClient.batch(
    e1: BatchElement<R1>,
    e2: BatchElement<R2>,
    transform: suspend (ApiResult<R1>, ApiResult<R2>) -> RESULT
): RESULT = execute(e1, e2).let { transform(it[0].cast(), it[1].cast()) }

suspend fun <R1, R2, R3> EthereumClient.batch(
    e1: BatchElement<R1>,
    e2: BatchElement<R2>,
    e3: BatchElement<R3>
): Triple<ApiResult<R1>, ApiResult<R2>, ApiResult<R3>> = batch(e1, e2, e3) { v1, v2, v3 -> Triple(v1, v2, v3) }

suspend fun <R1, R2, R3, RESULT> EthereumClient.batch(
    e1: BatchElement<R1>,
    e2: BatchElement<R2>,
    e3: BatchElement<R3>,
    transform: suspend (ApiResult<R1>, ApiResult<R2>, ApiResult<R3>) -> RESULT
): RESULT = execute(e1, e2, e3).let { transform(it[0].cast(), it[1].cast(), it[2].cast()) }

private suspend fun EthereumClient.execute(vararg e: BatchElement<Any?>): List<ApiResult<Any?>> {
    return batch { e.map { it(this) } }
}

private typealias BatchElement<T> = suspend EthereumClient.() -> ApiResult<T>

@Suppress("UNCHECKED_CAST")
private fun <E> ApiResult<Any?>.cast(): ApiResult<E> = this as ApiResult<E>