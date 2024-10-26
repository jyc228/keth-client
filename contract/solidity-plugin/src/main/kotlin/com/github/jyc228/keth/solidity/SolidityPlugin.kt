package com.github.jyc228.keth.solidity

import java.io.ByteArrayOutputStream
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.configurationcache.extensions.capitalized
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.register

class SolidityPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        val extension = target.extensions.create<SolidityPluginExtension>("solidity")

        target.tasks.register("configSolc") {
            if (configSolc(target, extension.solcVersion)) println("solc ${extension.solcVersion}")
        }

        target.extensions.getByType<SourceSetContainer>().forEach { sourceSet ->
            val generateCodeTask = target.tasks.register<GenerateCodeTask>(taskName(sourceSet)) {
                source = createSoliditySourceSet(target, sourceSet, extension)
                outputs.dir(target.layout.buildDirectory.dir("kotlinContractWrapper/${sourceSet.name}"))
                sourceSet.java.srcDirs(outputs)
            }
            target.tasks.getByName("compileKotlin").dependsOn(generateCodeTask)
        }
    }

    private fun taskName(sourceSet: SourceSet): String {
        val taskName = if (sourceSet.name == "main") "" else sourceSet.name.capitalized()
        return "generate${taskName}KotlinContractWrapper"
    }

    private fun createSoliditySourceSet(
        target: Project,
        sourceSet: SourceSet,
        extension: SolidityPluginExtension
    ): SourceDirectorySet {
        return target.objects.sourceDirectorySet("sol", "solidity").apply {
            srcDir("src/${sourceSet.name}/solidity")
            include("**/*.abi", "**/*.bin")
            if (extension.solcVersion.isNotBlank()) include("**/*.sol")
            sourceSet.resources.srcDirs(this)
        }
    }

    private fun configSolc(target: Project, version: String): Boolean {
        if (version.isBlank()) return false
        if (version in target.execCommand("solc", "--version")) return true

        val installed = target.execCommand("solc-select", "versions")
        when {
            installed.startsWith("error: ") -> error("solc-select not installed. can not configuration solc $version")
            version in installed -> target.execCommand("solc-select", "use", version)
            else -> {
                target.logger.info("solc $version is not installed.")
                target.execCommand("solc-select", "install", version)
                target.execCommand("solc-select", "use", version)
            }
        }
        return true
    }

    private fun Project.execCommand(command: String, vararg args: String) = ByteArrayOutputStream().use {
        val result = exec { commandLine(command).args(*args).setStandardOutput(it).setErrorOutput(it) }
        when (result.exitValue == 0) {
            true -> it.toString()
            false -> "error: $it"
        }
    }
}
