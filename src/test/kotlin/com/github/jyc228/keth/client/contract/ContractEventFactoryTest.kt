package com.github.jyc228.keth.client.contract

import com.github.jyc228.keth.client.eth.Log
import com.github.jyc228.keth.client.eth.Topics
import com.github.jyc228.keth.type.Address
import com.github.jyc228.keth.type.Hash
import com.github.jyc228.keth.type.HexData
import com.github.jyc228.keth.type.HexInt
import com.github.jyc228.keth.type.HexULong
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import java.math.BigInteger

@OptIn(ExperimentalStdlibApi::class)
class ContractEventFactoryTest : DescribeSpec({
    it("decode") {
        val log = Log(
            removed = false,
            logIndex = HexInt(number = 0),
            transactionIndex = HexInt(number = 0),
            transactionHash = Hash(hex = ""),
            blockHash = Hash(hex = ""),
            blockNumber = HexULong(number = 0u),
            address = Address(hex = ""),
            data = HexData("0x000000000000000000000000000000000000000000000000000000000000000100000000000000000000000096f14f89b3f3368f23fcd78876e2bb9290ad246b0000000000000000000000000000000000000000000000203c4343307fa5b800"),
            topics = listOf(
                HexData("0x8c6f22764e9af41b80f2c52b7bc67a82970e4197b5033c1ea1f6e61cff244d5f"),
                HexData("0x80b3009f20b261879d78a10b73125f5d213bcc152450f6cf6a1080386b362525"),
                HexData("0x00000000000000000000000096f14f89b3f3368f23fcd78876e2bb9290ad246b"),
            )
        )
        val result = ERC20MerkleClaimAirdropped.decode(log)
        "0x${result.root.toHexString()}" shouldBe "0x80b3009f20b261879d78a10b73125f5d213bcc152450f6cf6a1080386b362525"
        result.claimer.with0x shouldBe "0x96f14f89b3f3368f23fcd78876e2bb9290ad246b"
        result.leafData.airdropId shouldBe 1.toBigInteger()
        result.leafData.user.with0x shouldBe "0x96f14f89b3f3368f23fcd78876e2bb9290ad246b"
        result.leafData.amount shouldBe "594638198700000000000".toBigInteger()
    }
})

data class ERC20MerkleClaimAirdropped(
    val root: ByteArray,
    val claimer: Address,
    val leafData: ER20ClaimMLF
) : ContractEvent {
    companion object : ContractEventFactory<ERC20MerkleClaimAirdropped>(
        ERC20MerkleClaimAirdropped::class,
        "0x8c6f22764e9af41b80f2c52b7bc67a82970e4197b5033c1ea1f6e61cff244d5f",
        { """{"anonymous":false,"inputs":[{"name":"root","type":"bytes32","internalType":"bytes32","indexed":true},{"name":"claimer","type":"address","internalType":"address","indexed":true},{"name":"leafData","type":"tuple","components":[{"name":"airdropId","type":"uint256","internalType":"uint256"},{"name":"user","type":"address","internalType":"address"},{"name":"amount","type":"uint256","internalType":"uint256"}],"internalType":"struct ER20ClaimMLF","indexed":false}],"name":"ERC20MerkleClaimAirdropped","type":"event"}""" }
    ) {
        fun Topics.filterByRoot(vararg root: ByteArray) = apply { filterByByteArray(1, *root) }
        fun Topics.filterByClaimer(vararg claimer: Address) = apply { filterByAddress(2, *claimer) }
    }
}

data class ER20ClaimMLF(
    val airdropId: BigInteger,
    val user: Address,
    val amount: BigInteger
)
