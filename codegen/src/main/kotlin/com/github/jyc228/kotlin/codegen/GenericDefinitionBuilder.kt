package com.github.jyc228.kotlin.codegen

class GenericDefinitionBuilder {
    private val parameters = mutableListOf<Parameter>()
    fun parameter(name: String, upperBound: String? = null) = apply { parameters += Parameter(name, upperBound) }
    fun build(): String = "<${parameters.joinToString(", ") { it.build() }}>"

    data class Parameter(val name: String, val upperBound: String?) {
        fun build() = when (upperBound.isNullOrBlank()) {
            true -> name
            false -> "$name : $upperBound"
        }
    }
}
