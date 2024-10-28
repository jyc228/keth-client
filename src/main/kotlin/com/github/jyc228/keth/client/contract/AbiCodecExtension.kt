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
): Map<String, Any> = decodeLog(inputs, log.data.hex, log.topics.map { it.hex })

fun AbiCodec.decodeLog(inputs: List<IndexableAbiComponent>, hex: String, topics: List<String>): Map<String, Any> {
    val (indexedInput, nonIndexedInput) = inputs.partition { it.indexed == true }
    val offset = if (indexedInput.size + 1 == topics.size) 1 else 0
    return buildMap(inputs.size) {
        indexedInput.forEachIndexed { index, abiInput ->
            this[abiInput.name] = decode(abiInput, topics[index + offset])
        }
        this += decode(nonIndexedInput, hex)
    }
}
