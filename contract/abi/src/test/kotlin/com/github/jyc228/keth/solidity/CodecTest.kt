package com.github.jyc228.keth.solidity

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.spec.style.scopes.ContainerScope
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import java.math.BigInteger

@OptIn(ExperimentalStdlibApi::class)
class CodecTest : DescribeSpec({
    context("address") {
        context("encode") {
            encodeTest {
                "00000000219ab540356cbb839cbe05303d7705fa" encode "address".type shouldBe "00000000000000000000000000000000219ab540356cbb839cbe05303d7705fa"
                "0xc02aaa39b223fe8d0a0e5c4f27ead9083c756cC2" encode "address".type shouldBe "000000000000000000000000c02aaa39b223fe8d0a0e5c4f27ead9083c756cc2"
            }
        }

        context("decode") {
            decodeTest({ it.shouldBeInstanceOf<String>() }) {
                "000000000000000000000000e6004226bc1f1ba37e5c2c4689693b94b863cd58" decode "address".type shouldBe "0xe6004226bc1f1ba37e5c2c4689693b94b863cd58"
                "000000000000000000000000e6004226bc1f1ba37e5c2c4689693b94b863cd580000000000000000000000000000000000000000000000000000000000000001" decode "address".type shouldBe "0xe6004226bc1f1ba37e5c2c4689693b94b863cd58" withRemaining "0000000000000000000000000000000000000000000000000000000000000001"
            }
        }
    }

    context("bool") {
        context("encode") {
            encodeTest {
                1 encode "bool".type shouldBe "0000000000000000000000000000000000000000000000000000000000000001"
                0 encode "bool".type shouldBe "0000000000000000000000000000000000000000000000000000000000000000"
                1.toBigInteger() encode "bool".type shouldBe "0000000000000000000000000000000000000000000000000000000000000001"
                0.toBigInteger() encode "bool".type shouldBe "0000000000000000000000000000000000000000000000000000000000000000"
                "0x1" encode "bool".type shouldBe "0000000000000000000000000000000000000000000000000000000000000001"
                "0x0" encode "bool".type shouldBe "0000000000000000000000000000000000000000000000000000000000000000"
                true encode "bool".type shouldBe "0000000000000000000000000000000000000000000000000000000000000001"
                false encode "bool".type shouldBe "0000000000000000000000000000000000000000000000000000000000000000"
                "true" encode "bool".type shouldBe "0000000000000000000000000000000000000000000000000000000000000001"
                "false" encode "bool".type shouldBe "0000000000000000000000000000000000000000000000000000000000000000"
            }
        }

        context("decode") {
            decodeTest({ it.shouldBeInstanceOf<Boolean>() }) {
                "0000000000000000000000000000000000000000000000000000000000000001" decode "bool".type shouldBe true
                "0000000000000000000000000000000000000000000000000000000000000000" decode "bool".type shouldBe false
                "0000000000000000000000000000000000000000000000000000000000000001000000000000000000000000000000000000000000000000000000000000000c" decode "bool".type shouldBe true withRemaining "000000000000000000000000000000000000000000000000000000000000000c"
            }
        }
    }

    context("array") {
        context("encode") {
            encodeTest {
                listOf(1, 3)
                    .let { it encode "uint[]".type shouldBe "000000000000000000000000000000000000000000000000000000000000000200000000000000000000000000000000000000000000000000000000000000010000000000000000000000000000000000000000000000000000000000000003" }
                listOf(1, 3)
                    .let { it encode "uint[2]".type shouldBe "00000000000000000000000000000000000000000000000000000000000000010000000000000000000000000000000000000000000000000000000000000003" }
                listOf("hello", "web3")
                    .let { it encode "string[]".type shouldBe "000000000000000000000000000000000000000000000000000000000000000200000000000000000000000000000000000000000000000000000000000000400000000000000000000000000000000000000000000000000000000000000080000000000000000000000000000000000000000000000000000000000000000568656c6c6f00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000047765623300000000000000000000000000000000000000000000000000000000" }
                listOf("hello", "web3")
                    .let { it encode "string[2]".type shouldBe "00000000000000000000000000000000000000000000000000000000000000400000000000000000000000000000000000000000000000000000000000000080000000000000000000000000000000000000000000000000000000000000000568656c6c6f00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000047765623300000000000000000000000000000000000000000000000000000000" }
            }
        }
        context("decode") {
            decodeTest({ it.shouldBeInstanceOf<List<*>>() }) {
                "000000000000000000000000000000000000000000000000000000000000000200000000000000000000000000000000000000000000000000000000000000010000000000000000000000000000000000000000000000000000000000000003"
                    .let { it decode "uint[]".type shouldBe listOf("1".toBigInteger(), "3".toBigInteger()) }
                "00000000000000000000000000000000000000000000000000000000000000010000000000000000000000000000000000000000000000000000000000000003"
                    .let { it decode "uint[2]".type shouldBe listOf("1".toBigInteger(), "3".toBigInteger()) }
                "00000000000000000000000000000000000000000000000000000000000000400000000000000000000000000000000000000000000000000000000000000080000000000000000000000000000000000000000000000000000000000000000568656c6c6f00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000047765623300000000000000000000000000000000000000000000000000000000"
                    .let { it decode "string[2]".type shouldBe listOf("hello", "web3") }
                "000000000000000000000000000000000000000000000000000000000000000200000000000000000000000000000000000000000000000000000000000000400000000000000000000000000000000000000000000000000000000000000080000000000000000000000000000000000000000000000000000000000000000568656c6c6f00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000047765623300000000000000000000000000000000000000000000000000000000"
                    .let { it decode "string[]".type shouldBe listOf("hello", "web3") }
            }
        }
    }

    context("number") {
        context("encode") {
            encodeTest {
                (-122) encode "int".type shouldBe "ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff86"
                122.toBigInteger() encode "int".type shouldBe "000000000000000000000000000000000000000000000000000000000000007a"
                (-1) encode "int8".type shouldBe "ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff"
                (-122) encode "int8".type shouldBe "ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff86"
                (-128) encode "int8".type shouldBe "ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff80"
                "-122" encode "int8".type shouldBe "ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff86"
                (-123123) encode "int24".type shouldBe "fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffe1f0d"
                (-122).toBigInteger() encode "int32".type shouldBe "ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff86"
                "-0xa2" encode "int32".type shouldBe "ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff5e"
            }
        }

        context("decode") {
            decodeTest({ it.shouldBeInstanceOf<BigInteger>().toString() }) {
                "0000000000000000000000000000000000000000000000000000000000000012" decode "uint8".type shouldBe "18"
                "00000000000000000000003f29a33f562a1feab357509b77f71717e78667e7c1" decode "uint256".type shouldBe "92312312312312312312312312312312312303939393939393"
                "ffffffffffffffffffffffc0d65cc0a9d5e0154ca8af648808e8e8187998183f" decode "int256".type shouldBe "-92312312312312312312312312312312312303939393939393"
                "ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffe7" decode "int8".type shouldBe "-25"
                "000000000000000000000000000000000000000000000000000000000001e0f30000000000000000000000000000000000000000000000000000000000000001" decode "uint256".type shouldBe "123123" withRemaining "0000000000000000000000000000000000000000000000000000000000000001"
                "0000000000000000000000000000000000000000000000000000000000bbdef8" decode "uint24".type shouldBe "12312312"
                "fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffe1f0d" decode "int24".type shouldBe "-123123"
            }
        }
    }

    context("string") {
        context("encode") {
            encodeTest {
                "marin" encode "string".type shouldBe "00000000000000000000000000000000000000000000000000000000000000056d6172696e000000000000000000000000000000000000000000000000000000"
                "extraaaaaaaaaalooooooooooooooooooonnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnngggggggggggggggggggggggstrrrrrrrrrrrrrrrrrrrrrrrriiiiiiiiiiiinnnnnnnnng" encode "string".type shouldBe "000000000000000000000000000000000000000000000000000000000000008a65787472616161616161616161616c6f6f6f6f6f6f6f6f6f6f6f6f6f6f6f6f6f6f6f6e6e6e6e6e6e6e6e6e6e6e6e6e6e6e6e6e6e6e6e6e6e6e6e6e6e6e6e6e6e6e6e6e676767676767676767676767676767676767676767676773747272727272727272727272727272727272727272727272726969696969696969696969696e6e6e6e6e6e6e6e6e6700000000000000000000000000000000000000000000"
                "šč|€-!" encode "string".type shouldBe "000000000000000000000000000000000000000000000000000000000000000ac5a1c48d7ce282ac2d2100000000000000000000000000000000000000000000"
            }
        }
    }

    context("bytes") {
        context("encode") {
            encodeTest {
                "0x0000001010" encode "bytes5".type shouldBe "0000001010000000000000000000000000000000000000000000000000000000"
                "0x0000001010" encode "bytes".type shouldBe "00000000000000000000000000000000000000000000000000000000000000050000001010000000000000000000000000000000000000000000000000000000"
                "0x3a1bd524db9d52a12c4c60bb3f08e4ed34f380964a6882d46097f6fe4eff98af80552fddf116d4afb1a2676508d68eb62f13e23e1e696c2a800d384470c628c748cee4ad2260d26584cd6a06c4a0cccca37b" encode "bytes".type shouldBe "00000000000000000000000000000000000000000000000000000000000000523a1bd524db9d52a12c4c60bb3f08e4ed34f380964a6882d46097f6fe4eff98af80552fddf116d4afb1a2676508d68eb62f13e23e1e696c2a800d384470c628c748cee4ad2260d26584cd6a06c4a0cccca37b0000000000000000000000000000"
            }
        }

        context("decode") {
            decodeTest({ it.shouldBeInstanceOf<ByteArray>().toHexString() }) {
                "01020000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000002" decode "bytes2".type shouldBe "0102" withRemaining "0000000000000000000000000000000000000000000000000000000000000002"
                "0000001010000000000000000000000000000000000000000000000000000000" decode "bytes5".type shouldBe "0000001010"
                "00000000000000000000000000000000000000000000000000000000000000050000001010000000000000000000000000000000000000000000000000000000" decode "bytes".type shouldBe "0000001010"
            }
        }
    }

    context("tuple") {
        context("encode") {
            encodeTest {
                listOf(69, true)
                    .let { it encode "(uint8,bool)".type shouldBe "00000000000000000000000000000000000000000000000000000000000000450000000000000000000000000000000000000000000000000000000000000001" }
                listOf(true, listOf(69, true))
                    .let { it encode "(bool,(uint8,bool))".type shouldBe "000000000000000000000000000000000000000000000000000000000000000100000000000000000000000000000000000000000000000000000000000000450000000000000000000000000000000000000000000000000000000000000001" }
                listOf(false, true)
                    .let { it encode "(bool,bool)".type shouldBe "00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000001" }
            }
        }

        context("decode") {
            decodeTest({ it.shouldBeInstanceOf<List<*>>() }) {
                // @formatter:off
                "00000000000000000000000000000000000000000000000000000000000000450000000000000000000000000000000000000000000000000000000000000001"
                    .let { it decode "(uint8,bool)".type shouldBe listOf(69.toBigInteger(), true) }
                "000000000000000000000000000000000000000000000000000000000000000100000000000000000000000000000000000000000000000000000000000000450000000000000000000000000000000000000000000000000000000000000001"
                    .let { it decode "(bool,(uint8,bool))".type shouldBe listOf(true, listOf(69.toBigInteger(), true)) }
                "0000000000000000000000000000000000000000000000000000000000000060000000000000000000000000000000000000000000000000000000000000000100000000000000000000000000000000000000000000000000000000000000a000000000000000000000000000000000000000000000000000000000000000116d6172696e31323331323331323331323300000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000010000000000000000000000000000000000000000000000000000000000000040000000000000000000000000000000000000000000000000000000000000003c776562336a7374657374696e676c6f6e6773747269696969696969696969696969696969696969696969696969696969696969696969696969696e6700000000"
                    .let { it decode "(string,bool,(bool,string))".type shouldBe listOf("marin123123123123", true, listOf(true, "web3jstestinglongstriiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiing")) }
                // @formatter:on
            }
        }

        it("encode, decode") {
            val input = listOf(
                "0x96f14f89b3f3368f23fcd78876e2bb9290ad246b",
                listOf(1.toBigInteger(), 2.toBigInteger()),
                listOf(true, 3.toBigInteger())
            )

            val hex = AbiCodec.encode("(address,uint256[],(bool,uint8))", input)
            val output = AbiCodec.decode("(address,uint256[],(bool,uint8))", hex)
            input shouldBe output
        }
    }
})

