package com.github.jyc228.keth.client.net

import com.github.jyc228.keth.client.ApiResult
import com.github.jyc228.keth.type.HexData

interface NetApi {
    suspend fun version(): ApiResult<Int>
    suspend fun listening(): ApiResult<Boolean>
    suspend fun peerCount(): ApiResult<HexData>
}