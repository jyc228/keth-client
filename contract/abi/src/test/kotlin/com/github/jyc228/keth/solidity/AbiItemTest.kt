package com.github.jyc228.keth.solidity

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class AbiItemTest : DescribeSpec({
    context("event") {
        it("Transfer") {
            val abi =
                AbiItem.fromJson("""{"anonymous":false,"inputs":[{"name":"from","type":"address","indexed":true},{"name":"to","type":"address","indexed":true},{"name":"value","type":"uint256","indexed":false}],"name":"Transfer","type":"event"}""")
            abi.computeSig() shouldBe "ddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef"
        }

        it("ERC20MerkleClaimAirdropped") {
            val abi =
                AbiItem.fromJson("""{"anonymous":false,"inputs":[{"name":"root","type":"bytes32","internalType":"bytes32","indexed":true},{"name":"claimer","type":"address","internalType":"address","indexed":true},{"name":"leafData","type":"tuple","components":[{"name":"airdropId","type":"uint256","internalType":"uint256"},{"name":"user","type":"address","internalType":"address"},{"name":"amount","type":"uint256","internalType":"uint256"}],"internalType":"struct ER20ClaimMLF","indexed":false}],"name":"ERC20MerkleClaimAirdropped","type":"event"}""")
            abi.computeSig() shouldBe "8c6f22764e9af41b80f2c52b7bc67a82970e4197b5033c1ea1f6e61cff244d5f"
        }
    }
})