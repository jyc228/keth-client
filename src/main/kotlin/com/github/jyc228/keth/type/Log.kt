package com.github.jyc228.keth.type

import kotlinx.serialization.Serializable

@Serializable
data class Log(
    val removed: Boolean,
    val logIndex: HexInt,
    val transactionIndex: HexInt,
    val transactionHash: Hash,
    val blockHash: Hash,
    val blockNumber: HexULong,
    val address: Address,
    val data: HexData,
    val topics: List<HexData>
)

@Serializable
data class GetLogsRequest(
    var fromBlock: BlockReference? = null,
    var toBlock: BlockReference? = null,
    var address: String? = null,
    var topics: List<String?>? = null
)
