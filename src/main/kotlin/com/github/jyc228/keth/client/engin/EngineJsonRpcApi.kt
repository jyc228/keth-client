package com.github.jyc228.keth.client.engin

import com.github.jyc228.keth.client.AbstractJsonRpcApi
import com.github.jyc228.keth.client.ApiResult
import com.github.jyc228.keth.client.JsonRpcClient

class EngineJsonRpcApi(client: JsonRpcClient) : EngineApi, AbstractJsonRpcApi(client) {
    override suspend fun getPayloadV1(payloadId: PayloadId): ApiResult<ExecutionPayload> =
        "engine_getPayloadV1"(payloadId)

    override suspend fun newPayloadV1(payload: ExecutionPayload): ApiResult<PayloadStatusV1> =
        "engine_newPayloadV1"(payload)

    override suspend fun forkchoiceUpdatedV1(
        state: ForkchoiceState,
        attr: PayloadAttributes?
    ): ApiResult<ForkchoiceUpdatedResult> = "engine_forkchoiceUpdatedV1"(state, attr)
}
