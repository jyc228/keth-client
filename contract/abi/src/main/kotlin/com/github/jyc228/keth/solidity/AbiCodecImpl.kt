package com.github.jyc228.keth.solidity

class AbiCodecImpl : AbiCodec {
    private val primitiveValueConverter = mutableMapOf<String, (Any) -> Any>()
    override fun decode(component: AbiComponent, hex: String): Any {
        return Codec.decode(component.toType(), Codec.DecodingContext(hex, primitiveValueConverter))
    }

    override fun decode(components: List<AbiComponent>, hex: String): Map<String, Any> {
        val result = TupleCodec.decode(components.toTupleType(), Codec.DecodingContext(hex, primitiveValueConverter))
        return convertToMap(components, result)
    }

    override fun decode(type: String, hex: String): Any {
        return Codec.decode(Type.of(type), Codec.DecodingContext(hex, primitiveValueConverter))
    }

    private fun convertToMap(components: List<AbiComponent>, result: List<*>): Map<String, Any> {
        return components.withIndex().associateBy({ (_, c) -> c.name }, { (i, c) ->
            when (c.type) {
                "tuple" -> convertToMap(c.components, result[i] as List<*>)
                else -> result[i] as Any
            }
        })
    }

    override fun encode(
        component: AbiComponent,
        value: Any
    ) = Codec.encode(component.toType(), value)

    override fun encode(
        components: List<AbiComponent>,
        values: List<*>
    ) = Codec.encode(components.toTupleType(), values)

    override fun encode(type: String, value: Any): String = Codec.encode(Type.of(type), value)

    override fun registerPrimitiveTypeConverter(typeName: String, converter: (Any) -> Any) {
        primitiveValueConverter[typeName] = converter
    }

    private fun List<AbiComponent>.toTupleType() = TupleType(map { it.toType() })
    private fun AbiComponent.toType() = Type.of(encodeType())
}
