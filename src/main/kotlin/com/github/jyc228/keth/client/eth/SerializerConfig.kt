package com.github.jyc228.keth.client.eth

import kotlinx.serialization.KSerializer

class SerializerConfig(
    val blockHeader: KSerializer<out BlockHeader>,
    val transaction: KSerializer<out Transaction>,
    val blockWithTxHashes: KSerializer<out Block<Block.TransactionHash>>,
    val blockWithTxObjects: KSerializer<out Block<Block.TransactionObject>>
) {
    @Suppress("UNCHECKED_CAST")
    fun <E : Block.TransactionElement> getBlockSerializer(option: GetBlockOption<E>): KSerializer<out Block<E>> {
        return when (option.fullTx) {
            true -> blockWithTxObjects
            false -> blockWithTxHashes
        } as KSerializer<Block<E>>
    }
}