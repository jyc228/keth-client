package com.github.jyc228.keth.solidity

import com.github.jyc228.keth.solidity.compile.CompileResult
import io.kotest.core.spec.style.StringSpec
import io.kotest.engine.spec.tempdir
import java.io.File
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class ContractGeneratorTest : StringSpec({
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

    fun newContractGenerator(contractName: String): ContractGenerator {
        val abi = Json.decodeFromString<List<AbiItem>>(readFileContent("/$contractName.abi"))
        val bin = readFileContent("/$contractName.bin")
        return ContractGenerator("com.github.jyc228", CompileResult(contractName, abi, bin))
    }

    "generateInterface no constructor" {
        compile("/constructorTest/noConstructor.sol")
        val gen = newContractGenerator("noConstructor")
        println(gen.generateInterface().build())
    }

    "generateInterface with constructor1" {
        compile("/constructorTest/param1.sol")
        val gen = newContractGenerator("param1")
        println(gen.generateInterface().build())
    }

    "generateInterface with constructor2" {
        compile("/constructorTest/param2.sol")
        val gen = newContractGenerator("param2")
        println(gen.generateInterface().build())
    }

    "generateInterface with constructor3" {
        compile("/constructorTest/structParam.sol")
        val gen = newContractGenerator("structParam")
        println(gen.generateInterface().build())
    }

    "generate erc20" {
        compile("/erc/ERC20.sol")
        val gen = newContractGenerator("ERC20")
        println(gen.generateInterface().build())
        println(gen.generateDefaultImplementation().build())
    }

    "generate full test" {
        compile("/FullTest.sol")
        val gen = newContractGenerator("FullTest")
        println(gen.generateInterface().build())
        println(gen.generateDefaultImplementation().build())
        LibraryGenerator(
            "",
            gen.compileResult.topLevelStructures().associateBy { it.resolveStruct().name }.toMutableMap()
        ).generate("_Struct", null).build().let { println(it) }
    }
})
