package com.github.jyc228.keth.solidity

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.configurationcache.extensions.capitalized
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.getByType

class SolidityPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.extensions.getByType<SourceSetContainer>().forEach { config(target, it) }
    }

    private fun config(target: Project, sourceSet: SourceSet) {
        val set = target.objects.sourceDirectorySet("sol", "solidity")
        set.srcDir("src/${sourceSet.name}/solidity")
        set.include("**/*.sol", "**/*.abi", "**/*.bin")
        sourceSet.resources.srcDirs(set)

        val output = target.layout.buildDirectory.dir("kotlinContractWrapper/${sourceSet.name}")
        sourceSet.java.srcDirs(output)

        val taskName = if (sourceSet.name == "main") "" else sourceSet.name.capitalized()
        target.tasks.create<GenerateCodeTask>("generate${taskName}KotlinContractWrapper") {
            source = set
            solidityRoot = set.srcDirs.single()
            outputs.dir(output)
            target.tasks.getByName("compileKotlin").dependsOn(this)
        }
    }
}
