package com.github.jyc228.keth.client.contract

import com.github.jyc228.keth.solidity.AbiCodec
import com.github.jyc228.keth.solidity.AbiItem
import com.github.jyc228.keth.type.Address
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldBeEqualIgnoringCase
import io.kotest.matchers.types.shouldBeInstanceOf
import java.math.BigInteger

@OptIn(ExperimentalStdlibApi::class)
class AbiCodecExtensionTest : DescribeSpec({
    context("decodeLog") {
        it("TransactionBatchAppended") {
            val abi =
                // https://etherscan.io/tx/0x0aef838f174a0f9a8c19215c958f793824224c56905620d314b627af65832121#eventlog
                AbiItem.fromJson("""{"anonymous":false,"inputs":[{"indexed":true,"internalType":"uint256","name":"_batchIndex","type":"uint256"},{"indexed":false,"internalType":"bytes32","name":"_batchRoot","type":"bytes32"},{"indexed":false,"internalType":"uint256","name":"_batchSize","type":"uint256"},{"indexed":false,"internalType":"uint256","name":"_prevTotalElements","type":"uint256"},{"indexed":false,"internalType":"bytes","name":"_extraData","type":"bytes"}],"name":"TransactionBatchAppended","type":"event"}""")

            val hex =
                "0x8c5b901f0037e84123ec2c8289ba4771b95052385ba97da5f39461c26ca0125e000000000000000000000000000000000000000000000000000000000000004d0000000000000000000000000000000000000000000000000000000001fbf85e00000000000000000000000000000000000000000000000000000000000000800000000000000000000000000000000000000000000000000000000000000000"

            val topics = listOf(
                "0x127186556e7be68c7e31263195225b4de02820707889540969f62c05cf73525e",
                "0x000000000000000000000000000000000000000000000000000000000003ad3b"
            )

            val result = AbiCodec.decodeLog(abi.inputs, topics, hex)

            result[0].shouldBeInstanceOf<BigInteger>() shouldBe 240955.toBigInteger()
            result[1].shouldBeInstanceOf<ByteArray>()
                .toHexString() shouldBeEqualIgnoringCase "8C5B901F0037E84123EC2C8289BA4771B95052385BA97DA5F39461C26CA0125E"
            result[2].shouldBeInstanceOf<BigInteger>() shouldBe 77.toBigInteger()
            result[3].shouldBeInstanceOf<BigInteger>() shouldBe 33290334.toBigInteger()
            result[4].shouldBeInstanceOf<ByteArray>() shouldHaveSize 0
        }

        it("Transfer") {
            val abi =
                // https://etherscan.io/tx/0xdd377131912e9ac4ca74a60135dc3c9ac7a416d2b956315c34e34432a9bfae9f#eventlog
                AbiItem.fromJson("""{"anonymous":false,"inputs":[{"indexed":true,"internalType":"address","name":"from","type":"address"},{"indexed":true,"internalType":"address","name":"to","type":"address"},{"indexed":false,"internalType":"uint256","name":"value","type":"uint256"}],"name":"Transfer","type":"event"}""")

            val hex =
                "0x000000000000000000000000000000000000000000000000000000000396e260"

            val topics = listOf(
                "0xddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef",
                "0x000000000000000000000000cb983a3178ad960d8ed7288cd9d66c6b4b2b5f66",
                "0x0000000000000000000000002b8a6034c83706aacc846ddc4a2c8e943c09ae44",
            )

            val result = AbiCodec.decodeLog(abi.inputs, topics, hex)

            result[0].shouldBeInstanceOf<String>() shouldBeEqualIgnoringCase "0xCb983A3178aD960d8ED7288cD9D66c6B4b2B5F66"
            result[1].shouldBeInstanceOf<String>() shouldBeEqualIgnoringCase "0x2b8A6034c83706aacC846ddc4a2c8E943C09aE44"
            result[2].shouldBeInstanceOf<BigInteger>() shouldBe 60220000.toBigInteger()
        }
    }

    context("decodeParameter") {
        it("exactInputSingle") {
            val result = abiCodec.decodeFunctionCall(
                SmartRouter.exactInputSingle.abi.inputs,
                SmartRouter::exactInputSingle.parameters,
                "0x04e45aaf000000000000000000000000152649ea73beab28c5b49b26eb48f7ead6d4c898000000000000000000000000c02aaa39b223fe8d0a0e5c4f27ead9083c756cc200000000000000000000000000000000000000000000000000000000000009c4000000000000000000000000ec67e10a5991ab7ae9ea3d47a1f3725009abb92c00000000000000000000000000000000000000000000006c6b935b8bbd40000000000000000000000000000000000000000000000000000014f82c2d4eb4f7000000000000000000000000000000000000000000000000000000000000000000"
            )

            result shouldHaveSize 1
            result[0].shouldBeInstanceOf<IV3SwapRouter.ExactInputSingleParams>()
        }
    }
})

private interface SmartRouter {
    fun exactInputSingle(params: IV3SwapRouter.ExactInputSingleParams): ContractFunctionRequest<BigInteger>

    companion object {
        val exactInputSingle = ContractFunctionP1(
            SmartRouter::exactInputSingle,
            "0x5d76b977a20678c085d64cafedd47d4dcdcb7a9384bad25755ca7b988efaf6f6"
        ) { """{"inputs":[{"name":"params","type":"tuple","components":[{"name":"tokenIn","type":"address","internalType":"address"},{"name":"tokenOut","type":"address","internalType":"address"},{"name":"fee","type":"uint24","internalType":"uint24"},{"name":"recipient","type":"address","internalType":"address"},{"name":"amountIn","type":"uint256","internalType":"uint256"},{"name":"amountOutMinimum","type":"uint256","internalType":"uint256"},{"name":"sqrtPriceLimitX96","type":"uint160","internalType":"uint160"}],"internalType":"struct IV3SwapRouter.ExactInputSingleParams"}],"name":"exactInputSingle","outputs":[{"name":"amountOut","type":"uint256","internalType":"uint256"}],"stateMutability":"payable","type":"function"}""" }
    }
}

private object IV3SwapRouter {
    data class ExactInputSingleParams(
        val tokenIn: Address,
        val tokenOut: Address,
        val fee: BigInteger,
        val recipient: Address,
        val amountIn: BigInteger,
        val amountOutMinimum: BigInteger,
        val sqrtPriceLimitX96: BigInteger
    )
}
