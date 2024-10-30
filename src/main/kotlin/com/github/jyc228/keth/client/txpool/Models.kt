package com.github.jyc228.keth.client.txpool

import com.github.jyc228.keth.client.eth.Transaction
import com.github.jyc228.keth.type.Address
import com.github.jyc228.keth.type.HexInt
import kotlinx.serialization.Serializable

@Serializable
data class TxpoolContent(
    val pending: Map<Address, Map<Int, Transaction>>,
    val queued: Map<Address, Map<Int, Transaction>>
) {
    fun keys() = pending.keys + queued.keys
}

@Serializable
data class TxpoolContentFrom(
    val pending: Map<Int, Transaction>,
    val queued: Map<Int, Transaction>
) {
    fun keys() = pending.keys + queued.keys
}

@Serializable
data class TxpoolInspect(
    val pending: Map<Address, Map<Int, String>>,
    val queued: Map<Address, Map<Int, String>>
)

@Serializable
data class TxpoolStatus(
    val pending: HexInt,
    val queued: HexInt
)
