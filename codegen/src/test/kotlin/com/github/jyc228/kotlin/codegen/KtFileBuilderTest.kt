package com.github.jyc228.kotlin.codegen

import io.kotest.core.spec.style.FunSpec

class KtFileBuilderTest : FunSpec({

    context("test") {
        val builder = KtFileBuilder(GenerationContext { it }, "hello", "com.github.io")
    }
})
