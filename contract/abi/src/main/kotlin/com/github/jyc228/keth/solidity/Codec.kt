package com.github.jyc228.keth.solidity

import java.math.BigInteger
import java.nio.ByteBuffer
import kotlin.math.ceil

sealed interface Codec {
    fun computeEncodeSize(type: Type, data: Any?): Int
    fun encode(type: Type, data: Any?, buffer: ByteBuffer)
    fun decode(type: Type, context: DecodingContext): Any

    @OptIn(ExperimentalStdlibApi::class)
    class DecodingContext(internal val buffer: ByteBuffer) {
        constructor(hex: String) : this(ByteBuffer.wrap(hex.removePrefix("0x").hexToByteArray()))

        fun read(size: Int) = ByteArray(size).also { buffer.get(it) }
        fun readHexString(size: Int) = ByteArray(size).also { buffer.get(it) }.toHexString()
        fun read() = buffer.get()
        fun skip(count: Int) = apply { buffer.position(buffer.position() + count) }
        fun position(newPosition: Int) = apply { buffer.position(newPosition) }
        fun position() = buffer.position()
    }

    companion object : Codec {
        private val primitiveTypeCodec = mutableMapOf<String, Codec>().apply {
            this["string"] = StringCodec
            this["bool"] = BooleanCodec
            this["address"] = AddressCodec
            this["bytes"] = BytesCodec
            this["int"] = NumberCodec
            this["uint"] = NumberCodec
        }

        fun registerPrimitiveTypeConverter(typeName: String, converter: (Any) -> Any) {
            val codec = primitiveTypeCodec[typeName] ?: error { "unsupported type $typeName" }
            if (codec is CustomCodec) return
            primitiveTypeCodec[typeName] = CustomCodec(codec, converter)
        }

        fun encode(type: Type, data: Any?): ByteArray {
            return ByteBuffer.allocate(computeEncodeSize(type, data)).also { encode(type, data, it) }.array()
        }

        fun decode(type: Type, hex: String): Any {
            return decode(type, DecodingContext(hex))
        }

        override fun computeEncodeSize(type: Type, data: Any?): Int = selectCodec(type).computeEncodeSize(type, data)
        override fun encode(type: Type, data: Any?, buffer: ByteBuffer) = selectCodec(type).encode(type, data, buffer)
        override fun decode(type: Type, context: DecodingContext): Any = selectCodec(type).decode(type, context)

        private fun selectCodec(type: Type): Codec = when (type) {
            is ArrayType -> ArrayCodec
            is TupleType -> TupleCodec
            is PrimitiveType -> primitiveTypeCodec[type.name] ?: error { "unsupported type $type" }
        }
    }
}

class CustomCodec(private val codec: Codec, private val converter: (Any) -> Any) : Codec by codec {
    override fun decode(type: Type, context: Codec.DecodingContext): Any = converter(codec.decode(type, context))
}

data object BooleanCodec : Codec {
    override fun computeEncodeSize(type: Type, data: Any?): Int = 32

    override fun encode(type: Type, data: Any?, buffer: ByteBuffer) = encode(data, buffer)

    private fun encode(data: Any?, buffer: ByteBuffer) {
        repeat(31) { require(buffer.get() == 0.toByte()) }
        buffer.put(if (toBoolean(data)) 1 else 0)
    }

    private fun toBoolean(value: Any?): Boolean = when (value) {
        is Boolean -> value

        is Number -> when (value.toInt()) {
            0 -> false
            1 -> true
            else -> error("invalid value $value")
        }

        is String -> when (value) {
            "0" -> false
            "1" -> true
            "false" -> false
            "true" -> true
            "0x0", "0X0" -> false
            "0x1", "0X1" -> true
            else -> error("invalid value $value")
        }

        else -> error("unsupported type $value")
    }

    override fun decode(type: Type, context: Codec.DecodingContext): Boolean {
        return context.skip(31).read() == 1.toByte()
    }
}

data object NumberCodec : Codec {
    private data class Limit(val min: BigInteger, val max: BigInteger)

