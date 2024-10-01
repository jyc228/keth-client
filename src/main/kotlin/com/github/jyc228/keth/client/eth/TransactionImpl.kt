package com.github.jyc228.keth.client.eth

import com.github.jyc228.keth.type.AccessListTransactionBuilder
import com.github.jyc228.keth.type.Address
import com.github.jyc228.keth.type.BlobTransactionBuilder
import com.github.jyc228.keth.type.DynamicFeeTransactionBuilder
import com.github.jyc228.keth.type.Hash
import com.github.jyc228.keth.type.HexBigInt
import com.github.jyc228.keth.type.HexInt
import com.github.jyc228.keth.type.HexULong
import com.github.jyc228.keth.type.LegacyTransactionBuilder
import com.github.jyc228.keth.type.NullBlockHash
import com.github.jyc228.keth.type.NullBlockNumber
import com.github.jyc228.keth.type.NullGas
import com.github.jyc228.keth.type.NullList
import com.github.jyc228.keth.type.NullTxIndex
import kotlinx.serialization.Serializable

@Serializable
data class RpcTransaction(
    @Serializable(NullBlockHash::class)
    override val blockHash: Hash = Transaction.pendingBlockHash,

    @Serializable(NullBlockNumber::class)
    override val blockNumber: HexULong = Transaction.pendingBlockNumber,

    override val hash: Hash = nullHash,
    override val from: Address = nullAddress,
    override var to: Address? = null,
    override var input: String = "",
    override var value: HexBigInt = HexBigInt.ZERO,
    override var nonce: HexULong = HexULong.ZERO,
    override var gas: HexBigInt = HexBigInt.ZERO,
    override var gasPrice: HexBigInt? = null,

    @Serializable(NullTxIndex::class)
    override val transactionIndex: HexInt = HexInt.ZERO,
    override val type: TransactionType,
    override var v: HexBigInt? = null,
    override var r: HexBigInt? = null,
    override var s: HexBigInt? = null,
    override var chainId: HexULong? = null,
    override val yParity: HexULong? = null,

    @Serializable(NullList::class)
    override var accessList: List<Access> = emptyList(),
    @Serializable(NullGas::class)
    override var maxFeePerGas: HexBigInt = HexBigInt.ZERO,
    @Serializable(NullGas::class)
    override var maxPriorityFeePerGas: HexBigInt = HexBigInt.ZERO,
    @Serializable(NullGas::class)
    override var maxFeePerBlobGas: HexBigInt = HexBigInt.ZERO,
    @Serializable(NullList::class)
    override var blobVersionedHashes: List<Hash> = emptyList(),
) : Transaction,
    LegacyTransactionBuilder,
    AccessListTransactionBuilder,
    DynamicFeeTransactionBuilder,
    BlobTransactionBuilder {
    companion object {
        private val nullHash = Hash.unsafe("null")
        private val nullAddress = Address.unsafe("null")
    }
}