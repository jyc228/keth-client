package com.github.jyc228.keth.client.eth

import com.github.jyc228.keth.client.AbstractJsonRpcApi
import com.github.jyc228.keth.client.ApiResult
import com.github.jyc228.keth.client.JsonRpcClientWrapper
import com.github.jyc228.keth.type.Address
import com.github.jyc228.keth.type.Hash
import com.github.jyc228.keth.type.HexBigInt
import com.github.jyc228.keth.type.HexData
import com.github.jyc228.keth.type.HexString
import com.github.jyc228.keth.type.HexULong
import java.math.BigInteger
import org.web3j.crypto.Credentials
import org.web3j.crypto.TransactionEncoder
import org.web3j.utils.Numeric

class EthJsonRpcApi(
    client: JsonRpcClientWrapper,
    private val config: SerializerConfig
) : EthApi, AbstractJsonRpcApi(client) {

    override suspend fun chainId(): ApiResult<HexULong> = "eth_chainId"()
    override suspend fun gasPrice(): ApiResult<HexBigInt> = "eth_gasPrice"()
    override suspend fun blockNumber(): ApiResult<HexULong> = "eth_blockNumber"()

    override suspend fun getHeader(ref: BlockReference): ApiResult<BlockHeader> = when (ref.hash) {
        true -> "eth_getHeaderByHash"(ref.value, config.blockHeader)
        false -> "eth_getHeaderByNumber"(ref.value, config.blockHeader)
    }

    override suspend fun <E : Block.TransactionElement> getBlock(
        option: GetBlockOption<E>,
        ref: BlockReference
    ): ApiResult<Block<E>?> = when (ref.hash) {
        true -> "eth_getBlockByHash"(ref.value, option.fullTx, config.getBlockSerializer(option))
        false -> "eth_getBlockByNumber"(ref.value, option.fullTx, config.getBlockSerializer(option))
    }

    override suspend fun getBlockTransactionCount(ref: BlockReference): ApiResult<HexULong> = when (ref.hash) {
        true -> "eth_getBlockTransactionCountByHash"(ref.value)
        false -> "eth_getBlockTransactionCountByNumber"(ref.value)
    }

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

    override suspend fun getTransactionByHash(hash: Hash): ApiResult<Transaction?> =
        "eth_getTransactionByHash"(hash, RpcTransaction.serializer())

    override suspend fun getTransactionByBlock(
        ref: BlockReference,
        index: Int
    ): ApiResult<Transaction?> = when (ref.hash) {
        true -> "eth_getTransactionByBlockHashAndIndex"(ref.value, index.hex, config.transaction)
        false -> "eth_getTransactionByBlockNumberAndIndex"(ref.value, index.hex, config.transaction)
    }

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

    override suspend fun newFilter(request: GetLogsRequest): ApiResult<String> = "eth_newFilter"(request)
    override suspend fun uninstallFilter(filterId: String): ApiResult<Boolean> = "eth_uninstallFilter"(filterId)
    override suspend fun getFilterLogs(filterId: String): ApiResult<List<Log>> = "eth_getFilterLogs"(filterId)
    override suspend fun <T> getFilterChanges(filterId: FilterId<T>): ApiResult<List<T>> =
        "eth_getFilterChanges"(filterId.id, filterId.serializer)

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
        val client = EthJsonRpcApi(client.toImmediateClient(), config)
        val tx = TransactionBuilder().apply { build() }
        if (tx.gasPrice.number == BigInteger.ZERO) {
            tx.gasPrice = client.gasPrice().awaitOrThrow()
        }
        if (tx.gasLimit.number == BigInteger.ZERO && (tx.input != "" && tx.input != "0x")) {
            tx.gasLimit = client.estimateGas(
                CallRequest(
                    from = account.address,
                    to = tx.to,
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