    private val limitByType = buildMap {
        var base = 256.toBigInteger()
        (8..256 step 8).forEach { size ->
            this["int${size}"] = Limit(-base / BigInteger.TWO, base / BigInteger.TWO - BigInteger.ONE)
            this["uint${size}"] = Limit(BigInteger.ZERO, base - BigInteger.ONE)
            base *= 256.toBigInteger()
        }
        this["int"] = this["int256"]!!
        this["uint"] = this["uint56"]!!
    }

    private val mask = BigInteger.TWO.pow(256)

    override fun computeEncodeSize(type: Type, data: Any?): Int = 32

    override fun encode(type: Type, data: Any?, buffer: ByteBuffer) = encode(data, buffer)

    fun encode(data: Any?, buffer: ByteBuffer) {
        val value = when (data is String) {
            true -> when {
                data.startsWith("0x") -> data.removeRange(0, 2).toBigInteger(16)
                data.startsWith("-0x") -> data.removeRange(1, 3).toBigInteger(16)
                else -> data.toBigInteger()
            }

            false -> data.toString().toBigInteger()
        }
        val unsignedValue = if (value < BigInteger.ZERO) mask + value else value
        val hex = unsignedValue.toString(16).padStart(64, '0')
        buffer.putHexString(hex)
    }

    override fun decode(type: Type, context: Codec.DecodingContext): BigInteger {
        val result = context.readHexString(32).toBigInteger(16)
        if (type.dynamic) return result
        if (result <= limitByType[type.toString()]!!.max) return result
        return result - mask
    }
}

data object StringCodec : Codec {
    @OptIn(ExperimentalStdlibApi::class)
    override fun computeEncodeSize(type: Type, data: Any?): Int =
        BytesCodec.computeEncodeSize(type, data.toString().toByteArray(Charsets.UTF_8).toHexString())

    @OptIn(ExperimentalStdlibApi::class)
    override fun encode(type: Type, data: Any?, buffer: ByteBuffer) {
        BytesCodec.encode(type, data.toString().toByteArray(Charsets.UTF_8).toHexString(), buffer)
    }

    override fun decode(type: Type, context: Codec.DecodingContext): String {
        return BytesCodec.decode(type, context).toString(Charsets.UTF_8)
    }
}

data object BytesCodec : Codec {
    private val emptyByteArray = byteArrayOf()
    override fun computeEncodeSize(type: Type, data: Any?): Int {
        if (type.size != null) return 32
        var size = (data as String).length
        if (size % 2 == 1) size += 1
        if (data.startsWith("0x")) size -= 2
        return 32 + (ceil((size / 2).toFloat() / 32).toInt() * 32)
    }

    override fun encode(type: Type, data: Any?, buffer: ByteBuffer) {
        require(data is String) { "unsupported type $data" }
        if (type.size != null) {
            return buffer.putHexString(toValidHexString(data))
        }
        val hex = toValidHexString(data).removePrefix("0x")
        NumberCodec.encode(hex.length / 2, buffer)
        buffer.putHexString(hex)
    }

    override fun decode(type: Type, context: Codec.DecodingContext): ByteArray {
        if (type.dynamic) {
            val size = NumberCodec.decode(Type.of("uint32"), context)
            if (size == BigInteger.ZERO) return emptyByteArray
            return context.read(size.toInt()).also { context.skip(32 - (size.toInt() % 32)) }
        }
        return context.read(requireNotNull(type.size)).also { context.skip(32 - it.size) }
    }

    private fun toValidHexString(hex: String): String {
        if (hex.length % 2 != 0) {
            return hex + "0"
        }
        return hex
    }
}

data object AddressCodec : Codec {
    override fun computeEncodeSize(type: Type, data: Any?): Int = 32

    override fun encode(type: Type, data: Any?, buffer: ByteBuffer) = encode(data, buffer)

    private fun encode(data: Any?, buffer: ByteBuffer) {
        require(data is String)
        val address = when (data.startsWith("0x", ignoreCase = true)) {
            true -> data
            false -> "0x${data}"
        }
        buffer.position(12).putHexString(address)
    }

    override fun decode(type: Type, context: Codec.DecodingContext): String = decode(context)

    fun decode(context: Codec.DecodingContext): String {
        return "0x${context.skip(12).readHexString(20).lowercase()}"
    }
}

