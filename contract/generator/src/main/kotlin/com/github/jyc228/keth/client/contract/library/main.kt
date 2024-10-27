package com.github.jyc228.keth.client.contract.library

import com.github.jyc228.keth.solidity.AbiItem
import com.github.jyc228.keth.solidity.ContractBuilder
import com.github.jyc228.keth.solidity.compile.CompileResult
import java.io.File
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream

fun main(args: Array<String>) {
    val targetDir = "com/github/jyc228/keth/client/contract/library"
    val dir = File(args[0], targetDir)
    File(requireNotNull(ContractBuilder::class.java.getResource("/abi")).toURI()).listFiles()?.forEach { file ->
        val abi = Json.decodeFromStream<List<AbiItem>>(file.inputStream())
        val builder = ContractBuilder(
            targetDir.replace('/', '.'),
            CompileResult(file.name.removeSuffix(".abi"), abi, "")
        )
        File(dir, file.name.replace(".abi", ".kt")).writeText(builder.buildInterface().build())
        File(dir, file.name.replace(".abi", "Impl.kt")).writeText(builder.buildDefaultImplementation().build())
    }
}