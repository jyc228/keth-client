package com.github.jyc228.keth.solidity

import io.kotest.matchers.sequences.shouldHaveAtLeastSize
import io.kotest.matchers.sequences.shouldHaveSize
import java.io.File
import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir

class SolidityPluginTest {
    @TempDir
    lateinit var testProjectDir: File

    @Test
    fun `test solc version`() {
        testProjectDir.child("build.gradle.kts")
            .writeText(buildFileContent("""solidity { solcVersion = "0.8.25" }"""))

        val result = runGradleTask("configSolc")

        println(result.output)
    }

    @Test
    fun `test generateKotlinContractWrapper`() {
        testProjectDir.child("build.gradle.kts").writeText(buildFileContent())
        testProjectDir.child("src/main/solidity").apply { mkdirs() }

        testInputFiles().forEach { it.copyTo(testProjectDir.child("src/main/solidity/${it.name}")) }

        runGradleTask("generateKotlinContractWrapper")

        testProjectDir.child("build").walkTopDown()
            .map { it.relativeTo(testProjectDir) }
            .filter { it.extension == "kt" }
            .onEach { println(it) } shouldHaveAtLeastSize 10

        testProjectDir.child("build").walkTopDown()
            .map { it.relativeTo(testProjectDir) }
            .filter { it.name.startsWith("Storage") && it.extension == "kt" } shouldHaveSize 0
    }

    @Test
    fun `test generateKotlinContractWrapper with solc`() {
        testProjectDir.child("build.gradle.kts")
            .writeText(buildFileContent("""solidity { solcVersion = "0.8.25" }"""))
        testProjectDir.child("src/main/solidity").apply { mkdirs() }

        testInputFiles().forEach { it.copyTo(testProjectDir.child("src/main/solidity/${it.name}")) }

        runGradleTask("generateKotlinContractWrapper").let { println(it.output) }

        testProjectDir.child("build").walkTopDown()
            .map { it.relativeTo(testProjectDir) }
            .filter { it.name.startsWith("Storage") && it.extension == "kt" }
            .onEach { println(it) } shouldHaveSize 2
    }

    private fun runGradleTask(vararg args: String) = GradleRunner.create()
        .withDebug(true)
        .withProjectDir(testProjectDir)
        .withPluginClasspath()
        .withArguments(*args)
        .build()

    private fun buildFileContent(content: String = "") = """
       plugins {
            kotlin("jvm") version "2.0.0"
            id("com.github.jyc228.keth") version "1.0-SNAPSHOT"
        }
        $content
    """

    private fun testInputFiles(): Array<File> {
        val solidity = requireNotNull(SolidityPluginTest::class.java.getResource("/solidity")).toURI()
        return requireNotNull(File(solidity).listFiles())
    }

    private fun File.child(path: String) = File(this, path)
}
