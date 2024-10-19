package com.github.jyc228.keth.solidity

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.maps.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldBeEqualIgnoringCase
import io.kotest.matchers.types.shouldBeInstanceOf
import java.math.BigInteger
import kotlinx.serialization.json.Json

@OptIn(ExperimentalStdlibApi::class)
internal class AbiCodecTest : DescribeSpec({

    context("encode") {
        it("Transfer") {
            val abi =
                // https://etherscan.io/tx/0xdd377131912e9ac4ca74a60135dc3c9ac7a416d2b956315c34e34432a9bfae9f#eventlog
                Json.decodeFromString<AbiItem>("""{"inputs":[{"name":"recipient","type":"address","internalType":"address"},{"name":"amount","type":"uint256","internalType":"uint256"}],"name":"transfer","outputs":[{"name":"","type":"bool","internalType":"bool"}],"stateMutability":"nonpayable","type":"function"}""")

            val result = AbiCodec.encode(
                abi.inputs,
                listOf("0x2b8A6034c83706aacC846ddc4a2c8E943C09aE44", 60220000.toBigInteger())
            )

            result shouldBe "0000000000000000000000002b8a6034c83706aacc846ddc4a2c8e943c09ae44000000000000000000000000000000000000000000000000000000000396e260"
        }
    }

    context("decodeLog") {
        it("TransactionBatchAppended") {
            val abi =
                // https://etherscan.io/tx/0x0aef838f174a0f9a8c19215c958f793824224c56905620d314b627af65832121#eventlog
                Json.decodeFromString<AbiItem>("""{"anonymous":false,"inputs":[{"indexed":true,"internalType":"uint256","name":"_batchIndex","type":"uint256"},{"indexed":false,"internalType":"bytes32","name":"_batchRoot","type":"bytes32"},{"indexed":false,"internalType":"uint256","name":"_batchSize","type":"uint256"},{"indexed":false,"internalType":"uint256","name":"_prevTotalElements","type":"uint256"},{"indexed":false,"internalType":"bytes","name":"_extraData","type":"bytes"}],"name":"TransactionBatchAppended","type":"event"}""")

            val hex =
                "0x8c5b901f0037e84123ec2c8289ba4771b95052385ba97da5f39461c26ca0125e000000000000000000000000000000000000000000000000000000000000004d0000000000000000000000000000000000000000000000000000000001fbf85e00000000000000000000000000000000000000000000000000000000000000800000000000000000000000000000000000000000000000000000000000000000"

            val topics = listOf(
                "0x127186556e7be68c7e31263195225b4de02820707889540969f62c05cf73525e",
                "0x000000000000000000000000000000000000000000000000000000000003ad3b"
            )

            val result = AbiCodec.decodeLog(abi.inputs, hex, topics)

            result["_batchIndex"].shouldBeInstanceOf<BigInteger>() shouldBe 240955.toBigInteger()
            result["_batchRoot"].shouldBeInstanceOf<ByteArray>()
                .toHexString() shouldBeEqualIgnoringCase "8C5B901F0037E84123EC2C8289BA4771B95052385BA97DA5F39461C26CA0125E"
            result["_batchSize"].shouldBeInstanceOf<BigInteger>() shouldBe 77.toBigInteger()
            result["_prevTotalElements"].shouldBeInstanceOf<BigInteger>() shouldBe 33290334.toBigInteger()
            result["_extraData"].shouldBeInstanceOf<ByteArray>() shouldHaveSize 0
        }

        it("Transfer") {
            val abi =
                // https://etherscan.io/tx/0xdd377131912e9ac4ca74a60135dc3c9ac7a416d2b956315c34e34432a9bfae9f#eventlog
                Json.decodeFromString<AbiItem>("""{"anonymous":false,"inputs":[{"indexed":true,"internalType":"address","name":"from","type":"address"},{"indexed":true,"internalType":"address","name":"to","type":"address"},{"indexed":false,"internalType":"uint256","name":"value","type":"uint256"}],"name":"Transfer","type":"event"}""")

            val hex =
                "0x000000000000000000000000000000000000000000000000000000000396e260"

            val topics = listOf(
                "0xddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef",
                "0x000000000000000000000000cb983a3178ad960d8ed7288cd9d66c6b4b2b5f66",
                "0x0000000000000000000000002b8a6034c83706aacc846ddc4a2c8e943c09ae44",
            )

            val result = AbiCodec.decodeLog(abi.inputs, hex, topics)

            result["from"].shouldBeInstanceOf<String>() shouldBeEqualIgnoringCase "0xCb983A3178aD960d8ED7288cD9D66c6B4b2B5F66"
            result["to"].shouldBeInstanceOf<String>() shouldBeEqualIgnoringCase "0x2b8A6034c83706aacC846ddc4a2c8E943C09aE44"
            result["value"].shouldBeInstanceOf<BigInteger>() shouldBe 60220000.toBigInteger()
        }
    }

    context("decodeParameters") {
        it("setL1BlockValues") {
            val abi =
                Json.decodeFromString<AbiItem>("""{"inputs":[{"internalType":"uint64","name":"_number","type":"uint64"},{"internalType":"uint64","name":"_timestamp","type":"uint64"},{"internalType":"uint256","name":"_basefee","type":"uint256"},{"internalType":"bytes32","name":"_hash","type":"bytes32"},{"internalType":"uint64","name":"_sequenceNumber","type":"uint64"},{"internalType":"bytes32","name":"_batcherHash","type":"bytes32"},{"internalType":"uint256","name":"_l1FeeOverhead","type":"uint256"},{"internalType":"uint256","name":"_l1FeeScalar","type":"uint256"}],"name":"setL1BlockValues","outputs":[],"stateMutability":"nonpayable","type":"function"}""")

            val hex =
                "0x015d8eb9000000000000000000000000000000000000000000000000000000000006cf4900000000000000000000000000000000000000000000000000000000639833b0000000000000000000000000000000000000000000000000000000000000000751c54ac1869a3f078ff0aa2994f27f6aceb5d2bda18ec30f2593fa20d9c052fa000000000000000000000000000000000000000000000000000000000000000000000000000000000000000016cb0a409497493c2ef7688f69534fd8f8f23b74000000000000000000000000000000000000000000000000000000000000083400000000000000000000000000000000000000000000000000000000000f4240"

            val result = AbiCodec.decodeParameters(abi.inputs.map { it.type }, hex.drop(10))

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
                Json.decodeFromString<AbiItem>("""{"inputs":[{"components":[{"internalType":"address","name":"tokenIn","type":"address"},{"internalType":"address","name":"tokenOut","type":"address"},{"internalType":"uint24","name":"fee","type":"uint24"},{"internalType":"address","name":"recipient","type":"address"},{"internalType":"uint256","name":"deadline","type":"uint256"},{"internalType":"uint256","name":"amountIn","type":"uint256"},{"internalType":"uint256","name":"amountOutMinimum","type":"uint256"},{"internalType":"uint160","name":"sqrtPriceLimitX96","type":"uint160"}],"internalType":"struct ISwapRouter.ExactInputSingleParams","name":"params","type":"tuple"}],"name":"exactInputSingle","outputs":[{"internalType":"uint256","name":"amountOut","type":"uint256"}],"stateMutability":"payable","type":"function"}""")

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