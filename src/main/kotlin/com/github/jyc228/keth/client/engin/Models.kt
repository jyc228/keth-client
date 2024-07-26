package com.github.jyc228.keth.client.engin

import com.github.jyc228.keth.type.Address
import com.github.jyc228.keth.type.Hash
import com.github.jyc228.keth.type.HexBigInt
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@JvmInline
@Serializable
value class PayloadId(
    // 8 bytes
    val bytes: ByteArray
)

@Serializable
class ExecutionPayload(
    val parentHash: Hash,
    val feeRecipient: Address,
    val stateRoot: Hash,
    val receiptsRoot: Hash,
    val logsBloom: String,
    val prevRandao: String,
    val blockNumber: ULong,
    val gasLimit: HexBigInt,
    val gasUsed: HexBigInt,
    val timestamp: Instant,
    val extraData: String,
    val baseFeePerGas: HexBigInt,
    val blockHash: Hash,
    val transactions: Array<ByteArray>,
)

@Serializable
data class ForkchoiceState(
    // block hash of the head of the canonical chain
    val headBlockHash: String,
    // safe block hash in the canonical chain
    val safeBlockHash: String,
    // block hash of the most recent finalized block
    val finalizedBlockHash: String,
)

@Serializable
data class ForkchoiceUpdatedResult(
    // the result of the payload execution
    val payloadStatus: PayloadStatusV1,
    // the payload id if requested
    val payloadId: PayloadId? = null
)

@Serializable
data class PayloadStatusV1(
    // the result of the payload execution
    val status: ExecutePayloadStatus,
    // the hash of the most recent valid block in the branch defined by payload and its ancestors (optional field)
    val latestValidHash: Hash,
    // additional details on the result (optional field)
    val validationError: String? = null,
)

@Serializable
enum class ExecutePayloadStatus {
    // given payload is valid
    VALID,

    // given payload is invalid
    INVALID,

    // sync process is in progress
    SYNCING,

    // returned if the payload is not fully validated, and does not extend the canonical chain,
    // but will be remembered for later (on reorgs or sync updates and such)
    ACCEPTED,

    // if the block-hash in the payload is not correct
    INVALID_BLOCK_HASH,

    // proof-of-stake transition only, not used in rollup
    INVALID_TERMINAL_BLOCK,
}

@Serializable
data class PayloadAttributes(
    // value for the timestamp field of the new payload
    val timestamp: ULong,
    // value for the random field of the new payload
    val prevRandao: ByteArray,
    // suggested value for the coinbase field of the new payload
    val suggestedFeeRecipient: String,
    // Transactions to force into the block (always at the start of the transactions list).
    val transactions: Array<ByteArray>,
    // NoTxPool to disable adding any transactions from the transaction-pool.
    val noTxPool: Boolean,
    // GasLimit override
    val gasLimit: ULong,
)
