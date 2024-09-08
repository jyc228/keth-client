package com.github.jyc228.kotlin.codegen

@JvmInline
value class Indent(private val depth: Int) {
    private val step get() = 4
    val prev get() = Indent(depth - 1)
    val next get() = Indent(depth + 1)
    override fun toString(): String = " ".repeat(depth * step)
}