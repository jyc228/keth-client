package com.github.jyc228.keth.type

import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.StringSpec
import kotlinx.serialization.json.Json

class TransactionJsonTest : StringSpec({
    "decode legacy transaction" {
        val tx = decodeJsonResource<RpcTransaction>("/transaction/tx_legacy.json")
        println(tx)
    }

    "decode dynamic fee transaction" {
        val tx = decodeJsonResource<RpcTransaction>("/transaction/tx_dynamic_fee.json")
        println(tx)
    }

    "decode blob transaction" {
        val tx = decodeJsonResource<RpcTransaction>("/transaction/tx_blob.json")
        println(tx)
    }

    "decode unknown transaction" {
        shouldThrowAny { decodeJsonResource<RpcTransaction>("/transaction/tx_unknown.json", Json) }
        val tx = decodeJsonResource<RpcTransaction>("/transaction/tx_unknown.json", Json { ignoreUnknownKeys = true })
        println(tx)
    }
})
