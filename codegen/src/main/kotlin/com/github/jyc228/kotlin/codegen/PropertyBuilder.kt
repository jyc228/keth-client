package com.github.jyc228.kotlin.codegen

class PropertyBuilder(val name: String, val indent: Indent, val context: GenerationContext) {
    private val annotations = mutableListOf<String>()
    private var modifier = ""
    private var type = ""
    private var defaultValue = ""
    private var default: StatementBuilder? = null

    val length get() = name.length + type.length + defaultValue.length

    fun annotation(name: String?) {
        if (!name.isNullOrBlank()) {
            annotations += "@$name"
            context.reportType(name)
        }
    }

    fun mutable() = apply { modifier = "var" }
    fun immutable() = apply { modifier = "val" }

    fun type(name: String, nullable: Boolean = false) = apply {
        type = name
        if (nullable) {
            type += "?"
        }
        context.reportType(name)
    }

    fun defaultNull() = apply { defaultValue = "null" }

    fun defaultValue(init: StatementBuilder.() -> Unit) = apply { default = StatementBuilder().apply(init) }

    fun defaultValue(name: String, init: CallBuilder.() -> Unit) {
        defaultValue = CallBuilder(name, indent.next).apply(init).build()
        context.reportType(name)
    }

    fun build() = buildList {
        addAll(annotations)
        this += buildString {
            if (modifier.isNotBlank()) append("$modifier ")
            append(name)
            if (type.isNotBlank()) append(": $type")
            if (defaultValue.isNotBlank()) append(" = $defaultValue\n")
            if (default != null) append(" = ${default!!.build()}")
        }
    }
}
