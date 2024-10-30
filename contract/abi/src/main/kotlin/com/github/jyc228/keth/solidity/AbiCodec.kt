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
     * @return A [List] if the [component] is an array, a [Map] if the [component] is a tuple, otherwise refer to [AbiCodec] for Kotlin types.
     */
    fun decode(component: AbiComponent, hex: String): Any

    /**
     * Decodes the [components] wrapped as a tuple type. The returned [Map] keys are [AbiComponent.name]
     *
     * @param hex must not start with 0x and sig
     */
    fun decode(components: List<AbiComponent>, hex: String): Map<String, Any>

    /**
     * Decodes a low-level function using only the [type]. Acceptable formats are as follows:
     * - single: Refer to [AbiCodec] Solidity types
     * - array: type[] (e.g., int[], uint256[])
     * - tuple: (type1, type2, ...) (e.g., (address,uint256[]))
     *
     * @param type [formal spec](https://docs.soliditylang.org/en/latest/abi-spec.html#formal-specification-of-the-encoding)
     * @param hex must not start with 0x and sig
     * @return A [List] if the [type] is a tuple or an array, otherwise refer to [AbiCodec] for Kotlin types.
     */
    fun decode(type: String, hex: String): Any

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
