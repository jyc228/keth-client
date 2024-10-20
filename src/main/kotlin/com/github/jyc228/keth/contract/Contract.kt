package com.github.jyc228.keth.contract

import com.github.jyc228.keth.client.ApiResult
import com.github.jyc228.keth.client.eth.EthApi
import com.github.jyc228.keth.client.eth.GetLogsRequest
import com.github.jyc228.keth.client.eth.Log
import com.github.jyc228.keth.client.eth.Topics
import com.github.jyc228.keth.solidity.AbiItem
import com.github.jyc228.keth.type.Address
import kotlinx.serialization.json.Json
import org.intellij.lang.annotations.Language

interface Contract<ROOT_EVENT : ContractEvent> {
    suspend fun getLogs(options: (GetLogsRequest.() -> Unit)? = null): ApiResult<List<Pair<ROOT_EVENT, Log>>>

    suspend fun getLogs(
        vararg requests: GetEventRequest<ROOT_EVENT>,
        options: (GetLogsRequest.() -> Unit)? = null
    ): ApiResult<List<Pair<ROOT_EVENT, Log>>>

    abstract class Factory<T : Contract<*>>(val create: (Address, EthApi) -> T) {
        protected fun encodeParameters(@Language("json") jsonAbi: String, vararg args: Any?): String {
            val abi: AbiItem = Json.decodeFromString(jsonAbi)
            return abiCodec.encode(abi.inputs, args.toList())
        }
    }

    class GetEventRequest<out EVENT : ContractEvent>(
        val factory: ContractEventFactory<out EVENT>,
        val buildTopic: Topics.() -> Unit,
        var subscribe: ((@UnsafeVariance EVENT) -> Unit)?
    )
}

interface ContractEvent
