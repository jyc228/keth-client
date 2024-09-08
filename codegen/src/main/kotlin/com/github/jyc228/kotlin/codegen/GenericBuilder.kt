package com.github.jyc228.kotlin.codegen

class GenericBuilder {
    val types = mutableListOf<String>()
    fun type(t: String) = apply { types += t }
    fun build(): String = "<${types.joinToString(", ")}>"
}
