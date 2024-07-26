package com.github.jyc228.keth.type

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class JsonTest : StringSpec({
    "GetLogsRequest to json" {
        val address = Address.fromHexString("0x1")
        Json.encodeToString(GetLogsRequest(address = address)) shouldBe """{"address":"0x1"}"""
    }
})