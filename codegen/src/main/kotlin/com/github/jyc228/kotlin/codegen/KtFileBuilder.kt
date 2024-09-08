package com.github.jyc228.kotlin.codegen

import java.io.File

class KtFileBuilder(val context: GenerationContext, val name: String, val packagePath: String) {
    private val indent: Indent = Indent(0)
    private val types = mutableListOf<TypeBuilder>()

    fun type() = TypeBuilder(indent, context).also { types += it }

    fun build() = buildString {
        if (packagePath.isNotBlank()) appendLine("package $packagePath")
        appendLine()
        context.fullPaths().forEach { appendLine("import $it") }
        if (context.reportedTypes.isNotEmpty()) {
            appendLine()
        }
        types.forEach {
            appendLine(it.build())
            appendLine()
        }
        if (endsWith("\n")) {
            deleteCharAt(lastIndexOf("\n"))
        }
    }

    fun write(parentDirectory: File) {
        parentDirectory.mkdirs()
        File(parentDirectory, "${name}.kt").writeText(build())
    }
}
