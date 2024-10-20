package com.github.jyc228.keth

import com.github.jyc228.keth.type.Address

data class PrivateAccount(
    val address: Address,
    val privateKey: String
)
