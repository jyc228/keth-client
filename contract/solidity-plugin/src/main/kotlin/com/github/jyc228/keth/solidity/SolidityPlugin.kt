package com.github.jyc228.keth.solidity

import java.io.File
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.SourceSet
import org.gradle.configurationcache.extensions.capitalized
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.getByType

class SolidityPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.pluginManager.apply("java")
        target.pluginManager.apply("org.jetbrains.kotlin.jvm")
        val sourceSets = target.extensions.getByType<JavaPluginExtension>().sourceSets
        target.afterEvaluate {
            sourceSets.forEach { config(target, it) }
        }
    }

    private fun config(target: Project, sourceSet: SourceSet) {
        val set = target.objects.sourceDirectorySet("solidity", "solidity")
        set.srcDir("src/${sourceSet.name}/solidity")
        set.include("**/*.sol", "**/*.abi", "**/*.bin")
        val output = File("${target.buildDir.absoluteFile}/generated/kotlinContractWrapper/${sourceSet.name}")
        val taskName = if (sourceSet.name == "main") "" else sourceSet.name.capitalized()
        target.tasks.create<GenerateCodeTask>("generate${taskName}KotlinContractWrapper") {
            source = set
            solidityRoot = set.srcDirs.single()
            outputs.dir(output)
            target.tasks.getByName("compileKotlin").dependsOn(this)
        }
    }
}
