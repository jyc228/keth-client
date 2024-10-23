package com.github.jyc228.keth.client.eth

import com.github.jyc228.keth.type.Hash
import io.kotest.assertions.json.shouldEqualJson
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.resource.resourceAsString
import io.kotest.matchers.shouldBe

class BlockJsonTest : StringSpec({
    "decode block no tx" {
        val block = decodeJsonResource<RpcBlock<Block.TransactionHash>>("/block/block_no_tx.json")
        block.hash shouldBe Hash("0xb4bc78f3a7c239836325b3fc2f12ff788fee0180caa86e82afd4f3887230e168")
        block.transactions shouldHaveSize 0
    }

    "decode block with tx hash" {
        val block = decodeJsonResource<RpcBlock<Block.TransactionHash>>("/block/block_with_tx_hash.json")
        block.hash shouldBe Hash("0xb4bc78f3a7c239836325b3fc2f12ff788fee0180caa86e82afd4f3887230e168")
        block.transactions shouldHaveSize 111
    }

    "decode block with tx object" {
        val block = decodeJsonResource<RpcBlock<Block.TransactionObject>>("/block/block_with_tx.json")
        block.hash shouldBe Hash("0xb4bc78f3a7c239836325b3fc2f12ff788fee0180caa86e82afd4f3887230e168")
        block.transactions shouldHaveSize 111
        block.withdrawals shouldHaveSize 16
    }

    "decode block with uncle" {
        val block = decodeJsonResource<RpcBlock<Block.TransactionObject>>("/block/block_with_uncle.json")
        block.uncles shouldHaveSize 1
    }

    "decode uncle block" {
        val block = decodeJsonResource<RpcUncleBlock>("/block/uncle_block.json")
        block.stateRoot shouldBe Hash("0xa18dcb578ea2c1d80999c09879550ccb11e86cebe6824c18d3b0981166481be0")
    }

    "encode to header" {
        val block = decodeJsonResource<RpcBlock<Block.TransactionHash>>("/block/block_with_tx_hash.json")
        encodeToJson(block) shouldEqualJson resourceAsString("/block/block_with_tx_hash.json")
    }
})
