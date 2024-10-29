package com.github.jyc228.keth.solidity

import com.github.jyc228.keth.solidity.compile.CompileResult
import java.io.File

data class ContractGenerator(
    private val abiRootDir: File,
    private val outputRootDir: File,
    private val genLibraryByFullName: MutableMap<String, LibraryBuilder> = mutableMapOf()
) {
    fun generate() {
        abiRootDir.walkTopDown().filter { it.extension == "abi" }.forEach { file -> generate(file) }
        genLibraryByFullName.toList().forEach { (fullName, gen) -> generateLibrary(fullName, gen) }
    }

    private fun generate(abiFile: File) {
        val binFile = abiFile.resolveSibling("${abiFile.nameWithoutExtension}.bin")
        val generator = ContractBuilder(
            packagePath = abiFile.relativeTo(abiRootDir).parent?.replace("/", ".") ?: "",
            compileResult = CompileResult(
                abiFile.name.replace(".abi", ""),
                AbiItem.listFromJsonStream(abiFile.inputStream()),
                if (binFile.exists()) binFile.readText() else ""
            )
        )
        val parentDirectory = File(outputRootDir, generator.packagePath.replace(".", "/"))
        generator.buildInterface().write(parentDirectory)
        generator.buildDefaultImplementation().write(parentDirectory)
        generator.compileResult.topLevelStructures().forEach { io ->
            val struct = io.resolveStruct()
            val gen =
                genLibraryByFullName.getOrPut("${generator.packagePath}._Struct") { LibraryBuilder(generator.packagePath) }
            gen.abiIOByName[struct.name] = io
        }
        generator.compileResult.externalStructures().forEach { io ->
            val struct = io.resolveStruct()
            val gen = genLibraryByFullName.getOrPut("${generator.packagePath}.${struct.ownerName}") {
                LibraryBuilder(generator.packagePath)
            }
            gen.abiIOByName[struct.name] = io
        }
    }

    private fun generateLibrary(fullName: String, gen: LibraryBuilder) {
        val path = fullName.split(".")
        val dir = File(outputRootDir, path.dropLast(1).joinToString("/"))
        gen.build(path.last(), path.last().takeIf { it != "_Struct" }).write(dir)
    }
}
