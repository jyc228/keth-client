package com.github.jyc228.kotlin.codegen

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import org.intellij.lang.annotations.Language

class CodegenTest : StringSpec({
    fun String.verifySourceCode(@Language("kotlin") code: String) {
        this shouldBe code.trimIndent()
    }

    fun typeBuilder() = TypeBuilder(Indent(0), GenerationContext { it })

    "interface generic" {
        val r = typeBuilder()
            .`interface`("ContractEventFactory")
            .generic { parameter("Event") }

        r.build().verifySourceCode("""interface ContractEventFactory<Event>""")
    }

    "interface generic with upperbound" {
        val r = typeBuilder()
            .`interface`("ContractEventFactory")
            .generic { parameter("Event", "ContractEvent") }

        r.build().verifySourceCode("""interface ContractEventFactory<Event : ContractEvent>""")
    }

    "interface inherit" {
        val r = typeBuilder()
            .`interface`("ContractEventFactory")
            .inherit { `interface`("Factory").typeParameter("String") }

        r.build().verifySourceCode("""interface ContractEventFactory : Factory<String>""")
    }

    "interface inherit many" {
        val r = typeBuilder()
            .`interface`("ContractEventFactory")
            .inherit {
                `interface`("Factory").typeParameter("String")
                `interface`("Closeable")
                `interface`("Event")
                `interface`("Test").typeParameter("Hello")
            }

        r.build().verifySourceCode(
            """
            interface ContractEventFactory : Factory<String>,
                Closeable,
                Event,
                Test<Hello>
        """
        )
    }
})
