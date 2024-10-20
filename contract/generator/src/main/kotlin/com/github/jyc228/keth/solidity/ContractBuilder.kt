package com.github.jyc228.keth.solidity

import com.github.jyc228.keth.solidity.compile.CompileResult
import com.github.jyc228.kotlin.codegen.BodyBuilder
import com.github.jyc228.kotlin.codegen.GenerationContext
import com.github.jyc228.kotlin.codegen.KtFileBuilder
import com.github.jyc228.kotlin.codegen.TypeBuilder
import java.util.Locale
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class ContractBuilder(
    val packagePath: String,
    val compileResult: CompileResult,
) {
    fun buildInterface() = KtFileBuilder(
        context = GenerationContext.new(),
        name = compileResult.contractName,
        packagePath = packagePath
    ).apply { type().buildContractInterface(compileResult.contractName) }

    private fun TypeBuilder.buildContractInterface(interfaceName: String) = `interface`(interfaceName)
        .inherit { `interface`("Contract").typeParameter("$interfaceName.Event") }
        .body {
            compileResult.functions().forEach { item -> addFunction(item) }
            compileResult.internalStructures().forEach { addStruct(it, interfaceName) }
            type().sealedInterface("Event").inherit { `interface`("ContractEvent") }
            compileResult.events().forEach { item -> addEvent(item) }

            companionObject()
                .inherit {
                    `class`("Contract.Factory")
                        .typeParameter(interfaceName)
                        .invokeConstructor("::${interfaceName}Impl")
                }
                .body {
                    function("bin")
                        .returnType("String")
                        .body("return \"${compileResult.bin.trim()}\"")

                    var code = "return \"0x\" + bin()"
                    compileResult.constructor()?.let { item ->
                        code += " + encodeParameters(\n${indent.next.next}${item.toJsonStringTemplate()},\n${indent.next.next}${item.inputs.joinToStringNames()}\n${indent.next})"
                    }
                    function("encodeDeploymentCallData")
                        .parameters(compileResult.constructor()?.inputs?.toParameters())
                        .body(code)
                        .returnType("String")
                    compileResult.functions().forEach { item -> addFunctionMetadata(item, interfaceName) }
                }
        }

    private fun BodyBuilder.addFunction(item: AbiItem) {
        function(item.name!!)
            .parameters(item.inputs.toParameters())
            .returnType("ContractFunctionRequest", listOf(item.outputToKotlinType() ?: "Unit"))
        context.reportType("Contract")
    }

    private fun BodyBuilder.addStruct(item: AbiComponent, interfaceName: String) {
        val struct = item.resolveStruct()
        type().dataClass(struct.name).constructor {
            item.components.forEach { output ->
                parameter(output.resolveName())
                    .immutable()
                    .type(output.typeToKotlin)
            }
        }
        context.reportType("$packagePath.$interfaceName.${struct.name}")
    }

    private fun BodyBuilder.addFunctionMetadata(item: AbiItem, interfaceName: String) {
        property(resolveMetadataPropertyName(item))
            .immutable()
            .defaultValue("ContractFunctionP${item.inputs.size}") {
                parameter("$interfaceName::${item.name}")
                stringParameter("0x${item.computeSig()}")
                parameter("{ ${item.toJsonStringTemplate()} }")
            }
    }

    private fun BodyBuilder.addEvent(event: AbiItem) {
        val eventClass = when (event.inputs.isEmpty()) {
            true -> type().`class`(event.name!!)
            false -> type().dataClass(event.name!!)
        }
        eventClass
            .constructor {
                event.inputs.forEach { input ->
                    parameter(input.name)
                        .immutable()
                        .type(input.typeToKotlin)
                }
            }
            .inherit { `interface`("Event") }
            .body {
                val indexedInputs = event.inputs.filter { it.indexed == true }
                companionObject().inherit {
                    `class`("ContractEventFactory")
                        .typeParameter(event.name!!)
                        .invokeConstructor(
                            "${event.name}::class",
                            "\"0x${event.computeSig()}\"",
                            "{ ${event.toJsonStringTemplate()} }"
                        )
                }.body {
                    indexedInputs.forEachIndexed { index, input ->
                        function("Topics.filterBy${input.name.replaceFirstChar { it.titlecase(Locale.getDefault()) }}")
                            .parameter("vararg ${input.name}", input.typeToKotlin)
                            .expressionBody(" = apply { filterBy${input.typeToKotlin}(${index + 1}, *${input.name}) }")
                        context.reportType("Topics")
                    }
                }
            }
    }

    fun buildDefaultImplementation() = KtFileBuilder(
        context = GenerationContext.new(),
        name = "${compileResult.contractName}Impl",
        packagePath = packagePath
    ).apply { type().buildContractInterfaceImplementation(compileResult.contractName) }

    private fun TypeBuilder.buildContractInterfaceImplementation(interfaceName: String) =
        `class`("${interfaceName}Impl")
            .constructor {
                parameter("address").type("Address")
                parameter("api").type("EthApi")
            }
            .inherit {
                `interface`(interfaceName)
                `class`("AbstractContract")
                    .typeParameter("$interfaceName.Event")
                    .invokeConstructor("address", "api")
            }
            .body {
                compileResult.functions().forEach { item ->
                    val functionReference = "$interfaceName.${resolveMetadataPropertyName(item)}"
                    function(item.name!!)
                        .override()
                        .parameters(item.inputs.toParameters())
                        .returnType(
                            "ContractFunctionRequest",
                            when (item.outputs.size > 3) {
                                true -> listOf("$interfaceName.${item.outputToKotlinType()}")
                                false -> listOf(item.outputToKotlinType() ?: "Unit")
                            }
                        )
                        .body(
                            """
                            return newRequest(
                                        $functionReference::decodeResult,
                                        $functionReference.encodeFunctionCall(${item.inputs.joinToStringNames()}),
                                    )
                        """.trimIndent()
                        )
                    context.reportType("Contract")
                }
            }

    private fun resolveMetadataPropertyName(item: AbiItem): String {
        if (item.name!! in compileResult.overloadingFunctions) {
            return when (item.inputs.isEmpty()) {
                true -> item.name!!
                false -> "${item.name}_${item.inputs.joinToString("_") { it.type.replace("[]", "Array") }}"
            }
        }
        return item.name!!
    }

    private fun List<AbiInput>.toParameters() =
        mapIndexed { i, input -> input.name.ifBlank { "key$i" } to input.typeToKotlin }

    private fun List<AbiInput>.joinToStringNames() =
        withIndex().joinToString(", ") { (idx, input) -> input.name.ifBlank { "key$idx" } }

    private fun AbiItem.toJsonStringTemplate() = "\"\"\"${Json.encodeToString(this)}\"\"\""
}
