package com.github.jyc228.keth

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class WeiTest : StringSpec({
    "toString" {
        1.eth.toString() shouldBe "1.eth"
        (1.eth / 2.wei).toString() shouldBe "500000000.gwei"
    }
})
