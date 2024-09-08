package com.github.jyc228.kotlin.codegen

class ConstructorBuilder(val indent: Indent, val context: GenerationContext) {
    private val parameters = mutableListOf<PropertyBuilder>()

    fun parameter(name: String) = PropertyBuilder(name, indent, context).also { parameters += it }

    fun build(): String = buildList {
        if (parameters.sumOf { it.length } >= 80 || parameters.size >= 3) {
            add("(")
            add("\n")
            add(parameters.joinToString(",\n") { b -> b.build().joinToString("\n") { "${indent.next}${it}" } })
            add("\n")
            add("$indent)")
        } else {
            add("(")
            add(parameters.joinToString(", ") { it.build().joinToString(" ") })
            add(")")
        }
    }.joinToString("")
}
