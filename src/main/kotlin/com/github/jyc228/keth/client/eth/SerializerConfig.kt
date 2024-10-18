package com.github.jyc228.keth.client.eth

import kotlinx.serialization.KSerializer

class SerializerConfig(
    val blockHeader: KSerializer<BlockHeader>,
    val transaction: KSerializer<Transaction>,
    val blockWithTxHashes: KSerializer<Block<Block.TransactionHash>>,
    val blockWithTxObjects: KSerializer<Block<Block.TransactionObject>>
) {
    fun <E : Block.TransactionElement> getBlockSerializer(option: GetBlockOption<E>) = when (option.fullTx) {
        true -> blockWithTxObjects
        false -> blockWithTxHashes
    } as KSerializer<Block<E>>
}