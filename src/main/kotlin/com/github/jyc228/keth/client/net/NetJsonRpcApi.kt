package com.github.jyc228.keth.client.net

import com.github.jyc228.keth.client.AbstractJsonRpcApi
import com.github.jyc228.keth.client.ApiResult
import com.github.jyc228.keth.client.JsonRpcClientWrapper
import com.github.jyc228.keth.type.HexData
import kotlinx.serialization.builtins.serializer

class NetJsonRpcApi(client: JsonRpcClientWrapper) : NetApi, AbstractJsonRpcApi(client) {
    override suspend fun version(): ApiResult<Int> = "net_version"(String.serializer()).map { it.toInt() }
    override suspend fun listening(): ApiResult<Boolean> = "net_listening"()
    override suspend fun peerCount(): ApiResult<HexData> = "net_peerCount"()
}
