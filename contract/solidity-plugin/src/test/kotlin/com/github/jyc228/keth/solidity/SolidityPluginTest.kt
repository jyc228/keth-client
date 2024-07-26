package com.github.jyc228.keth.solidity

import java.io.File
import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir

class SolidityPluginTest {
    @TempDir
    lateinit var testProjectDir: File

    @Test
    fun testX() {
        File(testProjectDir, "build.gradle.kts").writeText(buildFileContent())
        val testSoliditySrcDir = File(testProjectDir, "src/main/solidity").apply { mkdirs() }
        val testInputAbiFileDir = File(testSoliditySrcDir, "").apply { mkdirs() }
//        val testInputFiles = File("src/test/solidity").listFiles()!!
        val testInputFiles = listOf(File("src/test/solidity/Storage.sol"))
        testInputFiles.forEach { it.copyTo(File(testInputAbiFileDir, it.name)) }

        val buildResult = GradleRunner.create()
            .withDebug(true)
            .withProjectDir(testProjectDir)
            .withPluginClasspath()
//            .withArguments("GenerateContractWrapper")
            .withArguments("generateKotlinContractWrapper")
            .build()

        println(buildResult.output)

        testProjectDir.walkTopDown().forEach { println(it.relativeTo(testProjectDir)) }
    }

    private fun buildFileContent() = """
       plugins {
            kotlin("jvm") version "2.0.0"
            id("com.github.jyc228.keth") version "1.0-SNAPSHOT"
        }
    """
}
