package com.github.jyc228.kotlin.codegen

class GenerationContext(private val toFullName: (String) -> String) {
    val reportedTypes = mutableSetOf<String>()

    fun reportType(type: String) {
        if (type.isBlank()) return
        reportedTypes += type
    }

    fun fullPaths() = reportedTypes.asSequence().map(toFullName).sorted().filter { it.isNotBlank() }

    companion object
}