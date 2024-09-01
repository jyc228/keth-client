package com.github.jyc228.keth.solidity

interface AbiCodec {
    fun decodeLog(inputs: List<AbiInput>, hex: String, topics: List<String>): Map<String, Any>
    fun decodeParameters(types: List<String>, hex: String): List<Any>
    fun encodeParameters(types: List<String>, parameters: List<*>): String
    fun encodeFunctionCall(abiItem: AbiItem, parameters: List<*>): String

    companion object : AbiCodec by AbiCodecImpl
}
