package com.github.jyc228.keth.contract

import com.github.jyc228.keth.abi.Abi
import com.github.jyc228.keth.contract.Contract.GetEventRequest
import com.github.jyc228.keth.solidity.AbiItem
import com.github.jyc228.keth.type.Hash
import com.github.jyc228.keth.type.HexData
import com.github.jyc228.keth.type.Topics
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

    fun decodeIf(
        data: HexData,
        topics: List<HexData> = emptyList()
    ): EVENT? {
        return if (topics[0].hex == hash.hex) decode(data, topics) else null
    }

    fun decode(
        data: HexData,
        topics: List<HexData> = emptyList()
    ): EVENT {
        requireNotNull(event.primaryConstructor) { "${event.simpleName} primaryConstructor not exist" }
        val resultByName = Abi.decodeLog(abi.inputs, data.hex, topics.map { it.hex })
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
