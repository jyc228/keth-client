package com.github.jyc228.kotlin.codegen

class BodyBuilder(val indent: Indent, val context: GenerationContext) {
    private val properties = mutableListOf<PropertyBuilder>()
    private val functions = mutableListOf<FunctionBuilder>()
    private val types = mutableListOf<TypeBuilder>()
    private var companionBuilder: TypeBuilder? = null

    fun property(name: String) = PropertyBuilder(name, indent.prev, context).also { properties += it }
    fun function(name: String) = FunctionBuilder(name, indent, context).also { functions += it }
    fun type() = TypeBuilder(indent, context).also { types += it }
    fun companionObject() = TypeBuilder(indent, context).companionObject().also { companionBuilder = it }

    fun build(): String = buildString {
        appendLine("{")
        append(properties.flatMap { it.build() }.joinToString("\n") { "$indent$it" })
        if (properties.isNotEmpty()) appendLine()
        append(functions.joinToString("\n") { "$indent${it.build()}" })
        if (functions.isNotEmpty()) appendLine()
        append(types.joinToString("\n") { "$indent${it.build()}\n" })
        if (types.isNotEmpty()) appendLine()
        if (companionBuilder != null) appendLine("$indent${companionBuilder!!.build()}")
        if (last() != '\n') appendLine()
        append("${indent.prev}}")
    }
}
