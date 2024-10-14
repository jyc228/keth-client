package com.github.jyc228.keth.client.eth

import kotlinx.serialization.KSerializer

class SerializerConfig(
    val blockHeader: KSerializer<BlockHeader>,
    val transaction: KSerializer<Transaction>,
    val blockWithTxHashes: KSerializer<Block<TransactionHashes>>,
    val blockWithTxObjects: KSerializer<Block<TransactionObjects>>
) {
    fun <T : Transactions> getBlockSerializer(option: GetBlockOption<T>) = when (option.fullTx) {
        true -> blockWithTxObjects
        false -> blockWithTxHashes
    } as KSerializer<Block<T>>
}