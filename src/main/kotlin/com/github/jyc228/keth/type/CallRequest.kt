package com.github.jyc228.keth.type

import kotlinx.serialization.Serializable

@Serializable
data class CallRequest(
    var from: String? = null,
    var to: String? = null,
    var gas: HexBigInt? = null,
    var gasPrice: HexBigInt? = null,
    var value: HexBigInt? = null,
    var data: String? = null
)
