package com.github.jyc228.keth.client.eth

import com.github.jyc228.keth.contract.ContractEventFactory
import com.github.jyc228.keth.type.Address
import com.github.jyc228.keth.type.Hash
import com.github.jyc228.keth.type.HexData
import com.github.jyc228.keth.type.HexInt
import com.github.jyc228.keth.type.HexULong
import java.math.BigInteger
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
    var address: MutableSet<Address> = mutableSetOf(),
    var topics: Topics = Topics()
)

@Serializable
@JvmInline
value class Topics(val list: MutableList<MutableList<HexData>?> = MutableList(1) { null }) {
    fun filterByEvent(e: ContractEventFactory<*>) = apply { insert(0, e.eventSig.hex) }
    fun filterByAddress(index: Int, vararg address: Address) = apply { address.forEach { insert(index, it.hex) } }

    @OptIn(ExperimentalStdlibApi::class)
    fun filterByByteArray(index: Int, vararg bytes: ByteArray) =
        apply { bytes.forEach { insert(index, it.toHexString()) } }

    fun filterByBigInteger(index: Int, vararg bigInt: BigInteger) =
        apply { bigInt.forEach { insert(index, it.toString(16)) } }

    private fun insert(index: Int, hexString: String) = apply {
        val hex = HexData.fromHexString("0x${hexString.padStart(64, '0')}")
        if (list.lastIndex < index) {
            repeat(index - list.lastIndex) {
                list += null
            }
        }
        if (list[index]?.add(hex) == null) {
            list[index] = mutableListOf(hex)
        }
    }
}
