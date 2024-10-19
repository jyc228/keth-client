package com.github.jyc228.keth.solidity

import java.nio.ByteBuffer

object AbiCodecImpl : AbiCodec {
    override fun decode(component: AbiComponent, hex: String): Any {
        return Codec.decode(Type.of(component.encodeType()), hexToByteBuffer(hex))
    }

    override fun decode(components: List<AbiComponent>, hex: String): Map<String, Any> {
        val result = TupleCodec.decode(components.toTupleType(), hexToByteBuffer(hex))
        return convertToMap(components, result)
    }

    @OptIn(ExperimentalStdlibApi::class)
    override fun encode(component: AbiComponent, value: Any): String {
        return Codec.encode(Type.of(component.encodeType()), value).toHexString()
    }

    @OptIn(ExperimentalStdlibApi::class)
    override fun encode(components: List<AbiComponent>, values: List<*>): String {
        return Codec.encode(components.toTupleType(), values).toHexString()
    }

    private fun convertToMap(components: List<AbiComponent>, result: List<*>): Map<String, Any> {
        return components.withIndex().associateBy({ (_, c) -> c.name }, { (i, c) ->
            when (c.type) {
                "tuple" -> convertToMap(c.components, result[i] as List<*>)
                else -> result[i] as Any
            }
        })
    }

    override fun decodeLog(inputs: List<AbiInput>, hex: String, topics: List<String>): Map<String, Any> {
        val types = inputs.fold(LogTypes()) { types, abi -> types.add(abi) }
        val indexedResult = types.indexed.mapIndexed { i, t -> Codec.decode(t, topics[i + 1]) }.iterator()
        val nonIndexedResult = TupleCodec.decode(TupleType(types.nonIndexed), hexToByteBuffer(hex)).iterator()
        return buildMap(inputs.size) {
            for (input in inputs) {
                this[input.name] = when (input.indexed == true) {
                    true -> indexedResult
                    false -> nonIndexedResult
                }.next()
            }
            require(!indexedResult.hasNext() && !nonIndexedResult.hasNext())
        }
    }

    override fun decodeParameters(types: List<String>, hex: String): List<Any> {
        return TupleCodec.decode(TupleType(types.map(Type::of)), hexToByteBuffer(hex))
    }

    @OptIn(ExperimentalStdlibApi::class)
    private fun hexToByteBuffer(hex: String) = ByteBuffer.wrap(hex.removePrefix("0x").hexToByteArray())

    @OptIn(ExperimentalStdlibApi::class)
    override fun encodeParameters(types: List<String>, parameters: List<*>): String {
        return Codec.encode(TupleType(types.map(Type::of)), parameters).toHexString()
    }

    override fun encodeFunctionCall(abiItem: AbiItem, parameters: List<*>): String {
        TODO("Not yet implemented")
    }

    override fun registerPrimitiveTypeConverter(typeName: String, converter: (Any) -> Any) {
        Codec.registerPrimitiveTypeConverter(typeName, converter)
    }

    private fun List<AbiComponent>.toTupleType() = TupleType(map { Type.of(it.encodeType()) })

    private data class LogTypes(
        val indexed: MutableList<Type> = mutableListOf(),
        val nonIndexed: MutableList<Type> = mutableListOf()
    ) {
        fun add(abi: AbiInput) = apply {
            when (abi.indexed == true) {
                true -> indexed
                false -> nonIndexed
            } += Type.of(abi.type)
        }
    }
}
