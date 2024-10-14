package com.github.jyc228.keth.client

import com.github.jyc228.keth.client.contract.ContractApi
import com.github.jyc228.keth.client.engin.EngineApi
import com.github.jyc228.keth.client.eth.EthApi
import com.github.jyc228.keth.client.txpool.TxpoolApi

interface EthereumClient {
    val eth: EthApi
    val engin: EngineApi
    val txpool: TxpoolApi
    val contract: ContractApi
    suspend fun <R> batch(init: suspend EthereumClient.() -> List<ApiResult<R>>): List<ApiResult<R>>

    companion object
}

suspend fun <R1, R2> EthereumClient.batch2(
    e1: BatchElement<R1>,
    e2: BatchElement<R2>,
): Pair<ApiResult<R1>, ApiResult<R2>> = batch2(e1, e2) { r1, r2 -> r1 to r2 }

@Suppress("UNCHECKED_CAST")
suspend fun <R1, R2, RESULT> EthereumClient.batch2(
    e1: BatchElement<R1>,
    e2: BatchElement<R2>,
    transform: suspend (ApiResult<R1>, ApiResult<R2>) -> RESULT
): RESULT = batch(e1, e2).let { transform(it[0] as ApiResult<R1>, it[1] as ApiResult<R2>) }

suspend fun <R1, R2, R3> EthereumClient.batch3(
    e1: BatchElement<R1>,
    e2: BatchElement<R2>,
    e3: BatchElement<R3>
): Triple<ApiResult<R1>, ApiResult<R2>, ApiResult<R3>> = batch3(e1, e2, e3) { v1, v2, v3 -> Triple(v1, v2, v3) }

@Suppress("UNCHECKED_CAST")
suspend fun <R1, R2, R3, RESULT> EthereumClient.batch3(
    e1: BatchElement<R1>,
    e2: BatchElement<R2>,
    e3: BatchElement<R3>,
    transform: suspend (ApiResult<R1>, ApiResult<R2>, ApiResult<R3>) -> RESULT
): RESULT = batch(e1, e2, e3).let { transform(it[0] as ApiResult<R1>, it[1] as ApiResult<R2>, it[2] as ApiResult<R3>) }

private suspend fun EthereumClient.batch(vararg e: BatchElement<Any?>): List<ApiResult<Any?>> {
    return batch { e.map { it(this) } }
}

private typealias BatchElement<T> = suspend EthereumClient.() -> ApiResult<T>
