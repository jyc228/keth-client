package com.github.jyc228.keth.client.web3

import com.github.jyc228.keth.client.ApiResult
import com.github.jyc228.keth.type.HexData

interface Web3Api {
    suspend fun clientVersion(): ApiResult<String>
    suspend fun sha3(data: String): ApiResult<HexData>
}