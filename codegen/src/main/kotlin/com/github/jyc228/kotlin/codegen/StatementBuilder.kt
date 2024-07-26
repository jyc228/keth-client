package com.github.jyc228.kotlin.codegen

class StatementBuilder(var code: String = "") {

    val statements = mutableListOf<StatementBuilder>()

    fun string(value: String) {
        code = "\"$value\""
    }

    fun stringTemplate(value: String) {
        code = "\"\"\"$value\"\"\""
    }

    fun variable() {

    }

    fun `class`(name: String) = apply { code += name }
    fun `interface`(name: String) = apply { code += name }
    fun reflection(target: String) = apply { code = "$code::$target" }

    fun invoke(vararg buildStatement: StatementBuilder.() -> Unit) {
        statements += StatementBuilder("(")
        buildStatement.forEach { statements += StatementBuilder().apply(it) }
        statements += StatementBuilder(")")
    }

    fun build() = buildString {
        var call = false
        statements.forEach {
            if (!call && it.code == "(") {
                call = true
            } else if (call && it.code == ")") {
                call = false
            }
            append(it.code)
            if (call) {
                append(",")
            }
        }
    }

    override fun toString(): String = code
}