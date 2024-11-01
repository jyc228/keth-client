package com.github.jyc228.keth.client.contract.library

import com.github.jyc228.keth.solidity.AbiItem
import com.github.jyc228.keth.solidity.ContractBuilder
import com.github.jyc228.keth.solidity.compile.CompileResult
import java.io.File

fun main(args: Array<String>) {
    val targetDir = "com/github/jyc228/keth/client/contract/library"
    val dir = File(args[0], targetDir)
    File(requireNotNull(ContractBuilder::class.java.getResource("/abi")).toURI()).listFiles()?.forEach { file ->
        val builder = ContractBuilder(
            targetDir.replace('/', '.'),
            CompileResult(file.name.removeSuffix(".abi"), AbiItem.listFromJsonStream(file.inputStream()), "")
        )
        File(dir, file.name.replace(".abi", ".kt")).writeText(builder.buildInterface().build())
        File(dir, file.name.replace(".abi", "Impl.kt")).writeText(builder.buildDefaultImplementation().build())
    }
}