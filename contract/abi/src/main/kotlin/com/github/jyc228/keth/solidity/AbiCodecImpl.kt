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

    @OptIn(ExperimentalStdlibApi::class)
    private fun hexToByteBuffer(hex: String) = ByteBuffer.wrap(hex.removePrefix("0x").hexToByteArray())

    override fun registerPrimitiveTypeConverter(typeName: String, converter: (Any) -> Any) {
        Codec.registerPrimitiveTypeConverter(typeName, converter)
    }

    private fun List<AbiComponent>.toTupleType() = TupleType(map { Type.of(it.encodeType()) })
}
