package com.github.jyc228.keth.client.contract

import com.github.jyc228.keth.client.ApiResult
import com.github.jyc228.keth.client.eth.EthApi
import com.github.jyc228.keth.client.eth.GetLogsRequest
import com.github.jyc228.keth.client.eth.Log
import com.github.jyc228.keth.client.eth.Topics
import com.github.jyc228.keth.solidity.AbiItem
import com.github.jyc228.keth.type.Address
import org.intellij.lang.annotations.Language

interface Contract<ROOT_EVENT : ContractEvent> {
    suspend fun getLogs(options: (GetLogsRequest.() -> Unit)? = null): ApiResult<List<Pair<ROOT_EVENT, Log>>>

    suspend fun <EVENT : ROOT_EVENT> getLogs(
        vararg requests: GetEventRequest<EVENT>,
        options: (GetLogsRequest.() -> Unit)? = null
    ): ApiResult<List<Pair<EVENT, Log>>>
}

interface ContractEvent

class ContractAccessor<T : Contract<*>>(val address: Address, internal val factory: ContractFactory<T>)

abstract class ContractFactory<T : Contract<*>>(val create: (Address, EthApi) -> T) {
    protected fun encodeParameters(@Language("json") jsonAbi: String, vararg args: Any?): String {
        return abiCodec.encode(AbiItem.fromJson(jsonAbi).inputs, args.toList())
    }

    operator fun invoke(address: Address): ContractAccessor<T> = ContractAccessor(address, this)
    operator fun invoke(address: String): ContractAccessor<T> = ContractAccessor(Address(address), this)
}

class GetEventRequest<out EVENT : ContractEvent>(
    internal val factory: ContractEventFactory<out EVENT>,
    internal var onEach: ((@UnsafeVariance EVENT, Log) -> Unit)?,
    private var buildTopics: (GetEventRequest<@UnsafeVariance EVENT>.() -> Unit)?
) {
    var topics: Topics? = null

    internal fun buildTopics(topics: Topics) {
        this.topics = topics
        this.topics!!.filterByEvent(factory)
        this.buildTopics?.invoke(this)
    }
}
