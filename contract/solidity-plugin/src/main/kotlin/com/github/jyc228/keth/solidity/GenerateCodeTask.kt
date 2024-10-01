package com.github.jyc228.keth.solidity

import java.io.File
import org.gradle.api.internal.file.FileTreeInternal
import org.gradle.api.internal.tasks.compile.CompilationSourceDirs
import org.gradle.api.tasks.SourceTask
import org.gradle.api.tasks.TaskAction

open class GenerateCodeTask : SourceTask() {

    @TaskAction
    fun execute() {
        val solidityRoot = CompilationSourceDirs.inferSourceRoots(this.source as FileTreeInternal).single()
        val generator = ContractGenerator(solidityRoot, outputs.files.singleFile)
        generator.generate()
        if (source.filter { it.extension == "sol" }.onEach { file -> compile(file, solidityRoot) }.files.isNotEmpty()) {
            generator.copy(abiRootDir = outputs.files.singleFile).generate()
        }
    }

    private fun compile(
        file: File,
        srcRoot: File,
    ) {
        val outputDir = file.relativeTo(srcRoot).parent ?: ""
        if (!File(outputs.files.singleFile, "$outputDir/${file.nameWithoutExtension}.bin").exists()) {
            val compileOutput = File(outputs.files.singleFile, outputDir)
            project.exec {
                commandLine("solc", file.absoluteFile, "--bin", "--abi", "-o", compileOutput.absoluteFile)
            }
        }
    }
}
