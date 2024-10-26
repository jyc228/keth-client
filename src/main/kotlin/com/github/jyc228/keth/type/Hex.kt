package com.github.jyc228.keth.type

import java.math.BigInteger
import kotlinx.serialization.Serializable

abstract class HexString {
    abstract val hex: String
    val with0x get() = "0x$hex"

    @OptIn(ExperimentalStdlibApi::class)
    val bytes get() = hex.hexToByteArray()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is HexString) return false
        return hex == other.hex
    }

    override fun hashCode(): Int = hex.hashCode()
    override fun toString(): String = with0x

    @OptIn(ExperimentalStdlibApi::class)
    abstract class Factory<T : HexString>(protected val new: (String) -> T) {
        operator fun invoke(hex: String): T = new(hex.removePrefix("0x").lowercase())
        operator fun invoke(bytes: ByteArray): T = new(bytes.toHexString())
        fun unsafe(value: String) = new(value)
    }
}

@Serializable(HashSerializer::class)
class Hash private constructor(override val hex: String) : HexString() {
    companion object : Factory<Hash>(::Hash)
}

@Serializable(AddressSerializer::class)
class Address private constructor(override val hex: String) : HexString() {
    companion object : Factory<Address>(::Address)
}

sealed class HexNumber<T, SELF : HexNumber<T, SELF>>(
    private val lazyHex: Lazy<String>,
    private val lazyNumber: Lazy<T>
) : HexString() {
    constructor(
        toHex: (Int) -> String,
        number: T
    ) : this(lazy(LazyThreadSafetyMode.NONE) { toHex(16) }, lazyOf(number))

    constructor(
        hex: String,
        toNumber: () -> T
    ) : this(lazyOf(hex.removePrefix("0x")), lazy(LazyThreadSafetyMode.NONE, toNumber))

    override val hex: String get() = lazyHex.value
    val number: T get() = lazyNumber.value

    abstract operator fun compareTo(other: SELF): Int
    abstract operator fun plus(other: SELF): SELF
    abstract operator fun minus(other: SELF): SELF
    override fun toString(): String {
        if (lazyNumber.isInitialized()) return number.toString()
        return with0x
    }
}

@Serializable(HexIntSerializer::class)
class HexInt : HexNumber<Int, HexInt> {
    constructor(number: Int) : super(number::toString, number)
    constructor(hex: String) : super(hex, hex::hexToInt)

    override operator fun compareTo(other: HexInt): Int = number.compareTo(other.number)
    override operator fun plus(other: HexInt): HexInt = HexInt(number + other.number)
    override operator fun minus(other: HexInt): HexInt = HexInt(number - other.number)

    companion object {
        val ZERO = HexInt("0x0")
    }
}

@Serializable(HexULongSerializer::class)
class HexULong : HexNumber<ULong, HexULong> {
    constructor(number: ULong) : super(number::toString, number)
    constructor(hex: String) : super(hex, hex::hexToULong)

    override operator fun compareTo(other: HexULong): Int = number.compareTo(other.number)
    override operator fun plus(other: HexULong): HexULong = HexULong(number + other.number)
    override operator fun minus(other: HexULong): HexULong = HexULong(number - other.number)

    companion object {
        val ZERO = HexULong("0x0")
    }
}

@Serializable(HexBigIntSerializer::class)
class HexBigInt : HexNumber<BigInteger, HexBigInt> {
    constructor(number: BigInteger) : super(number::toString, number)
    constructor(hex: String) : super(hex, hex::hexToBigInt)

    override operator fun compareTo(other: HexBigInt): Int = number.compareTo(other.number)
    override operator fun plus(other: HexBigInt): HexBigInt = HexBigInt(number + other.number)
    override operator fun minus(other: HexBigInt): HexBigInt = HexBigInt(number - other.number)

    companion object {
        val ZERO = HexBigInt("0x0")
    }
}

@Serializable(HexDataSerializer::class)
data class HexData(override val hex: String) : HexString() {
    fun toText(): String = buildString {
        val value = hex.removePrefix("0x")
        for (i in value.indices step 2) {
            val char = value.substring(i, i + 2).toInt(16).toChar()
            if (!char.isISOControl()) append(char)
        }
    }.trim()

    companion object : Factory<HexData>(::HexData)
}

fun String.hexToInt() = removePrefix("0x").toInt(16)
fun String.hexToULong() = removePrefix("0x").toULong(16)
fun String.hexToBigInt() = removePrefix("0x").toBigInteger(16)