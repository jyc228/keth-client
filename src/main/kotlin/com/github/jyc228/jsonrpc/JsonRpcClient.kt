package com.github.jyc228.jsonrpc


/**
 * Factory function to create an instance of JsonRpcClient. Depending on the url prefix, it operates as either http or websocket.
 *
 * @throws IllegalArgumentException If the URL protocol is not supported.
 */
fun JsonRpcClient(url: String, jwtSecret: String? = null): JsonRpcClient = when {
    url.startsWith("http") -> JsonRpcKtorHttpClient(url, jwtSecret)
    url.startsWith("ws") -> JsonRpcKtorWebSocketClient(url)
    else -> error("unsupported protocol $url")
}

// https://www.jsonrpc.org/
interface JsonRpcClient {
    suspend fun send(request: JsonRpcRequest): JsonRpcResponse
    suspend fun sendBatch(requests: List<JsonRpcRequest>): List<JsonRpcResponse>

    companion object
}
