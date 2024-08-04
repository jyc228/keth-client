package com.github.jyc228.keth.client.eth

import com.github.jyc228.keth.client.ApiResult
import com.github.jyc228.keth.contract.ContractEvent
import com.github.jyc228.keth.contract.ContractEventFactory
import com.github.jyc228.keth.type.Address
import com.github.jyc228.keth.type.Block
import com.github.jyc228.keth.type.BlockHeader
import com.github.jyc228.keth.type.BlockReference
import com.github.jyc228.keth.type.CallRequest
import com.github.jyc228.keth.type.GetLogsRequest
import com.github.jyc228.keth.type.Hash
import com.github.jyc228.keth.type.HexBigInt
import com.github.jyc228.keth.type.HexData
import com.github.jyc228.keth.type.HexString
import com.github.jyc228.keth.type.HexULong
import com.github.jyc228.keth.type.Log
import com.github.jyc228.keth.type.Topics
import com.github.jyc228.keth.type.Transaction
import com.github.jyc228.keth.type.TransactionHashes
import com.github.jyc228.keth.type.TransactionObjects
import com.github.jyc228.keth.type.TransactionReceipt
import com.github.jyc228.keth.type.UncleBlock

@Suppress("UNCHECKED_CAST")
interface EthApi {
    suspend fun chainId(): ApiResult<HexULong>
    suspend fun gasPrice(): ApiResult<HexBigInt>
    suspend fun blockNumber(): ApiResult<HexULong>

    suspend fun getHeaderByHash(hash: Hash): ApiResult<out BlockHeader>
    suspend fun getHeaderByNumber(number: ULong): ApiResult<out BlockHeader>
    suspend fun getHeaderByNumber(tag: String): ApiResult<out BlockHeader>
    suspend fun getHeaderByNumber(ref: BlockReference = BlockReference.latest): ApiResult<out BlockHeader>
    suspend fun getHeaders(numbers: ULongProgression) = numbers.map { getHeaderByNumber(it) }

    suspend fun getBlockByHash(hash: Hash, fullTx: Boolean): ApiResult<out Block<*>?>
    suspend fun getBlockByNumber(number: ULong, fullTx: Boolean): ApiResult<out Block<*>?>
    suspend fun getBlockByNumber(tag: String, fullTx: Boolean): ApiResult<out Block<*>?>
    suspend fun getBlockByNumber(ref: BlockReference = BlockReference.latest, fullTx: Boolean): ApiResult<out Block<*>?>

    suspend fun getFullBlock(hash: Hash) = getBlockByHash(hash, true) as ApiResult<Block<TransactionObjects>?>
    suspend fun getFullBlock(number: ULong) = getFullBlock(BlockReference(number))
    suspend fun getFullBlock(tag: String) = getFullBlock(BlockReference.fromTag(tag))
    suspend fun getFullBlock(ref: BlockReference = BlockReference.latest) =
        getBlockByNumber(ref, true) as ApiResult<Block<TransactionObjects>?>

    suspend fun getFullBlocks(numbers: ULongProgression) = numbers.map { getFullBlock(it) }

    suspend fun getSimpleBlock(hash: Hash) = getBlockByHash(hash, false) as ApiResult<TransactionHashes?>
    suspend fun getSimpleBlock(number: ULong) = getSimpleBlock(BlockReference(number))
    suspend fun getSimpleBlock(tag: String) = getSimpleBlock(BlockReference.fromTag(tag))
    suspend fun getSimpleBlock(ref: BlockReference = BlockReference.latest) =
        getBlockByNumber(ref, false) as ApiResult<Block<TransactionHashes>?>

    suspend fun getSimpleBlocks(numbers: ULongProgression) = numbers.map { getSimpleBlock(it) }

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
