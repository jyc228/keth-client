package com.github.jyc228.keth.client.eth

import com.github.jyc228.keth.client.AbstractJsonRpcApi
import com.github.jyc228.keth.client.ApiResult
import com.github.jyc228.keth.client.JsonRpcClientWrapper
import com.github.jyc228.keth.type.Address
import com.github.jyc228.keth.type.BlockReference
import com.github.jyc228.keth.type.CallRequest
import com.github.jyc228.keth.type.GetLogsRequest
import com.github.jyc228.keth.type.Hash
import com.github.jyc228.keth.type.HexBigInt
import com.github.jyc228.keth.type.HexData
import com.github.jyc228.keth.type.HexString
import com.github.jyc228.keth.type.HexULong
import com.github.jyc228.keth.type.Log
import com.github.jyc228.keth.type.RpcBlock
import com.github.jyc228.keth.type.RpcBlockHeader
import com.github.jyc228.keth.type.RpcTransaction
import com.github.jyc228.keth.type.TransactionReceipt
import com.github.jyc228.keth.type.Transactions
import com.github.jyc228.keth.type.UncleBlock
import java.math.BigInteger
import org.web3j.crypto.Credentials
import org.web3j.crypto.TransactionEncoder
import org.web3j.utils.Numeric

class EthJsonRpcApi(client: JsonRpcClientWrapper) : EthApi, AbstractJsonRpcApi(client) {

    override suspend fun chainId(): ApiResult<HexULong> = "eth_chainId"()
    override suspend fun gasPrice(): ApiResult<HexBigInt> = "eth_gasPrice"()
    override suspend fun blockNumber(): ApiResult<HexULong> = "eth_blockNumber"()

    override suspend fun getHeader(ref: BlockReference): ApiResult<RpcBlockHeader> = when (ref.hash) {
        true -> "eth_getHeaderByHash"(ref.value)
        false -> "eth_getHeaderByNumber"(ref.value)
    }

    override suspend fun <T : Transactions> getBlock(
        ref: BlockReference,
        option: GetBlockOption<T>
    ): ApiResult<RpcBlock<T>?> = when (ref.hash) {
        true -> "eth_getBlockByHash"(ref.value, option.fullTx, option.serializer)
        false -> "eth_getBlockByNumber"(ref.value, option.fullTx, option.serializer)
    }

    override suspend fun getBlockTransactionCountByHash(hash: Hash): ApiResult<HexULong> =
        "eth_getBlockTransactionCountByHash"(hash)

    override suspend fun getBlockTransactionCountByNumber(number: ULong): ApiResult<HexULong> =
        getBlockTransactionCountByNumber(number.ref)

    override suspend fun getBlockTransactionCountByNumber(tag: String): ApiResult<HexULong> =
        getBlockTransactionCountByNumber(tag.ref)

    override suspend fun getBlockTransactionCountByNumber(ref: BlockReference): ApiResult<HexULong> =
        "eth_getBlockTransactionCountByNumber"(ref)

    override suspend fun getTransactionCount(address: Address, ref: BlockReference): ApiResult<HexULong> =
        "eth_getTransactionCount"(address, ref)

    override suspend fun getRawTransactionByHash(hash: Hash): ApiResult<HexData?> = "eth_getRawTransactionByHash"(hash)

    override suspend fun getRawTransactionByBlockHashAndIndex(
        blockHash: Hash,
        index: Int
    ): ApiResult<HexData?> = "eth_getRawTransactionByBlockHashAndIndex"(blockHash, index.hex)

    override suspend fun getRawTransactionByBlockNumberAndIndex(
        blockNumber: ULong,
        index: Int
    ): ApiResult<HexData?> = "eth_getRawTransactionByBlockNumberAndIndex"(blockNumber.ref, index.hex)

    override suspend fun getTransactionByHash(hash: Hash): ApiResult<RpcTransaction?> = "eth_getTransactionByHash"(hash)

    override suspend fun getTransactionByBlockHashAndIndex(
        blockHash: Hash,
        index: Int
    ): ApiResult<RpcTransaction?> = "eth_getTransactionByBlockHashAndIndex"(blockHash, index.hex)

    override suspend fun getTransactionByBlockNumberAndIndex(
        blockNumber: ULong,
        index: Int
    ): ApiResult<RpcTransaction?> = "eth_getTransactionByBlockNumberAndIndex"(blockNumber.ref, index.hex)

    override suspend fun getTransactionReceipt(hash: Hash): ApiResult<TransactionReceipt?> =
        "eth_getTransactionReceipt"(hash)

    override suspend fun getUncleByBlockHashAndIndex(
        blockHash: Hash,
        index: Int
    ): ApiResult<UncleBlock?> = "eth_getUncleByBlockHashAndIndex"(blockHash, index.hex)

    override suspend fun getUncleByBlockNumberAndIndex(
        blockNumber: ULong,
        index: Int
    ): ApiResult<UncleBlock?> = "eth_getUncleByBlockNumberAndIndex"(blockNumber, index.hex)

    override suspend fun getStorageAt(
        address: Address,
        key: HexString,
        ref: BlockReference
    ): ApiResult<HexData?> = "eth_getStorageAt"(address, key.hex, ref)

    override suspend fun getProof(
        address: Address,
        storageKeys: List<HexData>,
        ref: BlockReference
    ): ApiResult<AccountProof?> = "eth_getProof"(address, storageKeys, ref)

    override suspend fun getLogs(request: GetLogsRequest): ApiResult<List<Log>> = "eth_getLogs"(request)

    override suspend fun getBalance(
        address: Address,
        ref: BlockReference
    ): ApiResult<HexBigInt?> = "eth_getBalance"(address, ref)

    override suspend fun getCode(
        address: Address,
        ref: BlockReference
    ): ApiResult<HexData?> = "eth_getCode"(address, ref)

    override suspend fun call(
        request: CallRequest,
        ref: BlockReference
    ): ApiResult<HexData?> = "eth_call"(request, ref)

    override suspend fun estimateGas(request: CallRequest): ApiResult<HexBigInt> = "eth_estimateGas"(request)

    override suspend fun sendRawTransaction(signedTransactionData: String): ApiResult<Hash> =
        "eth_sendRawTransaction"(signedTransactionData)

    override suspend fun sendTransaction(
        account: com.github.jyc228.keth.AccountWithPrivateKey,
        build: suspend TransactionBuilder.() -> Unit
    ): ApiResult<Hash> {
        val client = EthJsonRpcApi(client.toImmediateClient())
        val tx = TransactionBuilder().apply { build() }
        if (tx.gasPrice.number == BigInteger.ZERO) {
            tx.gasPrice = client.gasPrice().awaitOrThrow()
        }
        if (tx.gasLimit.number == BigInteger.ZERO && (tx.input != "" && tx.input != "0x")) {
            tx.gasLimit = client.estimateGas(
                CallRequest(
                    from = account.address.hex,
                    to = tx.to?.hex,
                    gasPrice = tx.gasPrice,
                    data = tx.input,
                    value = tx.value
                )
            ).awaitOrThrow()
        }
        val signedMessage = TransactionEncoder.signMessage(
            tx.toWeb3jTransaction(),
            tx.chainId?.number?.toLong() ?: client.chainId().awaitOrThrow().number.toLong(),
            Credentials.create(account.privateKey)
        )
        return sendRawTransaction(Numeric.toHexString(signedMessage))
    }

    private val ULong.ref get() = BlockReference(this)
    private val String.ref get() = BlockReference.fromTag(this)
    private val Int.hex get() = "0x${toString(16)}"
}
