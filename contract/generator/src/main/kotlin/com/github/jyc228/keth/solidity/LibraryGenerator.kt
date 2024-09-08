package com.github.jyc228.keth.solidity

import com.github.jyc228.kotlin.codegen.GenerationContext
import com.github.jyc228.kotlin.codegen.KtFileBuilder
import com.github.jyc228.kotlin.codegen.TypeBuilder

class LibraryGenerator(
    val packagePath: String,
    val abiIOByName: MutableMap<String, AbiComponent> = mutableMapOf()
) : SolidityCodeGen() {
    val generated = mutableSetOf<String>()
    fun generate(fileName: String, objectName: String?): KtFileBuilder {
        return KtFileBuilder(
            GenerationContext { it.importPackagePath },
            fileName,
            packagePath
        ).apply {
            when (objectName.isNullOrBlank()) {
                true -> generateStruct(objectName ?: "", context, ::type)
                false -> type().`object`(objectName).body { generateStruct(objectName, context, ::type) }
            }
        }
    }

    private fun generateStruct(objectName: String, context: GenerationContext, type: () -> TypeBuilder) {
        while (abiIOByName.isNotEmpty()) {
            abiIOByName.toList().forEach { (typeName, io) ->
                generated += typeName
                abiIOByName -= typeName
                type().dataClass(typeName).constructor {
                    io.components.forEach { item ->
                        if (item.type == "tuple" || item.type == "tuple[]") {
                            val struct = item.resolveStruct()
                            if (struct.ownerName == objectName) {
                                parameter(item.resolveName()).immutable().type(struct.name)
                                if (struct.name !in generated) abiIOByName[struct.name] = item
                            } else {
                                TODO()
                            }
                            context.reportType("$packagePath.${struct.name}")
                        } else {
                            parameter(item.resolveName()).immutable().type(item.typeToKotlin)
                        }
                    }
                }
            }
        }
    }
}
