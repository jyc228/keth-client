package com.github.jyc228.jsonrpc

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpMessageBuilder
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import java.util.Date

// https://www.jsonrpc.org/
internal class JsonRpcKtorHttpClient(
    private val http: HttpClient,
    private val algorithm: Algorithm? = null
) : JsonRpcClient {
    constructor(url: String, jwtSecret: String? = null) : this(httpClient(url), hmac256(jwtSecret))

    override suspend fun send(request: JsonRpcRequest): JsonRpcResponse {
        return http.post {
            jwtAuth()
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    override suspend fun sendBatch(requests: List<JsonRpcRequest>): List<JsonRpcResponse> {
        return http.post {
            jwtAuth()
            contentType(ContentType.Application.Json)
            setBody(requests)
        }.body<List<JsonRpcResponse>>()
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