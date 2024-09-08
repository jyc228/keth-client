package com.github.jyc228.kotlin.codegen

class TypeBuilder(private val indent: Indent, val context: GenerationContext) {
    private var kind = ""
    private var name = ""
    private var generic: GenericDefinitionBuilder? = null
    private var constructor: ConstructorBuilder? = null
    private var inherit: InheritBuilder? = null
    private var body: BodyBuilder? = null

    fun `class`(name: String) = apply {
        kind = "class"
        this.name = name
    }

    fun `interface`(name: String) = apply {
        kind = "interface"
        this.name = name
    }

    fun `object`(name: String) = apply {
        kind = "object"
        this.name = name
    }

    fun sealedInterface(name: String) = apply {
        kind = "sealed interface"
        this.name = name
    }

    fun dataClass(name: String) = apply {
        kind = "data class"
        this.name = name
    }

    fun companionObject(name: String? = null) = apply {
        kind = "companion object"
        this.name = name ?: ""
    }

    fun generic(init: GenericDefinitionBuilder.() -> Unit) = apply {
        generic = GenericDefinitionBuilder().apply(init)
    }

    fun constructor(init: ConstructorBuilder.() -> Unit) = apply {
        constructor = ConstructorBuilder(indent, context).apply(init)
    }

    fun inherit(init: InheritBuilder.() -> Unit) = apply {
        val builder = InheritBuilder(indent, context)
        inherit = builder.apply(init)
    }

    fun body(init: BodyBuilder.() -> Unit) = apply {
        body = BodyBuilder(indent.next, context).apply(init)
    }

    fun build() = buildString {
        append(kind)
        if (name.isNotBlank()) append(" $name")
        if (generic != null) append(generic!!.build())
        if (constructor != null) append(constructor!!.build())
        if (inherit != null) append(" ${inherit!!.build()}")
        if (body != null) append(" ${body!!.build()}")
    }
}