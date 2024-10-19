package com.github.jyc228.keth.contract

import com.github.jyc228.keth.client.eth.Log
import com.github.jyc228.keth.client.eth.Topics
import com.github.jyc228.keth.contract.Contract.GetEventRequest
import com.github.jyc228.keth.solidity.AbiCodec
import com.github.jyc228.keth.solidity.AbiItem
import com.github.jyc228.keth.type.Hash
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor
import kotlinx.serialization.json.Json

abstract class ContractEventFactory<EVENT : ContractEvent>(
    private val event: KClass<EVENT>,
    hash: String,
    jsonAbi: () -> String,
) {
    val hash = Hash.fromHexString(hash)
    private val const = requireNotNull(event.primaryConstructor) { "${event.simpleName} primaryConstructor not exist" }
    private val abi: AbiItem by lazy(LazyThreadSafetyMode.NONE) { Json.decodeFromString(jsonAbi()) }

    fun decodeIf(log: Log): EVENT? = if (log.topics.getOrNull(0)?.hex == hash.hex) decode(log) else null

    fun decode(log: Log): EVENT {
        val resultByName = AbiCodec.decodeLog(abi.inputs, log)
        val params = const.parameters.associateWith { p -> resultByName[p.name] }
        return const.callBy(params)
    }
}

inline fun <E : ContractEvent, reified F : ContractEventFactory<E>> F.filter(crossinline buildTopic: Topics.() -> Unit): GetEventRequest<E> {
    return GetEventRequest(this, { filterByEvent(this@filter).buildTopic() }, null)
}

fun <E : ContractEvent, F : ContractEventFactory<E>> F.subscribe(subscribe: (E) -> Unit): GetEventRequest<E> {
    return GetEventRequest(this, { filterByEvent(this@subscribe) }, subscribe)
}

fun <E : ContractEvent> GetEventRequest<E>.subscribe(subscribe: (E) -> Unit): GetEventRequest<E> {
    return apply { this.subscribe = subscribe }
}
