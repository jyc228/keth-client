package com.github.jyc228.kotlin.codegen

import org.intellij.lang.annotations.Language

class FunctionBuilder(val name: String, val indent: Indent, val context: GenerationContext) {
    private val tokens = mutableListOf<String>()
    private val parameters = mutableListOf<String>()
    private var returnType = ""
    private var body = ""

    fun override() = apply { tokens += "override" }

    fun suspend() = apply { tokens += "suspend" }

    fun parameters(params: List<Pair<String, String>>? = null) =
        apply { params?.forEach { parameter(it.first, it.second) } }

    fun parameter(name: String, type: String) = apply {
        parameters += "$name: $type"
        context.reportType(type)
    }

    fun returnType(type: String?, typeParameter: List<String> = emptyList()) = apply {
        if (type.isNullOrBlank()) return@apply
        returnType = ": $type"
        if (typeParameter.isNotEmpty()) {
            returnType += "<${typeParameter.joinToString(", ")}>"
        }
        context.reportType(type)
        typeParameter.forEach { context.reportType(it) }
    }

    fun body(@Language("kotlin") code: String) = apply {
        body = " {\n${indent.next}$code\n$indent}"
    }

    fun expressionBody(@Language("kotlin") code: String) = apply { body = code }

    fun build() = buildString {
        if (tokens.isNotEmpty()) append(tokens.joinToString(" ")).append(" ")
        append("fun $name(${buildParameter()})$returnType$body")
    }

    private fun buildParameter(): String {
        if (parameters.size == 1) {
            return parameters[0]
        }
        if (tokens.sumOf { it.length } + parameters.sumOf { it.length } + returnType.length >= 80 || parameters.size >= 3) {
            return parameters.joinToString(
                prefix = "\n",
                separator = ",\n",
                postfix = "\n$indent"
            ) { "${indent.next}$it" }
        }
        return parameters.joinToString(", ")
    }
}
