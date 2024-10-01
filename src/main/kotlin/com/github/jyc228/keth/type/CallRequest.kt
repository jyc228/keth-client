package com.github.jyc228.keth.type

import kotlinx.serialization.Serializable

@Serializable
data class CallRequest(
    var from: Address? = null,
    var to: Address? = null,
    var gas: HexBigInt? = null,
    var gasPrice: HexBigInt? = null,
    var value: HexBigInt? = null,
    var data: String? = null
)
