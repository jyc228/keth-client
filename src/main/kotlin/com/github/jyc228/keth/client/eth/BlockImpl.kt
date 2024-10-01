package com.github.jyc228.keth.client.eth

import com.github.jyc228.keth.type.Address
import com.github.jyc228.keth.type.Hash
import com.github.jyc228.keth.type.HexBigInt
import com.github.jyc228.keth.type.HexData
import com.github.jyc228.keth.type.HexULong
import com.github.jyc228.keth.type.InstantSerializer
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class RpcBlockHeader(
    override val hash: Hash,
    override val parentHash: Hash,
    override val sha3Uncles: Hash,
    override val miner: Address? = null,
    override val stateRoot: Hash,
    override val transactionsRoot: Hash,
    override val receiptsRoot: Hash,
    override val logsBloom: String,
    override val difficulty: HexBigInt,
    override val number: HexULong,
    override val gasLimit: HexBigInt,
    override val gasUsed: HexBigInt,
    @Serializable(InstantSerializer::class)
    override val timestamp: Instant,
    override val extraData: String,
    override val mixHash: Hash,
    override val nonce: HexULong,
    override val totalDifficulty: HexBigInt? = null,
    override val baseFeePerGas: HexBigInt? = null,
    override val withdrawalsRoot: Hash? = null,
    override val parentBeaconBlockRoot: Hash? = null,
    override val blobGasUsed: HexBigInt? = null,
    override val excessBlobGas: HexBigInt? = null,
) : BlockHeader

@Serializable
data class RpcBlock<T : Transactions>(
    override val baseFeePerGas: HexBigInt? = null,
    override val difficulty: HexBigInt,
    override val extraData: String = "",
    override val gasLimit: HexBigInt,
    override val gasUsed: HexBigInt,
    override val hash: Hash,
    override val logsBloom: String = "",
    override val miner: Address? = null,
    override val mixHash: Hash,
    override val nonce: HexULong,
    override val number: HexULong,
    override val parentHash: Hash,
    override val receiptsRoot: Hash,
    override val sha3Uncles: Hash,
    override val size: HexData,
    override val stateRoot: Hash,
    @Serializable(InstantSerializer::class)
    override val timestamp: Instant,
    override val totalDifficulty: HexBigInt? = null,
    override val transactions: T,
    override val transactionsRoot: Hash,
    override val uncles: List<Hash>,
    override val withdrawals: List<Withdrawal> = emptyList(),
    override val withdrawalsRoot: Hash? = null,
    override val parentBeaconBlockRoot: Hash? = null,
    override val blobGasUsed: HexBigInt? = null,
    override val excessBlobGas: HexBigInt? = null,
) : Block<T> {
    override fun toString(): String {
        return "number=${number} hash=${hash} parentHash=${parentHash} timestamp=${timestamp} txCount=${transactions.hashes.size}"
    }
}

@Serializable
data class RpcUncleBlock(
    override val baseFeePerGas: HexBigInt? = null,
    override val difficulty: HexBigInt,
    override val extraData: String = "",
    override val gasLimit: HexBigInt,
    override val gasUsed: HexBigInt,
    override val hash: Hash,
    override val logsBloom: String = "",
    override val miner: Address? = null,
    override val mixHash: Hash,
    override val nonce: HexULong,
    override val number: HexULong,
    override val parentHash: Hash,
    override val receiptsRoot: Hash,
    override val sha3Uncles: Hash,
    override val size: HexData,
    override val stateRoot: Hash,
    @Serializable(InstantSerializer::class)
    override val timestamp: Instant,
    override val totalDifficulty: HexBigInt? = null,
    override val transactionsRoot: Hash,
    override val uncles: List<Hash> = emptyList(),
    override val withdrawals: List<Withdrawal> = emptyList(),
    override val withdrawalsRoot: Hash? = null,
    override val parentBeaconBlockRoot: Hash? = null,
    override val blobGasUsed: HexBigInt? = null,
    override val excessBlobGas: HexBigInt? = null,
) : UncleBlock {
    override fun toString(): String {
        return "number=${number} hash=${hash} parentHash=${parentHash} timestamp=${timestamp}"
    }
}

