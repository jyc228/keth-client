package com.github.jyc228.keth.client.eth

import com.github.jyc228.keth.type.Address
import com.github.jyc228.keth.type.Hash
import com.github.jyc228.keth.type.HexBigInt
import com.github.jyc228.keth.type.HexData
import com.github.jyc228.keth.type.HexULong
import kotlinx.serialization.Serializable

@Serializable
data class AccountProof(
    val address: Address,
    val accountProof: List<HexData>,
    val balance: HexBigInt,
    val codeHash: Hash,
    val nonce: HexULong,
    val storageHash: Hash,
    val storageProof: List<StorageProof>,
)

@Serializable
data class StorageProof(
    val key: HexData,
    val value: HexBigInt,
    val proof: List<HexData>
)