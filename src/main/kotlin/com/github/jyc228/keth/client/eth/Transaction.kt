package com.github.jyc228.keth.client.eth

import com.github.jyc228.keth.type.Address
import com.github.jyc228.keth.type.Hash
import com.github.jyc228.keth.type.HexBigInt
import com.github.jyc228.keth.type.HexInt
import com.github.jyc228.keth.type.HexString
import com.github.jyc228.keth.type.HexULong
import com.github.jyc228.keth.type.TransactionStatusSerializer
import com.github.jyc228.keth.type.TransactionTypeSerializer
import com.github.jyc228.keth.type.hexToInt
import java.math.BigInteger
import kotlinx.serialization.Serializable

interface ECDSASignature {
    val v: HexBigInt?
    val r: HexBigInt?
    val s: HexBigInt?

    data class Mutable(
        override var r: HexBigInt?,
        override var s: HexBigInt?,
        override var v: HexBigInt?
    ) : ECDSASignature

    companion object {
        fun fromBytes(bytes: ByteArray): ECDSASignature {
            require(bytes.size == 65) { "wrong size for signature: ${bytes.size}" }
            return Mutable(
                r = HexBigInt(BigInteger(1, bytes.sliceArray(0 until 32))),
                s = HexBigInt(BigInteger(1, bytes.sliceArray(32 until 64))),
                v = HexBigInt(BigInteger(1, byteArrayOf((bytes[64] + 27).toByte())))
            )
        }
    }
}

interface Transaction : ECDSASignature {
    val blockHash: Hash
    val blockNumber: HexULong
    val hash: Hash
    val from: Address
    val to: Address?
    val input: String
    val value: HexBigInt
    val nonce: HexULong
    val gas: HexBigInt
    val gasPrice: HexBigInt?
    val transactionIndex: HexInt
    val type: TransactionType
    val accessList: List<Access>
    val maxFeePerGas: HexBigInt
    val maxPriorityFeePerGas: HexBigInt
    val maxFeePerBlobGas: HexBigInt
    val blobVersionedHashes: List<Hash>
    val chainId: HexULong?
    val yParity: HexULong?

    companion object {
        val pendingBlockHash = Hash.unsafe("pending")
        val pendingBlockNumber = HexULong(0uL)
    }
}

@Serializable
data class TransactionReceipt(
    val transactionHash: Hash,
    val transactionIndex: HexInt,
    val blockHash: Hash,
    val blockNumber: HexULong,
    val from: Address,
    /** address of the receiver. null when its a contract creation transaction. */
    val to: Address?,
    val effectiveGasPrice: HexBigInt?,
    val cumulativeGasUsed: HexBigInt,
    val gasUsed: HexBigInt,
    /** The contract address created, if the transaction was a contract creation, otherwise null. */
    val contractAddress: Address?,
    val logs: List<Log> = emptyList(),
    val logsBloom: String = "",
    val status: TransactionStatus,
    val type: TransactionType?
)

@Serializable
data class Access(val address: Address, val storageKeys: List<Hash> = emptyList())

@Serializable(TransactionTypeSerializer::class)
sealed class TransactionType(val value: Int, override val hex: String) : HexString() {
    data object Legacy : TransactionType(0, "0x0")
    data object AccessList : TransactionType(1, "0x1")
    data object DynamicFee : TransactionType(2, "0x2")
    data object Blob : TransactionType(3, "0x3")
    class Unknown(hex: String) : TransactionType(hex.hexToInt(), hex)

    companion object {
        fun from(hex: String): TransactionType = Legacy.takeIf { it.hex == hex }
            ?: AccessList.takeIf { it.hex == hex }
            ?: DynamicFee.takeIf { it.hex == hex }
            ?: Blob.takeIf { it.hex == hex }
            ?: Unknown(hex)
    }
}

@Serializable(TransactionStatusSerializer::class)
sealed class TransactionStatus(val value: Int, override val hex: String) : HexString() {
    data object Fail : TransactionStatus(0, "0x0")
    data object Success : TransactionStatus(1, "0x1")
    class Unknown(hex: String) : TransactionStatus(hex.hexToInt(), hex.removePrefix("0x"))

    companion object {
        fun from(hex: String): TransactionStatus = Fail.takeIf { it.hex == hex }
            ?: Success.takeIf { it.hex == hex }
            ?: Unknown(hex)
    }
}
