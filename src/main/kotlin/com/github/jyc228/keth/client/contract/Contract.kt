package com.github.jyc228.keth.client.contract

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

    suspend fun <EVENT : ROOT_EVENT> getLogs(
        vararg requests: GetEventRequest<EVENT>,
        options: (GetLogsRequest.() -> Unit)? = null
    ): ApiResult<List<Pair<EVENT, Log>>>

    abstract class Factory<T : Contract<*>>(val create: (Address, EthApi) -> T) {
        protected fun encodeParameters(@Language("json") jsonAbi: String, vararg args: Any?): String {
            val abi: AbiItem = Json.decodeFromString(jsonAbi)
            return abiCodec.encode(abi.inputs, args.toList())
        }

        operator fun invoke(address: Address): ContractAccessor<T> = ContractAccessor(address, this)
        operator fun invoke(address: String): ContractAccessor<T> = ContractAccessor(Address(address), this)
    }

    class GetEventRequest<out EVENT : ContractEvent>(
        internal val factory: ContractEventFactory<out EVENT>,
        internal val buildTopic: Topics.() -> Unit,
        internal var onEach: ((@UnsafeVariance EVENT, Log) -> Unit)?
    )
}

interface ContractEvent

class ContractAccessor<T : Contract<*>>(val address: Address, internal val factory: Contract.Factory<T>)