package com.github.jyc228.keth.client.contract

import com.github.jyc228.keth.client.eth.Log
import com.github.jyc228.keth.solidity.AbiCodec
import com.github.jyc228.keth.solidity.AbiCodecImpl
import com.github.jyc228.keth.solidity.IndexableAbiComponent
import com.github.jyc228.keth.type.Address

internal val abiCodec: AbiCodec = AbiCodecImpl().apply {
    registerPrimitiveTypeConverter("address") { Address(it as String) }
}

fun AbiCodec.decodeLog(
    inputs: List<IndexableAbiComponent>,
    log: Log
): Array<Any> = decodeLog(inputs, log.topics.map { it.hex }, log.data.hex)

fun AbiCodec.decodeLog(inputs: List<IndexableAbiComponent>, topics: List<String>, hex: String): Array<Any> {
    val (indexedInput, nonIndexedInput) = inputs.partition { it.indexed == true }
    val offset = if (indexedInput.size + 1 == topics.size) 1 else 0
    val nonIndexedDecodeResult =
        decode(nonIndexedInput.joinToString(",", prefix = "(", postfix = ")") { it.encodeType() }, hex) as List<*>
    return Array(inputs.size) {
        if (it < indexedInput.size) decode(indexedInput[it].encodeType(), topics[offset + it])
        else nonIndexedDecodeResult[it - indexedInput.size] as Any
    }
}
