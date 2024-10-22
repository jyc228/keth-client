package com.github.jyc228.keth.client.eth

import com.github.jyc228.keth.PrivateAccount
import com.github.jyc228.keth.client.ApiResult
import com.github.jyc228.keth.client.contract.ContractEvent
import com.github.jyc228.keth.client.contract.ContractEventFactory
import com.github.jyc228.keth.client.eth.BlockReference.Companion.latest
import com.github.jyc228.keth.type.Address
import com.github.jyc228.keth.type.Hash
import com.github.jyc228.keth.type.HexBigInt
import com.github.jyc228.keth.type.HexData
import com.github.jyc228.keth.type.HexString
import com.github.jyc228.keth.type.HexULong

// @formatter:off
interface EthApi {
    suspend fun chainId(): ApiResult<HexULong>
    suspend fun gasPrice(): ApiResult<HexBigInt>
    suspend fun blockNumber(): ApiResult<HexULong>

    suspend fun getHeader(ref: BlockReference = latest): ApiResult<BlockHeader>
    suspend fun getHeaders(numbers: ULongRange): List<ApiResult<BlockHeader>> = numbers.map { getHeader(it.ref) }
    suspend fun getHeaderByHash(hash: Hash) = getHeader(hash.ref)
    suspend fun getHeaderByNumber(number: ULong) = getHeader(number.ref)
    suspend fun getHeaderByNumber(tag: String) = getHeader(BlockReference.fromTag(tag))

    suspend fun <E : Block.TransactionElement> getBlock(option: GetBlockOption<E>, ref: BlockReference = latest): ApiResult<Block<E>?>
    suspend fun <E : Block.TransactionElement> getBlocks(numbers: ULongRange, option: GetBlockOption<E>): List<ApiResult<Block<E>?>> = numbers.map { getBlock(option, it.ref) }
    suspend fun <E : Block.TransactionElement> getBlockByHash(hash: Hash, option: GetBlockOption<E>) = getBlock(option, hash.ref)
    suspend fun <E : Block.TransactionElement> getBlockByNumber(number: ULong, option: GetBlockOption<E>) = getBlock(option, number.ref)
    suspend fun <E : Block.TransactionElement> getBlockByNumber(tag: String, option: GetBlockOption<E>) = getBlock(option, BlockReference.fromTag(tag))

    suspend fun getBlockTransactionCount(ref: BlockReference = latest): ApiResult<HexULong>
    suspend fun getBlockTransactionCounts(numbers: ULongRange) = numbers.map { getBlockTransactionCount(it.ref) }
    suspend fun getBlockTransactionCountByHash(hash: Hash): ApiResult<HexULong> = getBlockTransactionCount(hash.ref)
    suspend fun getBlockTransactionCountByNumber(number: ULong): ApiResult<HexULong> = getBlockTransactionCount(number.ref)
    suspend fun getBlockTransactionCountByNumber(tag: String): ApiResult<HexULong> = getBlockTransactionCount(BlockReference.fromTag(tag))

    suspend fun getTransactionCount(address: Address, ref: BlockReference = latest): ApiResult<HexULong>

    suspend fun getRawTransactionByHash(hash: Hash): ApiResult<HexData?>
    suspend fun getRawTransactionByBlockHashAndIndex(blockHash: Hash, index: Int): ApiResult<HexData?>
    suspend fun getRawTransactionByBlockNumberAndIndex(blockNumber: ULong, index: Int): ApiResult<HexData?>

    suspend fun getTransactionByHash(hash: Hash): ApiResult<Transaction?>
    suspend fun getTransactionByBlock(ref: BlockReference, index: Int): ApiResult<Transaction?>
    suspend fun getTransactionsByBlock(ref: BlockReference, indexes: IntRange): List<ApiResult<Transaction?>> = indexes.map { getTransactionByBlock(ref, it) }
    suspend fun getTransactionByBlockHashAndIndex(blockHash: Hash, index: Int) = getTransactionByBlock(blockHash.ref, index)
    suspend fun getTransactionByBlockNumberAndIndex(blockNumber: ULong, index: Int) = getTransactionByBlock(blockNumber.ref, index)
    suspend fun getTransactionByBlockNumberAndIndex(tag: String, index: Int) = getTransactionByBlock(BlockReference.fromTag(tag), index)

    suspend fun getTransactionReceipt(hash: Hash): ApiResult<TransactionReceipt?>

    suspend fun getUncleByBlockHashAndIndex(blockHash: Hash, index: Int): ApiResult<UncleBlock?>
    suspend fun getUncleByBlockNumberAndIndex(blockNumber: ULong, index: Int): ApiResult<UncleBlock?>

    suspend fun getStorageAt(address: Address, key: HexString, ref: BlockReference = latest): ApiResult<HexData?>
    suspend fun getProof(address: Address, storageKeys: List<HexData>, ref: BlockReference = latest): ApiResult<AccountProof?>

    suspend fun newFilter(request: GetLogsRequest): ApiResult<String>
    suspend fun newFilter(init: GetLogsRequest.() -> Unit): ApiResult<String> = newFilter(GetLogsRequest().apply(init))
    suspend fun uninstallFilter(filterId: String): ApiResult<Boolean>
    suspend fun getFilterLogs(filterId: String): ApiResult<List<Log>>
    suspend fun <T> getFilterChanges(filterId: FilterId<T>): ApiResult<List<T>>

    suspend fun getLogs(request: GetLogsRequest): ApiResult<List<Log>>
    suspend fun getBalance(address: Address, ref: BlockReference = latest): ApiResult<HexBigInt?>
    suspend fun getCode(address: Address, ref: BlockReference = latest): ApiResult<HexData?>

    suspend fun call(request: CallRequest, ref: BlockReference = latest): ApiResult<HexData?>
    suspend fun estimateGas(request: CallRequest): ApiResult<HexBigInt>
    suspend fun sendRawTransaction(signedTransactionData: String): ApiResult<Hash>
    suspend fun sendTransaction(
        account: PrivateAccount,
        build: suspend TransactionBuilder.() -> Unit
    ): ApiResult<Hash>

    suspend fun getLogs(init: GetLogsRequest.() -> Unit): ApiResult<List<Log>> = getLogs(GetLogsRequest().apply(init))

    suspend fun <T : ContractEvent> getLogs(
        event: ContractEventFactory<T>,
        init: GetLogsRequest.() -> Unit
    ): List<Pair<T, Log>> = getLogs(GetLogsRequest(topics = Topics().filterByEvent(event)).apply(init))
        .awaitOrThrow()
        .mapNotNull { log -> event.decodeIf(log)?.let { e -> e to log } }

    suspend fun call(
        ref: BlockReference = latest,
        init: CallRequest.() -> Unit
    ): ApiResult<HexData?> = call(CallRequest().apply(init), ref)
}
// @formatter:on