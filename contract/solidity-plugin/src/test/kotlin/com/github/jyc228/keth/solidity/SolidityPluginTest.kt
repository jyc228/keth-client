package com.github.jyc228.keth.solidity

import io.kotest.matchers.sequences.shouldHaveAtLeastSize
import java.io.File
import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir

class SolidityPluginTest {
    @TempDir
    lateinit var testProjectDir: File

    @Test
    fun `test generateKotlinContractWrapper`() {
        testProjectDir.child("build.gradle.kts").writeText(buildFileContent())
        testProjectDir.child("src/main/solidity").apply { mkdirs() }

        testInputFiles().forEach { it.copyTo(testProjectDir.child("src/main/solidity/${it.name}")) }

        GradleRunner.create()
            .withDebug(true)
            .withProjectDir(testProjectDir)
            .withPluginClasspath()
            .withArguments("generateKotlinContractWrapper")
            .build()

        testProjectDir.child("build").walkTopDown()
            .map { it.relativeTo(testProjectDir) }
            .filter { it.extension == "kt" }
            .onEach { println(it) } shouldHaveAtLeastSize 10
    }

    private fun buildFileContent() = """
       plugins {
            kotlin("jvm") version "2.0.0"
            id("com.github.jyc228.keth") version "1.0-SNAPSHOT"
        }
    """

    private fun testInputFiles(): Array<File> {
        val solidity = requireNotNull(SolidityPluginTest::class.java.getResource("/solidity")).toURI()
        return requireNotNull(File(solidity).listFiles())
    }

    private fun File.child(path: String) = File(this, path)
}
