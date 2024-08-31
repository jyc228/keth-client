package com.github.jyc228.jsonrpc

interface JsonRpcClient {
    suspend fun send(request: JsonRpcRequest): JsonRpcResponse
    suspend fun sendBatch(requests: List<JsonRpcRequest>): List<JsonRpcResponse>

    companion object {
        fun from(url: String, jwtSecret: String? = null): JsonRpcClient {
            if (url.startsWith("http")) return JsonRpcKtorHttpClient(url, jwtSecret)
            if (url.startsWith("ws")) return JsonRpcKtorWebSocketClient(url)
            error("unsupported protocol $url")
        }
    }
}