private val String.type: Type get() = Type.of(this)

private suspend fun ContainerScope.encodeTest(buildTest: EncodeTestBuilder.() -> Unit) {
    withData(
        nameFn = { tc -> "${tc.input::class.simpleName}(${tc.input}) encode to ${tc.type}" },
        EncodeTestBuilder().apply(buildTest).testCases
    ) { tc -> Codec.encode(tc.type, tc.input) shouldBe tc.expected }
}

private class EncodeTestBuilder {
    data class TC(val type: Type, val input: Any, var expected: String = "")

    val testCases = mutableListOf<TC>()
    infix fun Any.encode(type: Type): TC = TC(type, this)
    infix fun TC.shouldBe(expected: String) = apply { testCases += this.also { it.expected = expected } }
}

private suspend fun <R> ContainerScope.decodeTest(
    convertResult: (Any?) -> R,
    buildTest: DecodeTestBuilder<R>.() -> Unit
) {
    withData(
        nameFn = { tc -> "${tc.type}(${tc.inputHex})" },
        DecodeTestBuilder<R>().apply(buildTest).testCases
    ) { tc ->
        val context = Codec.DecodingContext(tc.inputHex, emptyMap())
        convertResult(Codec.decode(tc.type, context)) shouldBe tc.expected
        context.readHexString(context.buffer.remaining()) shouldBe tc.remaining
    }
}

private class DecodeTestBuilder<R> {
    data class TC<R>(val type: Type, val inputHex: String, var expected: R? = null, var remaining: String = "")

    val testCases = mutableListOf<TC<R>>()
    infix fun String.decode(type: Type): TC<R> = TC(type, this)
    infix fun TC<R>.shouldBe(expected: R?): TC<R> = apply { testCases += this.also { it.expected = expected } }
    infix fun TC<R>.withRemaining(remaining: String) = apply { this.remaining = remaining }
}
