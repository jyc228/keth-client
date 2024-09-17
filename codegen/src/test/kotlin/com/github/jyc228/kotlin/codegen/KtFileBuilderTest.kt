package com.github.jyc228.kotlin.codegen

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import org.intellij.lang.annotations.Language

class KtFileBuilderTest : StringSpec({
    fun String.verifySourceCode(@Language("kotlin") code: String) {
        this.trim() shouldBe code.trimIndent()
    }

    "generate file content" {
        val builder = KtFileBuilder(GenerationContext { it }, "hello", "com.github.io")
        builder.build().verifySourceCode("""package com.github.io""")
    }

    "generate file content with type" {
        val builder = KtFileBuilder(GenerationContext { it }, "Hello", "com.github.io")
        builder.type().`class`("Hello").body {
            function("world").body("""println("hello world!")""")
        }
        builder.build().verifySourceCode(
            """
            package com.github.io
            
            class Hello {
                fun world() {
                    println("hello world!")
                }
            }
        """
        )
    }
})