data object ArrayCodec : Codec {
    override fun computeEncodeSize(type: Type, data: Any?): Int {
        require(data is Collection<*> && type is ArrayType)
        var offset = 0
        if (type.dynamic) offset += 32
        if (type.elementType.dynamic) offset += data.size * 32
        return offset + data.sumOf { Codec.computeEncodeSize(type.elementType, it) }
    }

    override fun encode(type: Type, data: Any?, buffer: ByteBuffer) {
        require(data is Collection<*> && type is ArrayType)
        if (type.size != null) {
            require(type.size == data.size) { "Given arguments count doesn't match array length" }
        }
        if (type.dynamic) {
            NumberCodec.encode(data.size, buffer)
        }
        if (type.elementType.dynamic) {
            val staticSize = data.size * 32
            var dynamicSize = 0
            data.forEach {
                NumberCodec.encode(staticSize + dynamicSize, buffer)
                dynamicSize += Codec.computeEncodeSize(type.elementType, it)
            }
        }
        data.forEach { Codec.encode(type.elementType, it, buffer) }
    }

    override fun decode(type: Type, context: Codec.DecodingContext): List<*> {
        require(type is ArrayType)
        val size = type.size ?: NumberCodec.decode(Type.of("uint32"), context).toInt()
        if (type.elementType.dynamic) {
            val offset = context.position()
            return (0..<size).map {
                context.position(offset + it * 32)
                val offsetSize = NumberCodec.decode(Type.of("uint32"), context)
                context.position(offset + offsetSize.toInt())
                Codec.decode(type.elementType, context)
            }
        }
        return (0..<size).map { Codec.decode(type.elementType, context) }
    }
}

data object TupleCodec : Codec {
    override fun computeEncodeSize(type: Type, data: Any?): Int {
        return asSequence(type, data).sumOf { (type, data) ->
            when (type.dynamic) {
                true -> 32
                false -> 0
            } + Codec.computeEncodeSize(type, data)
        }
    }

    override fun encode(type: Type, data: Any?, buffer: ByteBuffer) {
        val staticSize = asSequence(type, data).sumOf { (type, data) ->
            when (type.dynamic) {
                true -> 32
                false -> Codec.computeEncodeSize(type, data)
            }
        }
        var dynamicSize = 0
        asSequence(type, data).forEach { (type, data) ->
            if (type.dynamic) {
                NumberCodec.encode(staticSize + dynamicSize, buffer)
                val start = buffer.position()
                Codec.encode(type, data, buffer)
                dynamicSize += buffer.position() - start
            } else {
                Codec.encode(type, data, buffer)
            }
        }
    }

    private fun asSequence(type: Type, data: Any?): Sequence<Pair<Type, Any?>> {
        require(type is TupleType && data is Collection<*>)
        val dataIterator = data.iterator()
        return type.components.asSequence().map { it to dataIterator.next() }
    }

    override fun decode(type: Type, context: Codec.DecodingContext): List<Any> {
        require(type is TupleType)
        val offset = context.position()
        return type.components.mapIndexed { i, c ->
            if (c.dynamic) {
                val offsetSize = NumberCodec.decode(Type.of("uint32"), context)
                context.position(offset + offsetSize.toInt())
                val result = Codec.decode(c, context)
                if (i < type.components.lastIndex) {
                    context.position(offset + (i + 1) * 32)
                }
                result
            } else {
                Codec.decode(c, context)
            }
        }
    }
}

private fun ByteBuffer.putHexString(hex: String) {
    val offset = if (hex.startsWith("0x", true)) 2 else 0
    repeat((hex.length - offset) / 2) { idx ->
        val hexIndex = (idx * 2) + offset
        put((charCodeToBase16(hex[hexIndex]) * 16 + charCodeToBase16(hex[hexIndex + 1])).toByte())
    }
    if (position() % 32 != 0) {
        position(position() + 32 - (position() % 32))
    }
}

private fun charCodeToBase16(char: Char): Int {
    if (char in '0'..'9')
        return char - '0'
    if (char in 'A'..'F')
        return char - ('A' - 10)
    if (char in 'a'..'f')
        return char - ('a' - 10)
    error("...")
}
