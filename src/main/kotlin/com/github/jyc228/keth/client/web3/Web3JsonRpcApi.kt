package com.github.jyc228.keth.client.web3

import com.github.jyc228.keth.client.AbstractJsonRpcApi
import com.github.jyc228.keth.client.ApiResult
import com.github.jyc228.keth.client.JsonRpcClientWrapper
import com.github.jyc228.keth.type.HexData

class Web3JsonRpcApi(client: JsonRpcClientWrapper) : Web3Api, AbstractJsonRpcApi(client) {
    override suspend fun clientVersion(): ApiResult<String> = "web3_clientVersion"()
    override suspend fun sha3(data: String): ApiResult<HexData> = "web3_sha3"(data)
}
