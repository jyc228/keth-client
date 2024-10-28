package com.github.jyc228.keth.solidity

/**
 *
 * This interface defines actions for encoding and decoding ABI components in Solidity to Kotlin and vice versa.
 *
 * Below is a mapping of Kotlin types to their equivalent Solidity types:
 *
 * | **Solidity Type**     | **Kotlin Type**   |
 * |-----------------------|--------------------|
 * | `int*` / `uint*`      | `BigInteger`       |
 * | `bool`                | `Boolean`          |
 * | `string`              | `String`           |
 * | `address`             | `String`           |
 * | `bytes*`              | `ByteArray`        |
 *
 * [abi-spec](https://docs.soliditylang.org/en/latest/abi-spec.html)
 */
interface AbiCodec {
    /**
     * @param hex must not start with 0x and sig
     */
    fun decode(component: AbiComponent, hex: String): Any

    /**
     * Decodes the [components] wrapped as a tuple type. The returned [Map] keys are [AbiComponent.name]
     *
     * @param hex must not start with 0x and sig
     */
    fun decode(components: List<AbiComponent>, hex: String): Map<String, Any>

    /**
     * @return hex string without 0x prefix and sig
     */
    fun encode(component: AbiComponent, value: Any): String

    /**
     * Encodes the [components] wrapped as a tuple type.
     *
     * @return hex string without 0x prefix and sig
     */
    fun encode(components: List<AbiComponent>, values: List<*>): String

    /**
     * Overrides the type conversion defined in [AbiCodec]. Currently, it is only applied when [decode] is called
     */
    fun registerPrimitiveTypeConverter(typeName: String, converter: (Any) -> Any)

    companion object : AbiCodec by AbiCodecImpl()
}
