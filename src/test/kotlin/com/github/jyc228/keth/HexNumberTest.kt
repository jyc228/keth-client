package com.github.jyc228.keth

import com.github.jyc228.keth.client.EthereumClient
import com.github.jyc228.keth.client.fromRpcUrl
import com.github.jyc228.keth.type.HexBigInt
import io.kotest.core.spec.style.FunSpec

class HexNumberTest : FunSpec({
    context("hi") {
        val a = HexBigInt(10.toBigInteger())
        val b = HexBigInt(12309229348.toBigInteger())
        val c = a + b
        println(c)
    }

    context("aa") {
        val client = EthereumClient.fromRpcUrl("https://rpc.ankr.com/eth")
    }
})
