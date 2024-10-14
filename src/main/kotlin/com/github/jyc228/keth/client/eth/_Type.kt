package com.github.jyc228.keth.client.eth

import com.github.jyc228.keth.type.Hash
import com.github.jyc228.keth.type.HexString
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer

@JvmInline
@Serializable
value class BlockReference private constructor(val value: String) {
    constructor(number: ULong) : this("0x${number.toString(16)}")
    constructor(number: Int) : this("0x${number.toString(16)}")

    val hash get() = value.length == 66 && value.startsWith("0x")

    companion object {
        val latest = BlockReference("latest")
        val safe = BlockReference("safe")
        val finalized = BlockReference("finalized")

        fun fromTag(tag: String) = when (tag.lowercase()) {
            latest.value -> latest
            safe.value -> safe
            finalized.value -> finalized
            else -> error("unknown tag $tag")
        }

        fun fromHex(hex: HexString) = BlockReference(hex.with0x)
    }
}

val Int.ref get() = BlockReference(this)
val ULong.ref get() = BlockReference(this)
val Hash.ref get() = BlockReference.fromHex(this)

@JvmInline
value class GetBlockOption<T : Transactions> internal constructor(val fullTx: Boolean = false)

val txHash = GetBlockOption<TransactionHashes>(false)
val txObject = GetBlockOption<TransactionObjects>(true)

class FilterId<T> internal constructor(
    val id: String,
    val serializer: KSerializer<List<T>>
) {
    companion object {
        fun log(filterId: String): FilterId<Log> = FilterId(filterId, serializer())
    }
}
