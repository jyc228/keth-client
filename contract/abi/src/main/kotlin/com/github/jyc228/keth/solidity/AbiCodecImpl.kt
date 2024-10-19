package com.github.jyc228.keth.solidity

class AbiCodecImpl : AbiCodec {
    private val primitiveValueConverter = mutableMapOf<String, (Any) -> Any>()
    override fun decode(component: AbiComponent, hex: String): Any {
        return Codec.decode(Type.of(component.encodeType()), Codec.DecodingContext(hex, primitiveValueConverter))
    }

    override fun decode(components: List<AbiComponent>, hex: String): Map<String, Any> {
        val result = TupleCodec.decode(components.toTupleType(), Codec.DecodingContext(hex, primitiveValueConverter))
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

    override fun registerPrimitiveTypeConverter(typeName: String, converter: (Any) -> Any) {
        primitiveValueConverter[typeName] = converter
    }

    private fun List<AbiComponent>.toTupleType() = TupleType(map { Type.of(it.encodeType()) })
}
