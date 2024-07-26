package com.github.jyc228.keth.type

fun buildLegacyTransaction(init: LegacyTransactionBuilder.() -> Unit): Transaction {
    return RpcTransaction(type = TransactionType.Legacy).apply(init)
}

fun buildAccessListTransaction(init: AccessListTransactionBuilder.() -> Unit): Transaction {
    return RpcTransaction(type = TransactionType.AccessList).apply(init)
}

fun buildDynamicFeeTransaction(init: DynamicFeeTransactionBuilder.() -> Unit): Transaction {
    return RpcTransaction(type = TransactionType.DynamicFee).apply(init)
}

fun buildBlobTransaction(init: BlobTransactionBuilder.() -> Unit): Transaction {
    return RpcTransaction(type = TransactionType.Blob).apply(init)
}

interface TransactionBuilder {
    var nonce: HexULong
    var gas: HexBigInt
    var to: Address?
    var value: HexBigInt
    var input: String
    var v: HexBigInt?
    var r: HexBigInt?
    var s: HexBigInt?

    fun withSignature(sig: ECDSASignature): TransactionBuilder = apply {
        v = sig.v
        r = sig.r
        s = sig.s
    }
}

interface LegacyTransactionBuilder : TransactionBuilder {
    var gasPrice: HexBigInt?
}

interface AccessListTransactionBuilder : TransactionBuilder {
    var chainId: HexULong?
    var gasPrice: HexBigInt?
    var accessList: List<Access>
}

interface DynamicFeeTransactionBuilder : TransactionBuilder {
    var chainId: HexULong?
    var maxFeePerGas: HexBigInt
    var maxPriorityFeePerGas: HexBigInt
    var accessList: List<Access>
}

interface BlobTransactionBuilder : TransactionBuilder {
    var chainId: HexULong?
    var maxFeePerGas: HexBigInt
    var maxPriorityFeePerGas: HexBigInt
    var maxFeePerBlobGas: HexBigInt
    var blobVersionedHashes: List<Hash>
    var accessList: List<Access>
}
