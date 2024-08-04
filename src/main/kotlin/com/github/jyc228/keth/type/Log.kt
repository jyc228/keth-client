package com.github.jyc228.keth.type

import com.github.jyc228.keth.contract.ContractEventFactory
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
    fun filterByEvent(e: ContractEventFactory<*>) = apply { insert(0, e.hash.hex) }
    fun filterByAddress(index: Int, vararg address: Address) = apply { address.forEach { insert(index, it.hex) } }

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
