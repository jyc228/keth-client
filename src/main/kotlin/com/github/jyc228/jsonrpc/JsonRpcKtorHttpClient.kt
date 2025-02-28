package com.github.jyc228.jsonrpc

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.CoroutineScope
import java.util.*

internal class JsonRpcKtorHttpClient(
    private val http: HttpClient,
    private val algorithm: Algorithm? = null
) : JsonRpcClient {
    constructor(url: String, jwtSecret: String? = null) : this(httpClient(url), hmac256(jwtSecret))

    override val coroutineScope: CoroutineScope = http

    override suspend fun send(request: JsonRpcRequest): JsonRpcResponse {
        return http.post {
            jwtAuth()
            contentType(ContentType.Application.Json)
            setBody(request)
        }.decodeOrThrow()
    }

    override suspend fun sendBatch(requests: List<JsonRpcRequest>): List<JsonRpcResponse> {
        return http.post {
            jwtAuth()
            contentType(ContentType.Application.Json)
            setBody(requests)
        }.decodeOrThrow<List<JsonRpcResponse>>()
    }

    private suspend inline fun <reified T> HttpResponse.decodeOrThrow(): T {
        if (status.isSuccess()) return body()
        throw JsonRpcSendException("status: ${status.value}, body: ${bodyAsText()}")
    }

    private fun HttpMessageBuilder.jwtAuth() {
        val jwt = jwt() ?: return
        headers { bearerAuth(jwt) }
    }

    private fun jwt(): String? {
        if (algorithm == null) return null
        return JWT.create()
            .withClaim("iat", Date().time / 1000)
            .sign(algorithm)
    }

    companion object {
        private fun httpClient(url: String) = HttpClient(CIO) {
            defaultRequest { url(url) }
            install(ContentNegotiation) { json() }
        }

        private fun hmac256(jwtSecret: String?): Algorithm? {
            if (jwtSecret == null) return null
            return when (jwtSecret.length % 2 == 0) {
                true -> jwtSecret
                false -> "0$jwtSecret"
            }.let { Algorithm.HMAC256(it.toBigInteger(16).toByteArray()) }
        }
    }
}