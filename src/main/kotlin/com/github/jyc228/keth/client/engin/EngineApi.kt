package com.github.jyc228.keth.client.engin

import com.github.jyc228.keth.client.ApiResult

interface EngineApi {
    suspend fun getPayloadV1(payloadId: PayloadId): ApiResult<ExecutionPayload>
    suspend fun newPayloadV1(payload: ExecutionPayload): ApiResult<PayloadStatusV1>
    suspend fun forkchoiceUpdatedV1(
        state: ForkchoiceState,
        attr: PayloadAttributes? = null
    ): ApiResult<ForkchoiceUpdatedResult>
}
