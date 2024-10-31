package com.github.jyc228.keth.client.contract

import com.github.jyc228.keth.client.eth.Log
import com.github.jyc228.keth.solidity.AbiComponent
import com.github.jyc228.keth.solidity.AbiItem
import com.github.jyc228.keth.type.Hash
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.full.primaryConstructor

abstract class ContractEventFactory<EVENT : ContractEvent>(
    private val event: KClass<EVENT>,
    hash: String,
    jsonAbi: () -> String,
) {
    val eventSig = Hash(hash)
    private val const = requireNotNull(event.primaryConstructor) { "${event.simpleName} primaryConstructor not exist" }
    private val abi by lazy(LazyThreadSafetyMode.NONE) { AbiItem.fromJson(jsonAbi()) }

    fun decodeIf(log: Log): EVENT? = if (log.topics.getOrNull(0)?.hex == eventSig.hex) decode(log) else null

    fun decode(log: Log): EVENT {
        val results = abiCodec.decodeLog(abi.inputs, log)
        val params = const.parameters.associateWith { readResult(abi.inputs[it.index], it, results[it.index]) }
        return const.callBy(params)
    }

    private fun readResult(abi: AbiComponent, p: KParameter, value: Any?): Any? {
        if (abi.type == "tuple") {
            val tuple = value as List<*>
            val const = (p.type.classifier as KClass<*>).primaryConstructor!!
            val params = const.parameters.associateWith {
                readResult(abi.components[it.index], it, tuple[it.index])
            }
            return const.callBy(params)
        }
        return value
    }
}

inline fun <E : ContractEvent, reified F : ContractEventFactory<E>> F.filter(noinline buildTopic: GetEventRequest<E>.() -> Unit): GetEventRequest<E> {
    return GetEventRequest(this, null, buildTopic)
}

fun <E : ContractEvent, F : ContractEventFactory<E>> F.onEach(callback: (E, Log) -> Unit): GetEventRequest<E> {
    return GetEventRequest(this, callback, null)
}

fun <E : ContractEvent> GetEventRequest<E>.onEach(callback: (E, Log) -> Unit): GetEventRequest<E> {
    return apply { this.onEach = callback }
}
