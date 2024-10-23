package com.github.jyc228.keth.client.contract

import com.github.jyc228.keth.client.eth.Log
import com.github.jyc228.keth.client.eth.Topics
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
    val eventSig = Hash(hash)
    private val const = requireNotNull(event.primaryConstructor) { "${event.simpleName} primaryConstructor not exist" }
    private val abi: AbiItem by lazy(LazyThreadSafetyMode.NONE) { Json.decodeFromString(jsonAbi()) }

    fun decodeIf(log: Log): EVENT? = if (log.topics.getOrNull(0)?.hex == eventSig.hex) decode(log) else null

    fun decode(log: Log): EVENT {
        val resultByName = abiCodec.decodeLog(abi.inputs, log)
        val params = const.parameters.associateWith { p -> resultByName[p.name] }
        return const.callBy(params)
    }
}

inline fun <E : ContractEvent, reified F : ContractEventFactory<E>> F.filter(crossinline buildTopic: Topics.() -> Unit): Contract.GetEventRequest<E> {
    return Contract.GetEventRequest(this, { filterByEvent(this@filter).buildTopic() }, null)
}

fun <E : ContractEvent, F : ContractEventFactory<E>> F.onEach(callback: (E) -> Unit): Contract.GetEventRequest<E> {
    return Contract.GetEventRequest(this, { filterByEvent(this@onEach) }, callback)
}

fun <E : ContractEvent> Contract.GetEventRequest<E>.onEach(callback: (E) -> Unit): Contract.GetEventRequest<E> {
    return apply { this.onEach = callback }
}
