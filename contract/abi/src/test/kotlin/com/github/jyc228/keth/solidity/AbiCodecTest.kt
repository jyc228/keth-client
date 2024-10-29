package com.github.jyc228.keth.solidity

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.maps.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldBeEqualIgnoringCase
import io.kotest.matchers.types.shouldBeInstanceOf
import java.math.BigInteger

@OptIn(ExperimentalStdlibApi::class)
internal class AbiCodecTest : DescribeSpec({

    context("encode") {
        it("Transfer") {
            val abi =
                // https://etherscan.io/tx/0xdd377131912e9ac4ca74a60135dc3c9ac7a416d2b956315c34e34432a9bfae9f#eventlog
                AbiItem.fromJson("""{"inputs":[{"name":"recipient","type":"address","internalType":"address"},{"name":"amount","type":"uint256","internalType":"uint256"}],"name":"transfer","outputs":[{"name":"","type":"bool","internalType":"bool"}],"stateMutability":"nonpayable","type":"function"}""")

            val result = AbiCodec.encode(
                abi.inputs,
                listOf("0x2b8A6034c83706aacC846ddc4a2c8E943C09aE44", 60220000.toBigInteger())
            )

            result shouldBe "0000000000000000000000002b8a6034c83706aacc846ddc4a2c8e943c09ae44000000000000000000000000000000000000000000000000000000000396e260"
        }
    }

    context("decodeParameters") {
        it("setL1BlockValues") {
            val abi =
                AbiItem.fromJson("""{"inputs":[{"internalType":"uint64","name":"_number","type":"uint64"},{"internalType":"uint64","name":"_timestamp","type":"uint64"},{"internalType":"uint256","name":"_basefee","type":"uint256"},{"internalType":"bytes32","name":"_hash","type":"bytes32"},{"internalType":"uint64","name":"_sequenceNumber","type":"uint64"},{"internalType":"bytes32","name":"_batcherHash","type":"bytes32"},{"internalType":"uint256","name":"_l1FeeOverhead","type":"uint256"},{"internalType":"uint256","name":"_l1FeeScalar","type":"uint256"}],"name":"setL1BlockValues","outputs":[],"stateMutability":"nonpayable","type":"function"}""")

            val hex =
                "0x015d8eb9000000000000000000000000000000000000000000000000000000000006cf4900000000000000000000000000000000000000000000000000000000639833b0000000000000000000000000000000000000000000000000000000000000000751c54ac1869a3f078ff0aa2994f27f6aceb5d2bda18ec30f2593fa20d9c052fa000000000000000000000000000000000000000000000000000000000000000000000000000000000000000016cb0a409497493c2ef7688f69534fd8f8f23b74000000000000000000000000000000000000000000000000000000000000083400000000000000000000000000000000000000000000000000000000000f4240"

            val result = AbiCodec.decode(abi.inputs, hex.drop(10)).entries.toList().map { it.value }

            result[0].shouldBeInstanceOf<BigInteger>() shouldBe 446281.toBigInteger()
            result[1].shouldBeInstanceOf<BigInteger>() shouldBe 1670919088.toBigInteger()
            result[2].shouldBeInstanceOf<BigInteger>() shouldBe 7.toBigInteger()
            result[3].shouldBeInstanceOf<ByteArray>()
                .toHexString() shouldBeEqualIgnoringCase "51c54ac1869a3f078ff0aa2994f27f6aceb5d2bda18ec30f2593fa20d9c052fa"
            result[4].shouldBeInstanceOf<BigInteger>() shouldBe 0.toBigInteger()
            result[5].shouldBeInstanceOf<ByteArray>()
                .toHexString() shouldBeEqualIgnoringCase "00000000000000000000000016cb0a409497493c2ef7688f69534fd8f8f23b74"
            result[6].shouldBeInstanceOf<BigInteger>() shouldBe 2100.toBigInteger()
            result[7].shouldBeInstanceOf<BigInteger>() shouldBe 1000000.toBigInteger()
        }

        it("exactInputSingle") {
            // https://etherscan.io/tx/0xff88cc410593c399697faf3ac6c4debbba05ef81ecd1843d18c4ef51e0214bdd
            val abi =
                AbiItem.fromJson("""{"inputs":[{"components":[{"internalType":"address","name":"tokenIn","type":"address"},{"internalType":"address","name":"tokenOut","type":"address"},{"internalType":"uint24","name":"fee","type":"uint24"},{"internalType":"address","name":"recipient","type":"address"},{"internalType":"uint256","name":"deadline","type":"uint256"},{"internalType":"uint256","name":"amountIn","type":"uint256"},{"internalType":"uint256","name":"amountOutMinimum","type":"uint256"},{"internalType":"uint160","name":"sqrtPriceLimitX96","type":"uint160"}],"internalType":"struct ISwapRouter.ExactInputSingleParams","name":"params","type":"tuple"}],"name":"exactInputSingle","outputs":[{"internalType":"uint256","name":"amountOut","type":"uint256"}],"stateMutability":"payable","type":"function"}""")

            val hex =
                "0x414bf389000000000000000000000000626e8036deb333b408be468f951bdb42433cbf18000000000000000000000000c02aaa39b223fe8d0a0e5c4f27ead9083c756cc200000000000000000000000000000000000000000000000000000000000009c4000000000000000000000000b1b2d032aa2f52347fbcfd08e5c3cc55216e84040000000000000000000000000000000000000000000000000000000066eae2d90000000000000000000000000000000000000000000002ac2d322ac7ef76e4000000000000000000000000000000000000000000000000001c73c927940973000000000000000000000000000000000000000000000000000000000000000000"
            val result = AbiCodec.decode(abi.inputs, hex.drop(10))
            result shouldHaveSize 1
            val params = result["params"].shouldBeInstanceOf<Map<String, *>>()
            params["tokenIn"] shouldBe "0x626E8036dEB333b408Be468F951bdB42433cBF18".lowercase()
            params["tokenOut"] shouldBe "0xC02aaA39b223FE8D0A0e5C4F27eAD9083C756Cc2".lowercase()
            params["fee"] shouldBe "2500".toBigInteger()
            params["recipient"] shouldBe "0xb1b2d032AA2F52347fbcfd08E5C3Cc55216E8404".lowercase()
            params["deadline"] shouldBe "1726669529".toBigInteger()
            params["amountIn"] shouldBe "12620829658936080000000".toBigInteger()
            params["amountOutMinimum"] shouldBe "2050203427208262400".toBigInteger()
            params["sqrtPriceLimitX96"] shouldBe "0".toBigInteger()
        }
    }
})