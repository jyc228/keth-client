package com.github.jyc228.keth.solidity

import com.github.jyc228.keth.solidity.compile.CompileResult
import io.kotest.core.spec.style.StringSpec
import io.kotest.engine.spec.tempdir
import java.io.File
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class ContractBuilderTest : StringSpec({
    val outputDir = tempdir()
    finalizeSpec { outputDir.delete() }

    suspend fun compile(file: String) = withContext(Dispatchers.IO) {
        val solidityFile = File(requireNotNull(this.javaClass.getResource(file)).file)
        val process = ProcessBuilder()
            .command("solc", solidityFile.absolutePath, "--abi", "--bin", "-o", outputDir.absolutePath)
            .start()
        process.waitFor(1, TimeUnit.MINUTES)
    }

    fun readFileContent(file: String): String = File(outputDir.absolutePath + file).readText()

    fun newContractBuilder(contractName: String): ContractBuilder {
        val abi = Json.decodeFromString<List<AbiItem>>(readFileContent("/$contractName.abi"))
        val bin = readFileContent("/$contractName.bin")
        return ContractBuilder("com.github.jyc228", CompileResult(contractName, abi, bin))
    }

    "buildInterface no constructor" {
        compile("/constructorTest/noConstructor.sol")
        val gen = newContractBuilder("noConstructor")
        println(gen.buildInterface().build())
    }

    "buildInterface with constructor1" {
        compile("/constructorTest/param1.sol")
        val gen = newContractBuilder("param1")
        println(gen.buildInterface().build())
    }

    "buildInterface with constructor2" {
        compile("/constructorTest/param2.sol")
        val gen = newContractBuilder("param2")
        println(gen.buildInterface().build())
    }

    "buildInterface with constructor3" {
        compile("/constructorTest/structParam.sol")
        val gen = newContractBuilder("structParam")
        println(gen.buildInterface().build())
    }

    "build erc20" {
        compile("/erc/ERC20.sol")
        val gen = newContractBuilder("ERC20")
        println(gen.buildInterface().build())
        println(gen.buildDefaultImplementation().build())
    }

    "build event" {
        compile("/EventTest.sol")
        val gen = newContractBuilder("EventTest")
        println(gen.buildInterface().build())
        println(gen.buildDefaultImplementation().build())
    }

    "build full test" {
        compile("/FullTest.sol")
        val builder = newContractBuilder("FullTest")
        println(builder.buildInterface().build())
        println(builder.buildDefaultImplementation().build())
        LibraryBuilder(
            "",
            builder.compileResult.topLevelStructures().associateBy { it.resolveStruct().name }.toMutableMap()
        ).build("_Struct", null).build().let { println(it) }
    }
})
