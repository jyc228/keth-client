package com.github.jyc228.keth.type

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class JsonTest : StringSpec({
    "GetLogsRequest to json" {
        val address = Address.fromHexString("0x1")
        Json.encodeToString(GetLogsRequest(address = mutableSetOf(address))) shouldBe """{"address":["0x1"]}"""

        Json.encodeToString(
            GetLogsRequest(
                topics = Topics().filterByAddress(
                    1,
                    address
                )
            )
        ) shouldBe """{"topics":[null,["0x0000000000000000000000000000000000000000000000000000000000000001"]]}"""
    }
})