package com.github.jyc228.keth.contract

import com.github.jyc228.keth.abi.Abi
import com.github.jyc228.keth.solidity.AbiItem
import com.github.jyc228.keth.type.HexData
import com.github.jyc228.keth.type.HexString
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.isSuperclassOf
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor
import kotlinx.serialization.json.Json

abstract class ContractEventFactory<EVENT : ContractEvent, INDEXED : Any>(
    private val event: KClass<EVENT>,
    private val indexed: KClass<INDEXED>,
    val hash: String,
    jsonAbi: () -> String,
) : (INDEXED.() -> Unit) -> List<String?> {
    private val const = requireNotNull(event.primaryConstructor) { "${event.simpleName} primaryConstructor not exist" }
    private val abi: AbiItem by lazy(LazyThreadSafetyMode.NONE) { Json.decodeFromString(jsonAbi()) }

    override fun invoke(indexedParameter: INDEXED.() -> Unit): List<String> {
        return emptyList()
    }

    fun buildTopics(init: (INDEXED.() -> Unit)? = null): List<String?> {
        if (init == null) {
            return listOf(hash)
        }
        return buildList {
            add(hash)
            val instance = indexed.createInstance().apply(init)
            indexed.memberProperties.forEach { p ->
                when (val v = p.get(instance)) {
                    null -> add(null)
                    else -> when (HexString::class.isSuperclassOf(p.returnType.classifier as KClass<*>)) {
                        true -> add((v as HexString).hex.replaceFirst("0x", "0x000000000000000000000000"))
                        false -> TODO()
                    }
                }
            }
        }
    }

    fun createIndexedInstance() = indexed.constructors.first().call()

    fun decodeIf(
        data: HexData,
        topics: List<HexData> = emptyList()
    ): EVENT? {
        return if (topics[0].hex == hash) decode(data, topics) else null
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
