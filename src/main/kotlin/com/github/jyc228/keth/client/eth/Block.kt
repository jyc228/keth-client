package com.github.jyc228.keth.client.eth

import com.github.jyc228.keth.type.Address
import com.github.jyc228.keth.type.Hash
import com.github.jyc228.keth.type.HexBigInt
import com.github.jyc228.keth.type.HexData
import com.github.jyc228.keth.type.HexInt
import com.github.jyc228.keth.type.HexULong
import com.github.jyc228.keth.type.TransactionHashSerializer
import com.github.jyc228.keth.type.TransactionObjectSerializer
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

interface Block<T : Block.TransactionElement> : BlockHeader {
    val size: HexData
    val transactions: List<T>
    val uncles: List<Hash>
    val withdrawals: List<Withdrawal>

    sealed interface TransactionElement

    @Serializable(TransactionHashSerializer::class)
    class TransactionHash(val hash: Hash) : TransactionElement {
        override fun toString(): String = hash.toString()
    }

    @Serializable(TransactionObjectSerializer::class)
    class TransactionObject(private val self: Transaction) : TransactionElement, Transaction by self {
        override fun toString(): String = self.toString()
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