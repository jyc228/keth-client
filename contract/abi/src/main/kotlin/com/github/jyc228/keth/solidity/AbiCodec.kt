package com.github.jyc228.keth.solidity

interface AbiCodec {
    fun decode(component: AbiComponent, hex: String): Any
    fun decode(components: List<AbiComponent>, hex: String): Map<String, Any>

    /**
     * @return hex string without 0x prefix and sig
     */
    fun encode(component: AbiComponent, value: Any): String

    /**
     * @return hex string without 0x prefix and sig
     */
    fun encode(components: List<AbiComponent>, values: List<*>): String

    fun registerPrimitiveTypeConverter(typeName: String, converter: (Any) -> Any)

    companion object : AbiCodec by AbiCodecImpl()
}
