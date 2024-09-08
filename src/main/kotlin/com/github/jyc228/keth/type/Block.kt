package com.github.jyc228.keth.type

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

interface BlockHeader {
    val hash: Hash
    val parentHash: Hash
    val sha3Uncles: Hash
    val miner: Address?
    val stateRoot: Hash
    val transactionsRoot: Hash
    val receiptsRoot: Hash
    val logsBloom: String
    val difficulty: HexBigInt
    val number: HexULong
    val gasLimit: HexBigInt
    val gasUsed: HexBigInt
    val timestamp: Instant
    val extraData: String
    val mixHash: Hash
    val nonce: HexULong
    val totalDifficulty: HexBigInt?

    // BaseFee was added by EIP-1559 and is ignored in legacy headers.
    val baseFeePerGas: HexBigInt?

    // WithdrawalsHash was added by EIP-4895 and is ignored in legacy headers.
    val withdrawalsRoot: Hash?
    val parentBeaconBlockRoot: Hash?
    val blobGasUsed: HexBigInt?
    val excessBlobGas: HexBigInt?
}

interface Block<T : Transactions> : BlockHeader {
    val size: HexData
    val transactions: T
    val uncles: List<Hash>
    val withdrawals: List<Withdrawal>
}

sealed interface Transactions {
    val hashes: List<Hash>
}

@Serializable(TransactionHashesSerializer::class)
class TransactionHashes(override val hashes: List<Hash>) : Transactions, List<Hash> by hashes

@Serializable(TransactionObjectsSerializer::class)
class TransactionObjects(self: List<Transaction>) : Transactions, List<Transaction> by self {
    override val hashes: List<Hash> = object : AbstractList<Hash>() {
        override val size: Int get() = this@TransactionObjects.size
        override fun get(index: Int): Hash = this@TransactionObjects[index].hash
    }
}

interface UncleBlock : BlockHeader {
    val size: HexData
    val uncles: List<Hash>
    val withdrawals: List<Withdrawal>
}

@Serializable
data class Withdrawal(
    val address: Address,
    val amount: HexBigInt,
    val index: HexInt,
    val validatorIndex: HexInt
)