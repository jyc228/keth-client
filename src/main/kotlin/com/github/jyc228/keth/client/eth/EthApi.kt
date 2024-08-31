package com.github.jyc228.keth.client.eth

import com.github.jyc228.keth.client.ApiResult
import com.github.jyc228.keth.contract.ContractEvent
import com.github.jyc228.keth.contract.ContractEventFactory
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
import com.github.jyc228.keth.type.Topics
import com.github.jyc228.keth.type.Transaction
import com.github.jyc228.keth.type.TransactionReceipt
import com.github.jyc228.keth.type.Transactions
import com.github.jyc228.keth.type.UncleBlock
import com.github.jyc228.keth.type.ref

interface EthApi {
    suspend fun chainId(): ApiResult<HexULong>
    suspend fun gasPrice(): ApiResult<HexBigInt>
    suspend fun blockNumber(): ApiResult<HexULong>

    suspend fun getHeader(ref: BlockReference): ApiResult<RpcBlockHeader>
    suspend fun getHeaders(numbers: ULongProgression) = numbers.map { getHeader(it.ref) }
    suspend fun getHeaderByHash(hash: Hash): ApiResult<RpcBlockHeader> = getHeader(hash.ref)
    suspend fun getHeaderByNumber(number: ULong): ApiResult<RpcBlockHeader> = getHeader(number.ref)
    suspend fun getHeaderByNumber(tag: String): ApiResult<RpcBlockHeader> = getHeader(BlockReference.fromTag(tag))

    suspend fun <T : Transactions> getBlock(
        ref: BlockReference = BlockReference.latest,
        option: GetBlockOption<T>
    ): ApiResult<RpcBlock<T>?>

    suspend fun <T : Transactions> getBlocks(
        numbers: ULongProgression,
        option: GetBlockOption<T>
    ): List<ApiResult<RpcBlock<T>?>> = numbers.map { getBlock(it.ref, option) }

    suspend fun <T : Transactions> getBlockByHash(
        hash: Hash,
        option: GetBlockOption<T>
    ): ApiResult<RpcBlock<T>?> = getBlock(hash.ref, option)

    suspend fun <T : Transactions> getBlockByNumber(
        number: ULong,
        option: GetBlockOption<T>
    ): ApiResult<RpcBlock<T>?> = getBlock(number.ref, option)

    suspend fun <T : Transactions> getBlockByNumber(
        tag: String,
        option: GetBlockOption<T>
    ): ApiResult<RpcBlock<T>?> = getBlock(BlockReference.fromTag(tag), option)

    suspend fun getBlockTransactionCountByHash(hash: Hash): ApiResult<HexULong>
    suspend fun getBlockTransactionCountByNumber(number: ULong): ApiResult<HexULong>
    suspend fun getBlockTransactionCountByNumber(tag: String): ApiResult<HexULong>
    suspend fun getBlockTransactionCountByNumber(ref: BlockReference = BlockReference.latest): ApiResult<HexULong>

    suspend fun getTransactionCount(address: Address, ref: BlockReference = BlockReference.latest): ApiResult<HexULong>

    suspend fun getRawTransactionByHash(hash: Hash): ApiResult<HexData?>
    suspend fun getRawTransactionByBlockHashAndIndex(blockHash: Hash, index: Int): ApiResult<HexData?>
    suspend fun getRawTransactionByBlockNumberAndIndex(blockNumber: ULong, index: Int): ApiResult<HexData?>

    suspend fun getTransactionByHash(hash: Hash): ApiResult<out Transaction?>
    suspend fun getTransactionByBlockHashAndIndex(blockHash: Hash, index: Int): ApiResult<out Transaction?>
    suspend fun getTransactionByBlockNumberAndIndex(blockNumber: ULong, index: Int): ApiResult<out Transaction?>

    suspend fun getTransactionReceipt(hash: Hash): ApiResult<TransactionReceipt?>

    suspend fun getUncleByBlockHashAndIndex(blockHash: Hash, index: Int): ApiResult<UncleBlock?>
    suspend fun getUncleByBlockNumberAndIndex(blockNumber: ULong, index: Int): ApiResult<UncleBlock?>

    suspend fun getStorageAt(
        address: Address,
        key: HexString,
        ref: BlockReference = BlockReference.latest
    ): ApiResult<HexData?>

    suspend fun getProof(
        address: Address,
        storageKeys: List<HexData>,
        ref: BlockReference = BlockReference.latest
    ): ApiResult<AccountProof?>

    suspend fun newFilter(request: GetLogsRequest): ApiResult<String>
    suspend fun newFilter(init: GetLogsRequest.() -> Unit): ApiResult<String> = newFilter(GetLogsRequest().apply(init))
    suspend fun uninstallFilter(filterId: String): ApiResult<Boolean>
    suspend fun getFilterLogs(filterId: String): ApiResult<List<Log>>

    suspend fun getLogs(request: GetLogsRequest): ApiResult<List<Log>>
    suspend fun getBalance(address: Address, ref: BlockReference = BlockReference.latest): ApiResult<HexBigInt?>
    suspend fun getCode(address: Address, ref: BlockReference = BlockReference.latest): ApiResult<HexData?>

    suspend fun call(request: CallRequest, ref: BlockReference = BlockReference.latest): ApiResult<HexData?>
    suspend fun estimateGas(request: CallRequest): ApiResult<HexBigInt>
    suspend fun sendRawTransaction(signedTransactionData: String): ApiResult<Hash>
    suspend fun sendTransaction(
        account: com.github.jyc228.keth.AccountWithPrivateKey,
        build: suspend TransactionBuilder.() -> Unit
    ): ApiResult<Hash>

    suspend fun getLogs(init: GetLogsRequest.() -> Unit): ApiResult<List<Log>> = getLogs(GetLogsRequest().apply(init))

    suspend fun <T : ContractEvent> getLogs(
        event: ContractEventFactory<T>,
        init: GetLogsRequest.() -> Unit
    ): List<Pair<T, Log>> = getLogs(GetLogsRequest(topics = Topics().filterByEvent(event)).apply(init))
        .awaitOrThrow()
        .mapNotNull { log -> event.decodeIf(log.data, log.topics)?.let { e -> e to log } }

    suspend fun call(ref: BlockReference = BlockReference.latest, init: CallRequest.() -> Unit): ApiResult<HexData?> =
        call(CallRequest().apply(init), ref)
}
