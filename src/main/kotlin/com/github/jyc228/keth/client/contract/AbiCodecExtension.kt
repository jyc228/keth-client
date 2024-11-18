package com.github.jyc228.keth.client.contract

import com.github.jyc228.keth.client.eth.Log
import com.github.jyc228.keth.solidity.AbiCodec
import com.github.jyc228.keth.solidity.AbiCodecImpl
import com.github.jyc228.keth.solidity.AbiComponent
import com.github.jyc228.keth.solidity.IndexableAbiComponent
import com.github.jyc228.keth.type.Address
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.full.primaryConstructor

internal val abiCodec: AbiCodec = AbiCodecImpl().apply {
    registerPrimitiveTypeConverter("address") { Address(it as String) }
}

fun <T> AbiCodec.decodeLog(
    inputs: List<IndexableAbiComponent>,
    log: Log,
    constructor: KFunction<T>
): T {
    val results = decodeLog(inputs, log.topics.map { it.hex }, log.data.hex)
    val params = constructor.parameters.associateWith { readResult(inputs[it.index], it, results[it.index]) }
    return constructor.callBy(params)
}

fun AbiCodec.decodeLog(inputs: List<IndexableAbiComponent>, topics: List<String>, hex: String): Array<Any> {
    val (indexedInput, nonIndexedInput) = inputs.partition { it.indexed == true }
    val offset = if (indexedInput.size + 1 == topics.size) 1 else 0
    val nonIndexedDecodeResult =
        decode(nonIndexedInput.encodeType(), hex) as List<*>
    return Array(inputs.size) {
        if (it < indexedInput.size) decode(indexedInput[it].encodeType(), topics[offset + it])
        else nonIndexedDecodeResult[it - indexedInput.size] as Any
    }
}

fun AbiCodec.decodeFunctionCall(inputs: List<AbiComponent>, params: List<KParameter>, data: String): Array<Any> {
    val decodedParams = decode(inputs.encodeType(), data.drop(10)) as List<*>
    return Array(decodedParams.size) { i ->
        if (inputs[i].type == "tuple") readResult(inputs[i], params[i + 1], decodedParams[i]) as Any
        else decodedParams[i] as Any
    }
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

private fun List<AbiComponent>.encodeType() = joinToString(",", prefix = "(", postfix = ")") { it.encodeType() }