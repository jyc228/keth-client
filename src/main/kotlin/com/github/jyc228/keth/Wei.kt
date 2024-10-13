package com.github.jyc228.keth

import java.lang.ref.WeakReference
import java.math.BigInteger
import java.math.RoundingMode
import java.util.WeakHashMap

val Int.eth get() = wei(this) * Wei.ONE_ETH
val Int.wei get() = wei(this)
val Long.wei get() = wei(this)
val BigInteger.wei get() = wei(this)

@JvmInline
value class Wei(val v: BigInteger) {
    operator fun plus(wei: Wei) = Wei(v + wei.v)
    operator fun minus(wei: Wei) = Wei(v - wei.v)
    operator fun times(wei: Wei) = Wei(v * wei.v)
    operator fun times(value: Int) = Wei(v * value.toBigInteger())
    operator fun times(value: Double) = Wei((v.toBigDecimal() * value.toBigDecimal()).toBigInteger())
    operator fun div(wei: Wei) = Wei(v / wei.v)
    operator fun compareTo(other: Wei) = v.compareTo(other.v)
    override fun toString(): String {
        if (v >= ONE_ETH.v) return "${toString(ONE_ETH, 18)}.eth"
        if (v >= ONE_GWEI.v) return "${toString(ONE_GWEI, 9)}.gwei"
        return "$v.wei"
    }

    private fun toString(divisor: Wei, scale: Int): String {
        return v.toBigDecimal().divide(divisor.v.toBigDecimal(), scale, RoundingMode.UP)
            .stripTrailingZeros()
            .toPlainString()
    }

    companion object {
        val ONE_GWEI = Wei(1000000000.toBigInteger())
        val ONE_ETH = Wei(1000000000000000000.toBigInteger())
    }
}

private val cache = WeakHashMap<String, WeakReference<Wei>>()
private fun wei(v: Any): Wei {
    val stringV = v.toString()
    while (true) return cache.getOrPut(stringV) { WeakReference(Wei(stringV.toBigInteger())) }.get() ?: continue
}
