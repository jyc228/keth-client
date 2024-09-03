package com.github.jyc228.keth.client.eth

import com.github.jyc228.keth.type.Log
import com.github.jyc228.keth.type.RpcBlock
import com.github.jyc228.keth.type.TransactionHashes
import com.github.jyc228.keth.type.TransactionObjects
import com.github.jyc228.keth.type.Transactions
import kotlinx.serialization.KSerializer
import kotlinx.serialization.serializer

class GetBlockOption<T : Transactions> internal constructor(
    val serializer: KSerializer<RpcBlock<T>?>,
    val fullTx: Boolean = false
)

val txHash = GetBlockOption<TransactionHashes>(serializer(), false)
val txObject = GetBlockOption<TransactionObjects>(serializer(), true)

class FilterId<T> internal constructor(
    val id: String,
    val serializer: KSerializer<List<T>>
) {
    companion object {
        fun log(filterId: String): FilterId<Log> = FilterId(filterId, serializer())
    }
}
