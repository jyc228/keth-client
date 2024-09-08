package com.github.jyc228.kotlin.codegen

class InheritBuilder(val indent: Indent, private val context: GenerationContext) {
    private val inheritTargets = mutableListOf<Target>()

    fun `class`(name: String) = Class(name, indent.next).also {
        inheritTargets += it
        context.reportType(name)
    }

    fun `interface`(name: String) = Interface(name).also {
        inheritTargets += it
        context.reportType(name)
    }

    sealed class Target(val name: String) {
        protected val typeParameters = mutableListOf<String>()
        open fun build(): String {
            if (typeParameters.isEmpty()) return name
            return "$name<${typeParameters.joinToString(", ")}>"
        }
    }

    class Class(name: String, private val indent: Indent) : Target(name) {
        private val constructorParameters = mutableListOf<String>()
        fun typeParameter(type: String) = apply { typeParameters += type }
        fun invokeConstructor(vararg value: String) {
            constructorParameters.addAll(value)
        }

        override fun build(): String {
            if (constructorParameters.sumOf { it.length } >= 80) {
                val args = constructorParameters.joinToString(",\n") { "$indent$it" }
                return "${super.build()}(\n$args\n${indent.prev})"
            }
            return "${super.build()}(${constructorParameters.joinToString(", ")})"
        }
    }


    class Interface(name: String) : Target(name) {
        fun typeParameter(type: String) = apply { typeParameters += type }
    }

    fun build(): String {
        if (inheritTargets.size >= 3) {
            val remain = inheritTargets.drop(1).joinToString(",\n") { "${indent.next}${it.build()}" }
            return ": ${inheritTargets[0].build()},\n$remain"
        }
        return ": ${inheritTargets.joinToString(", ") { it.build() }}"
    }
}