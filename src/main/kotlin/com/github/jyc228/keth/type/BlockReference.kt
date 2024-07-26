package com.github.jyc228.keth.type

import kotlinx.serialization.Serializable

@JvmInline
@Serializable
value class BlockReference private constructor(val value: String) {
    constructor(number: ULong) : this("0x${number.toString(16)}")
    constructor(number: Int) : this("0x${number.toString(16)}")

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